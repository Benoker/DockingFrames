package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.OverlayLayout;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.Tab;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabListener;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.util.container.Tuple;


/**
 * A <code>StackDockComponent</code>, based on a <code>RexTabbedComponent</code>, which looks like eclipse.
 * Partly copied from <code>DefaultStackDockComponent</code>.
 *
 * @author Janni Kovacs
 * @author Benjamin Sigg
 */
public class EclipseStackDockComponent extends JPanel implements StackDockComponent, TabListener {

	/**
	 * A listener to the enclosing component, using some {@link bibliothek.gui.dock.control.RemoteRelocator}
	 * to do drag & drop operations.
	 *
	 * @author Benjamin Sigg
	 */
	private class Listener extends MouseInputAdapter {
		/**
		 * Updates the value of {@link bibliothek.gui.dock.station.stack.DefaultStackDockComponent#relocator relocator}
		 *
		 * @param x the x-coordinate of the mouse
		 * @param y the y-coordinate of the mouse
		 */
		private void updateRelocator(int x, int y) {
			if (relocator != null)
				return;

			for (int i = 0, n = getTabCount(); i < n; i++) {
				Rectangle bounds = getBoundsAt(i);
				if (bounds != null && bounds.contains(x, y)) {
					relocator = dockables.get(i).getB();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			if (e.isConsumed())
				return;
			updateRelocator(e.getX(), e.getY());
			if (relocator != null) {
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen(mouse, e.getComponent());
				Reaction reaction = relocator.init(mouse.x, mouse.y, 0, 0, e.getModifiersEx());
				switch (reaction) {
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (e.isConsumed())
				return;
			updateRelocator(e.getX(), e.getY());
			if (relocator != null) {
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen(mouse, e.getComponent());
				Reaction reaction = relocator.drop(mouse.x, mouse.y, e.getModifiersEx());
				switch (reaction) {
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (e.isConsumed())
				return;
			updateRelocator(e.getX(), e.getY());
			if (relocator != null) {
				Point mouse = e.getPoint();
				SwingUtilities.convertPointToScreen(mouse, e.getComponent());
				Reaction reaction = relocator.drag(mouse.x, mouse.y, e.getModifiersEx());
				switch (reaction) {
					case BREAK_CONSUMED:
						e.consume();
					case BREAK:
						relocator = null;
						break;
					case CONTINUE_CONSUMED:
						e.consume();
						break;
				}
			}
		}
	}

	/**
	 * The Dockables shown on this component and their RemoteRelocators to control drag&drop operations
	 */
	private List<Tuple<Dockable, RemoteRelocator>> dockables = new ArrayList<Tuple<Dockable, RemoteRelocator>>();

	/**
	 * The controller for which this component is shown
	 */
	private DockController controller;

	/**
	 * the currently used remote
	 */
	private RemoteRelocator relocator;

	private EclipseTheme theme;
	private DockStation station;
	private EclipseTabbedComponent tabs;
	private JPanel actionPanel;
	private Map<Tab, Dockable> dockableMap = new LinkedHashMap<Tab, Dockable>();
	
	private Dockable selectedDockable;
	private ActionDockTitle itemPanel;

	public EclipseStackDockComponent(EclipseTheme theme, DockStation station) {
		this.theme = theme;
		this.station = station;
		setLayout(new OverlayLayout(this));
		tabs = new EclipseTabbedComponent(this);
		tabs.addTabListener(this);
		tabs.setAlignmentX(1.0f);
		tabs.setAlignmentY(0f);
		add(tabs);
		actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 3, 0)){
			@Override
			public boolean contains( int x, int y ){
				if( !super.contains( x, y ))
					return false;
				
				for( int i = 0, n = getComponentCount(); i<n; i++ ){
					Component child = getComponent( i );
					if( child.contains( x-child.getX(), y-child.getY() ))
						return true;
				}
				
				return false;
			}
		};
		actionPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 4));
		actionPanel.setAlignmentX(1.0f);
		actionPanel.setAlignmentY(0f);
		actionPanel.setOpaque(false);
		add(actionPanel);
		setComponentZOrder(actionPanel, 0);
		setComponentZOrder(tabs, 1);
		Listener listener = new Listener();
		tabs.getTabStrip().addMouseListener(listener);
		tabs.getTabStrip().addMouseMotionListener(listener);
	}

	@Override
	public void removeAll() {
		tabs.removeAllTabs();
		dockables.clear();
		dockableMap.clear();
	}

	@Override
	public void remove(int index) {
		Tab tab = tabs.getTabAt(index);
		tabs.removeTab(tab);
		dockables.remove(index);
		dockableMap.remove( tab );
	}

	public void tabRemoved(Tab t) {
		// tabRemoved has been replaced with tabCloseIconClicked()
//		System.err.println("tabRemoved: "+ t.getTitle());
//		Thread.dumpStack();
//		Dockable dockable = dockableMap.get(t);
//		DockStation dockParent = dockable.getDockParent();
//		if (dockParent.canDrag(dockable))
//			dockParent.drag(dockable);
	}

	public void tabChanged(Tab t) {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
			listener.stateChanged(event);
		}
		updateActions();
	}

