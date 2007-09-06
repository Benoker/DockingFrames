package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;

import bibliothek.extension.gui.dock.theme.eclipse.rex.resources.Resources;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.RectGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.ShapedGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.Tab;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabListener;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.util.container.Tuple;


/**
 * @author Janni Kovacs
 */
public class RexTabbedComponent extends JComponent {
	private class FocusEventListener implements AWTEventListener {
		public void eventDispatched(AWTEvent event) {
			FocusEvent fe = (FocusEvent) event;
			RexTabbedComponent instance = RexTabbedComponent.this;
			Component c1 = fe.getOppositeComponent();
			Component c2 = fe.getComponent();
			//	System.out.println(c2.getClass().getName() + (fe.getID() == FocusEvent.FOCUS_GAINED ? " GAINED focus" : " LOST focus"));
			//	System.out.println("comp: "+ c2);
			if (((c1 != null) && SwingUtilities.isDescendingFrom(c1, instance)) ||
					((c2 != null) && SwingUtilities.isDescendingFrom(c2, instance))) {
				//		System.out.println("Owner: "+FocusManager.getCurrentManager().getFocusOwner());
				tabStrip.repaint();
			}
		}
	}

	protected class TabStrip extends JComponent implements MouseListener, MouseMotionListener {

		private Map<Tab, Tuple<Dimension, Insets>> sizes = new LinkedHashMap<Tab, Tuple<Dimension, Insets>>();
		private boolean closeIconOnHover = false;
		private int tabOnHover = -1;

