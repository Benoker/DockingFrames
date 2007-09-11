package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
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
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabComponent;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabListener;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionPopup;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.util.container.Tuple;


/**
 * @author Janni Kovacs
 */
public class RexTabbedComponent extends JComponent {

	public static final String X_OVERLAY_PROPERTY = "RexTabbedComponent.xOverlay";

	public static final int TOP = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int RIGHT = 3;

	protected int tabPlacement;
	protected JComponent tabStrip;
	private Tab selectedTab;
	private Tab focusedTab;
	private TabPainter tabPainter;
	private List<TabListener> listeners = new LinkedList<TabListener>();
	private List<TabEntry> tabs = new ArrayList<TabEntry>();
	private JComponent contentArea;
	
	private boolean paintIconsWhenInactive = false;

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

	private void initComponent() {
		setTabStrip( new RexTabStrip( this ));
		//	tabStrip.addTabListener(this);
		contentArea = new JPanel(new BorderLayout());
//		contentArea.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1, SystemColor.controlShadow));
//		tabStrip.setBorder(BorderFactory.createMatteBorder(tabPlacement == BOTTOM ? 1 : 0, 1,
//				tabPlacement == TOP ? 1 : 0, 1, SystemColor.controlShadow));
		setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, SystemColor.controlShadow));
		setLayout(new BorderLayout());
		
		add(contentArea, BorderLayout.CENTER);
		setTabPainter( ShapedGradientPainter.FACTORY );
	}

	public void setTabStrip( JComponent strip ){
		if( tabStrip != null ){
			remove( tabStrip );
			tabStrip.removeAll();
		}
		
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
		tabStrip = strip;
		add(strip, constraints);
		
		for( TabEntry entry : tabs ){
			tabStrip.add( entry.tabComponent.getComponent() );
		}
	}
	
	public void setTabPainter(TabPainter painter) {
		this.tabPainter = painter;
		
		tabStrip.removeAll();
		
		int index = 0;
		
		for( TabEntry entry : tabs ){
			entry.tabComponent.removeMouseListener( entry.tabMouseListener );
			entry.tab.setPainter( null );
			
			entry.tabComponent = painter.createTabComponent( this, entry.tab, index );
			entry.tabComponent.addMouseListener( entry.tabMouseListener );
			entry.tabComponent.setPaintIconWhenInactive( paintIconsWhenInactive );
			entry.tabComponent.setSelected( entry.tab == selectedTab );
			entry.tabComponent.setFocused( entry.tab == focusedTab );
			
			entry.tab.setPainter( entry.tabComponent );
			
			tabStrip.add( entry.tabComponent.getComponent() );
		}
		
		//contentArea.setBorder(painter.getContentBorder());
		tabStrip.repaint();
	}
	
	public TabPainter getTabPainter(){
		return tabPainter;
	}
	
	public void setPaintIconsWhenInactive( boolean paint ){
		this.paintIconsWhenInactive = paint;
		for( TabEntry entry : tabs )
			entry.tabComponent.setPaintIconWhenInactive( paint );
	}

	public void addTab(Tab t) {
		insertTab(t, tabs.size());
		setSelectedTab(t);
	}

	public void insertTab(Tab tab, int index) {
		TabEntry entry = new TabEntry();
		entry.tab = tab;
		entry.tabComponent = tabPainter.createTabComponent( this, tab, index );
		entry.tabMouseListener = new TabMouseListener( tab );

		entry.tabComponent.addMouseListener( entry.tabMouseListener );
		entry.tabComponent.setPaintIconWhenInactive( paintIconsWhenInactive );
		entry.tab.setPainter( entry.tabComponent );
		
		tabs.add( index, entry );
		tabStrip.add( entry.tabComponent.getComponent() );
		
		for( int i = index+1, n = tabs.size(); i<n; i++ )
			tabs.get( i ).tabComponent.setIndex( i );
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

	public TabComponent getTabComponent( int index ){
		return tabs.get( index ).tabComponent;
	}
	
	public int indexOf(Tab t) {
		for( int i = 0, n = tabs.size(); i<n; i++ ){
			if( tabs.get( i ).tab == t )
				return i;
		}
		
		return -1;
	}

	public void removeTab(Tab t) {
		for (TabListener listener : listeners) {
			listener.tabRemoved(t);
		}
		int index = indexOf(t);
		TabEntry entry = tabs.get( index );
		tabs.remove( index );
		tabStrip.remove( entry.tabComponent.getComponent() );
		entry.tabComponent.removeMouseListener( entry.tabMouseListener );
		entry.tab.setPainter( null );
		
		for( int i = index, n = tabs.size(); i<n; i++ )
			tabs.get( i ).tabComponent.setIndex( i );
		
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
		
	}

	public void removeAllTabs() {
		for (TabEntry tab : tabs) {
			tab.tab.setPainter( null );
			
			for (TabListener listener : listeners) {
				listener.tabRemoved( tab.tab );
				tab.tabComponent.removeMouseListener( tab.tabMouseListener );
			}
		}
		
		tabStrip.removeAll();
		tabs.clear();
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
		setSelectedTab(tabs.get(i).tab);
	}
	
	public void setFocusedTab( Tab tab ){
		focusedTab = tab;
		for( TabEntry entry : tabs ){
			entry.tabComponent.setFocused( entry.tab == tab );
		}
	}

	public void setSelectedTab(Tab tab) {
		if (tab != selectedTab) {
			if( selectedTab != null ){
				int index = indexOf( selectedTab );
				if( index >= 0 )
					tabs.get( index ).tabComponent.setSelected( false );
			}
			
			selectedTab = tab;
			
			tabChanged(tab);
			for (TabListener listener : listeners) {
				listener.tabChanged(tab);
			}
		}
	}

	public Tab getTabAt(int index) {
		return tabs.get(index).tab;
	}

	public Rectangle getBoundsAt(int index) {
		return getBounds(getTabAt(index));
	}

	public Rectangle getBounds(Tab tab) {
		TabComponent component = tabs.get( indexOf( tab ) ).tabComponent;
		Point location = new Point( 0, 0 );
		location = SwingUtilities.convertPoint( component.getComponent(), location, this );
		
		return new Rectangle( location, component.getComponent().getSize() );
	}

	public void updateContentBorder(){
		if( selectedTab != null ){
			TabEntry entry = tabs.get( indexOf( selectedTab ) ); 
			entry.tabComponent.setSelected( true );
			contentArea.setBorder( entry.tabComponent.getContentBorder() );
		}
		else
			contentArea.setBorder( null );
	}
	
	protected void popup( Tab tab, MouseEvent e ){
		if( !e.isConsumed() && e.isPopupTrigger() ){
			Component comp = tab.getComponent();
			if( comp instanceof JComponent ){
				JComponent jcomp = (JComponent)comp;

				JPopupMenu popup = jcomp.getComponentPopupMenu();
				if (popup != null) {
					Point location = jcomp.getPopupLocation(e);
					if (location == null) {
						location = e.getPoint();
					}
					popup.show(this, location.x, location.y);
				}
			}
		}
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

	private class TabEntry{
		public Tab tab;
		public TabComponent tabComponent;
		public TabMouseListener tabMouseListener;
	}
	
	private class TabMouseListener extends MouseAdapter{
		private Tab tab;
		
		public TabMouseListener( Tab tab ){
			this.tab = tab;
		}
		
		@Override
		public void mouseClicked( MouseEvent e ){
			setSelectedTab( tab );
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			popup( tab, e );
		}
		
		@Override
		public void mouseReleased( MouseEvent e ){
			popup( tab, e );
		}
	}
	
	public static void main(String[] args) throws IllegalAccessException, UnsupportedLookAndFeelException,
			InstantiationException, ClassNotFoundException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final RexTabbedComponent comp = new RexTabbedComponent(RexTabbedComponent.TOP);
	//	comp.setTabPainter(new SmallTabPainter());
		//comp.setTabPainter(new RectGradientPainter());
		comp.setTabPainter( ShapedGradientPainter.FACTORY );
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