	public void tabCloseIconClicked(Tab t) {
		Dockable d = dockableMap.get(t);
		theme.getThemeConnector().dockableClosing(d);
	}

	private void updateActions() {
		Dockable dockable = dockableMap.get( tabs.getSelectedTab() );
		
		if( dockable != selectedDockable ){
			if( selectedDockable != null ){
				actionPanel.removeAll();
				selectedDockable.unbind( itemPanel );
				itemPanel = null;
			}
		
			selectedDockable = dockable;
			
			if( dockable != null ){
				itemPanel = new ActionDockTitle( dockable, null ){
					@Override
					protected DockActionSource createSource( Dockable dockable ){
						return new EclipseDockActionSource( theme, super.createSource( dockable ), dockable, false );
					}
				};
				
				dockable.bind( itemPanel );
				actionPanel.add( itemPanel.getComponent() );
			}
		}
	}

	public void addChangeListener(ChangeListener listener) {
		listenerList.add(ChangeListener.class, listener);
	}

	public void removeChangeListener(ChangeListener listener) {
		listenerList.remove(ChangeListener.class, listener);
	}

	public int getSelectedIndex() {
		return tabs.getSelectedIndex();
	}

	public void setSelectedIndex(int index) {
		tabs.setSelectedTab(index);
	}

	public Rectangle getBoundsAt(int index) {
		return tabs.getBoundsAt(index);
	}

	public void addTab(String title, Icon icon, Component comp, Dockable dockable) {
		insertTab(title, icon, comp, dockable, getTabCount());
		tabs.setSelectedTab(getTabCount() - 1);
	}

	public void insertTab(String title, Icon icon, Component comp, Dockable dockable, int index) {
		Tab tab = new Tab(title, icon, dockable.getComponent(), theme.getThemeConnector().isClosable(dockable));
		dockableMap.put(tab, dockable);
		
		tabs.insertTab(tab, index);
		if (controller == null)
			dockables.add(index, new Tuple<Dockable, RemoteRelocator>(dockable, null));
		else
			dockables.add(index, new Tuple<Dockable, RemoteRelocator>(dockable,
					controller.getRelocator().createRemote(dockable)));
	}

	public int getTabCount() {
		return tabs.getTabCount();
	}

	public void setTitleAt(int index, String newTitle) {
		tabs.getTabAt(index).setTitle(newTitle);
	}

	public void setIconAt(int index, Icon newIcon) {
		tabs.getTabAt(index).setIcon(newIcon);
	}

	public Component getComponent() {
		return this;
	}

	public void setController(DockController controller) {
		if (this.controller != controller) {
			if (relocator != null) {
				relocator.cancel();
				relocator = null;
			}

			this.controller = controller;
			if (controller == null) {
				for (Tuple<?, RemoteRelocator> tuple : dockables)
					tuple.setB(null);
			} else {				
				for (Tuple<Dockable, RemoteRelocator> tuple : dockables)
					tuple.setB(controller.getRelocator().createRemote(tuple.getA()));
			}

			for (Dockable dockable : dockableMap.values()) {
				dockable.setController(controller);
			}

			updateActions();
		}
	}
}