		public TabStrip() {
			setLayout(null);
			setFocusable(true);
			addMouseListener(this);
			addMouseMotionListener(this);
			ToolTipManager.sharedInstance().registerComponent(this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
		//	Thread.dumpStack();
			int x = 0;
			int index = 0;
			Insets insets = new Insets(0, 0, 0, 0);
			sizes.clear();
			for (Tab tab : tabs) {
				boolean isSelected = tab == selectedTab;
				Component focusOwner = FocusManager.getCurrentManager().getPermanentFocusOwner();
				RexTabbedComponent comp = RexTabbedComponent.this;
				boolean hasFocus = focusOwner != null && SwingUtilities.isDescendingFrom(focusOwner, comp);
				JComponent c = tabPainter.getTabComponent(comp, tab, index, isSelected, hasFocus);
				Dimension size = c.getPreferredSize();
				insets = c.getInsets();
				int width = size.width;
				Icon icon = closeIcon;
				Point pos = getMousePosition();
				if (tabOnHover == index && closeIconOnHover) {
					icon = hoverCloseIcon;
				}
				if (tab.isClosable()) {
					width += icon.getIconWidth() + 4;
				}
				SwingUtilities.paintComponent(g, c, this, x, 0, width + insets.right, size.height);
				if (tab.isClosable() && ((!showCloseIconOnHoverOnly || tabOnHover == index) || isSelected)) {
					icon.paintIcon(this, g, x + width - icon.getIconWidth() - 4,
							(size.height - icon.getIconHeight()) / 2);
				}
				width += insets.right - insets.left;
				size.width = width;
				Tuple<Dimension, Insets> t = new Tuple<Dimension, Insets>(size, insets);
				sizes.put(tab, t);
				if (size.height != getHeight()) {
					revalidate();
				}
				x += width;// + insets.right - insets.left;
				index++;
			}
			Border oldBorder = contentArea.getBorder();
			Border newBorder = null;
			if (index != 0) {
				newBorder = tabPainter.getContentBorder();
			}
			if (oldBorder != newBorder) {
				contentArea.setBorder(newBorder);
				contentArea.repaint();
			}
			g.setClip(0, 0, getWidth(), getHeight());
			tabPainter.paintTabStrip(RexTabbedComponent.this, g);
		}

		public void mouseExited(MouseEvent e) {
			if (closeIconOnHover || tabOnHover != -1) {
				closeIconOnHover = false;
				tabOnHover = -1;
				repaint();
			}
		}

		public void mouseClicked(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			requestFocus();
			Tab tab = getTabAt(e.getX(), e.getY());
			if (tab != null && tab != selectedTab && !closeIconOnHover)
				setSelectedTab(tab);
			showPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			requestFocus();
			showPopup(e);
			if (closeIconOnHover && !e.isPopupTrigger()) {
				Tab t = RexTabbedComponent.this.getTabAt(tabOnHover);
				// NOTE: for use in docking frames removeTab() has been replaced with  tabCloseIconClicked() call
				for (TabListener listener : listeners) {
					listener.tabCloseIconClicked(t);
				}
//				removeTab(t);
				closeIconOnHover = false;
			}
		}

		private void showPopup(MouseEvent e) {
			if (e.isPopupTrigger()) {
				Tab tab = getTabAt(e.getX(), e.getY());
				if (tab != null) {
					JComponent comp = tabPainter.getTabComponent(RexTabbedComponent.this, tab, indexOf(tab), true, true);
					JPopupMenu popup = comp.getComponentPopupMenu();
					if (popup != null) {
						Point location = comp.getPopupLocation(e);
						if (location == null) {
							location = e.getPoint();
						}
						popup.show(this, location.x, location.y);
					}
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public String getToolTipText(MouseEvent event) {
			Tab tab = getTabAt(event.getX(), event.getY());
			return tab != null ? tab.getTooltip() : super.getToolTipText(event);
		}

		public void mouseDragged(MouseEvent e) {
		}

		public void mouseMoved(MouseEvent event) {
			int x = event.getX(), y = event.getY();
			int width = 0, currentTab = -1;
			Tab tab = null;
			int i = 0;
			for (Entry<Tab, Tuple<Dimension, Insets>> e : sizes.entrySet()) {
				width += e.getValue().getA().width;
				if (x <= width) {
					tab = e.getKey();
					currentTab = i;
					break;
				}
				i++;
			}
			boolean shouldRepaint = false;
			if (tabOnHover != currentTab) {
				tabOnHover = currentTab;
				shouldRepaint = true;
			}
			if (tab != null) {
				if (tab.isClosable()) {
					Tuple<Dimension, Insets> tuple = sizes.get(tab);
					Dimension d = tuple.getA();
					Insets ins = tuple.getB();
					int closeIconY = (d.height - closeIcon.getIconHeight()) / 2;
					int closeIconX = width - 4 - (ins.right - ins.left) - closeIcon.getIconWidth();
					if (x > closeIconX && x < closeIconX + closeIcon.getIconWidth() &&
							y > closeIconY && y < d.height - closeIconY) {
						if (!closeIconOnHover || shouldRepaint) {
							closeIconOnHover = true;
							repaint();
						}
						return;
					}
				}
			}
			if (closeIconOnHover) {
				closeIconOnHover = false;
				shouldRepaint = true;
			}
			if (shouldRepaint)
				repaint();
		}

		public Tab getTabAt(int x, int y) {
			int width = 0;
			for (Entry<Tab, Tuple<Dimension, Insets>> e : sizes.entrySet()) {
				width += e.getValue().getA().width;
				if (x <= width) {
					return e.getKey();
				}
			}
			return null;
		}

		@Override
		public Dimension getPreferredSize() {
			int width = 0;
			int height = 1;
			for (Tuple<Dimension, Insets> t : sizes.values()) {
				Dimension dimension = t.getA();
				width += dimension.width;
				height = Math.max(height, dimension.height);
			}
			return new Dimension(width, height);
		}
	}

	public static final String X_OVERLAY_PROPERTY = "RexTabbedComponent.xOverlay";

	private RexTabbedComponent.FocusEventListener focusListener = new FocusEventListener();
	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	protected int tabPlacement;
	protected Icon closeIcon = Resources.getIcon("close-tab.png");
	protected Icon hoverCloseIcon = Resources.getIcon("close-tab-hover.png");
	protected boolean showCloseIconOnHoverOnly = true;
	protected TabStrip tabStrip;
	private Tab selectedTab;
	private TabPainter tabPainter;
	private List<TabListener> listeners = new LinkedList<TabListener>();
	private List<Tab> tabs = new ArrayList<Tab>();
	private JComponent contentArea;

	public RexTabbedComponent() {
		this(TOP);
	}

	public RexTabbedComponent(int tabPlacement) {
		if (!(tabPlacement == TOP || tabPlacement == BOTTOM || tabPlacement == LEFT || tabPlacement == RIGHT)) {
			throw new IllegalArgumentException("Wrong tabPlacement Property");
		}
		this.tabPlacement = tabPlacement;
		initComponent();
	}


	@Override
	public void addNotify() {
		super.addNotify();
		Toolkit.getDefaultToolkit().addAWTEventListener(focusListener, FocusEvent.FOCUS_EVENT_MASK);
	}

	@Override
	public void removeNotify() {
		super.removeNotify();
		Toolkit.getDefaultToolkit().removeAWTEventListener(focusListener);
	}

	private void initComponent() {
		tabStrip = new TabStrip();
		//	tabStrip.addTabListener(this);
		contentArea = new JPanel(new BorderLayout());
//		contentArea.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, SystemColor.controlShadow));
//		tabStrip.setBorder(BorderFactory.createMatteBorder(tabPlacement == BOTTOM ? 1 : 0, 1,
//				tabPlacement == TOP ? 1 : 0, 1, SystemColor.controlShadow));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, SystemColor.controlShadow));
		setLayout(new BorderLayout());
		String constraints = BorderLayout.NORTH;
		switch (tabPlacement) {
			case BOTTOM:
				constraints = BorderLayout.SOUTH;
				break;
			case LEFT:
				constraints = BorderLayout.WEST;
				break;
			case RIGHT:
				constraints = BorderLayout.EAST;
		}
		add(tabStrip, constraints);
		add(contentArea, BorderLayout.CENTER);
		setTabPainter(new ShapedGradientPainter());
	}

	public void setTabPainter(TabPainter painter) {
		this.tabPainter = painter;
		contentArea.setBorder(painter.getContentBorder());
		tabStrip.repaint();
	}

	public void addTab(Tab t) {
		insertTab(t, tabs.size());
		setSelectedTab(t);
	}

	public void insertTab(Tab tab, int index) {
		tabs.add(index, tab);
		tabStrip.repaint();
	}

	public int getTabCount() {
		return tabs.size();
	}

	public int getTabPlacement() {
		return tabPlacement;
	}

	public Tab getSelectedTab() {
		return selectedTab;
	}

	public int indexOf(Tab t) {
		return tabs.indexOf(t);
	}

	public void removeTab(Tab t) {
		for (TabListener listener : listeners) {
			listener.tabRemoved(t);
		}
		int index = indexOf(t);
		tabs.remove(t);
		tabStrip.sizes.remove(t);
		if (t == selectedTab) {
			if (index == tabs.size()) {
				index = tabs.size() - 1;
			}
			if (index >= 0 && index < tabs.size()) {
				setSelectedTab(index);
			} else {
				setSelectedTab(null);
			}
		}
		tabStrip.repaint();
	}

	public void removeAllTabs() {
		for (Tab tab : tabs) {
			for (TabListener listener : listeners) {
				listener.tabRemoved(tab);
			}
		}
		tabs.clear();
		tabStrip.sizes.clear();
		tabStrip.repaint();
		setSelectedTab(null);
	}

	public Component getContentArea() {
		if (getSelectedTab() == null)
			return null;
		return contentArea.getComponent(0);
	}

	public void addTabListener(TabListener listener) {
		listeners.add(listener);
	}

	public void removeTabListener(TabListener listener) {
		listeners.remove(listener);
	}

	public int getSelectedIndex() {
		return indexOf(getSelectedTab());
	}

	public void setSelectedTab(int i) {
		setSelectedTab(tabs.get(i));
	}

	public void setSelectedTab(Tab tab) {
		if (tab != selectedTab) {
			selectedTab = tab;
			tabStrip.repaint();
			tabChanged(tab);
			for (TabListener listener : listeners) {
				listener.tabChanged(tab);
			}
		}
	}

	public Tab getTabAt(int index) {
		return tabs.get(index);
	}

	public Rectangle getBoundsAt(int index) {
		return getBounds(getTabAt(index));
	}

	public Rectangle getBounds(Tab tab) {
		Rectangle r = new Rectangle(tabStrip.sizes.get(tab).getA());
		if (getTabPlacement() == RexTabbedComponent.BOTTOM) {
			r.y = getHeight() - r.height;
		}
		for (Entry<Tab, Tuple<Dimension, Insets>> e : tabStrip.sizes.entrySet()) {
			if (e.getKey() == tab)
				break;
			r.x += e.getValue().getA().width;
		}
		return r;
	}

	protected void tabChanged(Tab t) {
		contentArea.removeAll();
		if (t == null) {
			contentArea.removeAll();
		} else {
			contentArea.add(t.getComponent(), BorderLayout.CENTER);
		}
		contentArea.revalidate();
		contentArea.repaint();
	}

	public Icon getCloseIcon() {
		return closeIcon;
	}

	public void setCloseIcon(Icon closeIcon) {
		this.closeIcon = closeIcon;
	}

	public Icon getHoverCloseIcon() {
		return hoverCloseIcon;
	}

	public void setHoverCloseIcon(Icon hoverCloseIcon) {
		this.hoverCloseIcon = hoverCloseIcon;
	}

	public boolean isShowCloseIconOnHoverOnly() {
		return showCloseIconOnHoverOnly;
	}

	public void setShowCloseIconOnHoverOnly(boolean showCloseIconOnHoverOnly) {
		this.showCloseIconOnHoverOnly = showCloseIconOnHoverOnly;
	}

	public static void main(String[] args) throws IllegalAccessException, UnsupportedLookAndFeelException,
			InstantiationException, ClassNotFoundException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final RexTabbedComponent comp = new RexTabbedComponent(RexTabbedComponent.TOP);
	//	comp.setTabPainter(new SmallTabPainter());
		comp.setTabPainter(new RectGradientPainter());
		comp.setTabPainter(new ShapedGradientPainter());
//		comp.setBorder(null);
		//	comp.addTab(new Tab("abc", new JLabel("oppa")));
		for (int i = 0; i < 10; i++) {
//			comp.addTab(new Tab(StringUtils.compose('x', i), null, "tooltip uscha: " + i, new JLabel("uscha " + i), true));
		}
		JFrame f = new JFrame("RexTabbedComponent Test");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(300, 300);
		f.setLocationRelativeTo(null);
		JSplitPane sp = new JSplitPane();
		JPanel p = new JPanel(new BorderLayout());
		p.add(comp);
		p.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		sp.setLeftComponent(new JButton(new AbstractAction("sizes") {

			public void actionPerformed(ActionEvent e) {
			}
		}));
		sp.setRightComponent(p);
		f.add(sp);
		//	f.add(new JTextArea("ugga"), BorderLayout.SOUTH);
		f.setVisible(true);
	}
}
