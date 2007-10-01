/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.*;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.PropertyValue;


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
	private class Listener extends MouseInputAdapter implements DockableListener {
		private TabEntry entry;
		
		public Listener( TabEntry entry ){
			this.entry = entry;
		}
		
		private void updateRelocator(){
			if( relocator != null ){
				if( entry.relocator != relocator ){
					relocator.cancel();
					relocator = null;
				}
			}
			
			if( relocator == null ){
				relocator = entry.relocator;
			}
		}
		
		@Override
		public void mousePressed(MouseEvent e) {
			if( controller != null )
				controller.setFocusedDockable( entry.dockable, false );
			
			if (e.isConsumed())
				return;
		
			updateRelocator();
			
			if( relocator != null ){
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

			if( relocator != null ){
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
			

			if( relocator != null ){
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
		
		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
			entry.tab.setTitle( newTitle );
		}
		
		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			entry.tab.setIcon( newIcon );
		}
		
		public void titleBinded( Dockable dockable, DockTitle title ){
			// ignore
		}
		
		public void titleUnbinded( Dockable dockable, DockTitle title ){
			// ignore
		}
	}

	/**
	 * The Dockables shown on this component and their RemoteRelocators to control drag&drop operations
	 */
	private List<TabEntry> dockables = new ArrayList<TabEntry>();

	/**
	 * The controller for which this component is shown
	 */
	private DockController controller;

	private FocusListener controllerFocusListener = new FocusListener();
	
	/**
	 * the currently used remote
	 */
	private RemoteRelocator relocator;
	
	private EclipseTheme theme;
	private DockStation station;
	private EclipseTabbedComponent tabs;
	private Map<Tab, Dockable> dockableMap = new LinkedHashMap<Tab, Dockable>();
	
	private Dockable selectedDockable;
	
	private PropertyValue<Boolean> paintIconsWhenDeselected = 
		new PropertyValue<Boolean>( EclipseTheme.PAINT_ICONS_WHEN_DESELECTED ){
		
		@Override
		protected void valueChanged( Boolean oldValue, Boolean newValue ){
			tabs.setPaintIconsWhenInactive( Boolean.TRUE.equals( newValue ) );
		}
	};
	
	private PropertyValue<TabPainter> tabPainter =
		new PropertyValue<TabPainter>( EclipseTheme.TAB_PAINTER ){
		
		@Override
		protected void valueChanged(TabPainter oldValue, TabPainter newValue){
			if( newValue == null )
				newValue = ShapedGradientPainter.FACTORY;
			
			if( tabs.getTabPainter() != newValue ){
				for( int i = 0, n = dockables.size(); i<n; i++ ){
					TabEntry entry = dockables.get( i );
					
					TabComponent tabComponent = tabs.getTabComponent( i );
					tabComponent.removeMouseListener( entry.listener );
					tabComponent.removeMouseMotionListener( entry.listener );
				}
				
				tabs.setTabPainter( newValue );
				
				for( int i = 0, n = dockables.size(); i<n; i++ ){
					TabEntry entry = dockables.get( i );
					
					TabComponent tabComponent = tabs.getTabComponent( i );
					tabComponent.addMouseListener( entry.listener );
					tabComponent.addMouseMotionListener( entry.listener );
				}
			}
		}
	};

	public EclipseStackDockComponent(EclipseTheme theme, DockStation station) {
		this.theme = theme;
		this.station = station;
		setLayout( new GridLayout( 1, 1 ) );
		tabs = new EclipseTabbedComponent(this);
		tabs.addTabListener(this);
		tabs.setAlignmentX(1.0f);
		tabs.setAlignmentY(0f);
		add(tabs);
	}
	
	@Override
	public void removeAll() {
		for( int i = 0, n = dockables.size(); i<n; i++ ){
			TabEntry entry = dockables.get( i );
			TabComponent tabComponent = tabs.getTabComponent( i );
			
			entry.buttons.set( null );
			tabComponent.removeMouseListener( entry.listener );
			tabComponent.removeMouseMotionListener( entry.listener );
			
			entry.dockable.removeDockableListener( entry.listener );
		}
		tabs.removeAllTabs();
		dockables.clear();
		dockableMap.clear();
	}

	@Override
	public void remove(int index) {
		Tab tab = tabs.getTabAt(index);
		TabComponent tabComponent = tabs.getTabComponent( index );
		tabs.removeTab(tab);
		TabEntry entry = dockables.remove(index);
		entry.buttons.set( null );
		entry.dockable.removeDockableListener( entry.listener );
		tabComponent.removeMouseListener( entry.listener );
		tabComponent.removeMouseMotionListener( entry.listener );
		dockableMap.remove( tab );
	}

	public void tabRemoved(Tab t) {
		// ignore
	}

	public void tabChanged(Tab t) {
		ChangeEvent event = new ChangeEvent(this);
		for (ChangeListener listener : listenerList.getListeners(ChangeListener.class)) {
			listener.stateChanged(event);
		}
		updateActions();
	}

	private void updateActions() {
		Dockable dockable = dockableMap.get( tabs.getSelectedTab() );
		
		if( dockable != selectedDockable ){
			if( selectedDockable != null ){
				tabs.set( null, null );
			}
		
			selectedDockable = dockable;
			
			if( dockable != null ){
				tabs.set( dockable,
						new EclipseDockActionSource( theme, dockable.getGlobalActionOffers(),
								dockable, false ) );
			}
		}
	}
	
	private void updateFocus(){
		Dockable focused = controller == null ? null : controller.getFocusedDockable();
		for( TabEntry entry : dockables ){
			if( entry.dockable == focused ){
				tabs.setFocusedTab( entry.tab );
				return;
			}
		}
		
		tabs.setFocusedTab( null );
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
		ButtonPanel buttons = new ButtonPanel( false );
		buttons.set( dockable, new EclipseDockActionSource( theme, dockable.getGlobalActionOffers(), dockable, true ) );
		Tab tab = new Tab(title, icon, dockable.getComponent(), buttons );
		dockableMap.put(tab, dockable);
		
		tabs.insertTab(tab, index);
		TabEntry entry = new TabEntry();
		entry.tab = tab;
		entry.dockable = dockable;
		entry.buttons = buttons;
		entry.listener = new Listener( entry );
		
		TabComponent tabComponent = tabs.getTabComponent( index );
		tabComponent.addMouseListener( entry.listener );
		tabComponent.addMouseMotionListener( entry.listener );
		
		dockable.addDockableListener( entry.listener );
		
		if (controller == null)
			entry.relocator = null;
		else
			entry.relocator = controller.getRelocator().createRemote(dockable);
		
		dockables.add( index, entry );
	}

	public Dockable getDockable( int index ){
		return dockables.get( index ).dockable;
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

			if( this.controller != null ){
				this.controller.removeDockControllerListener( controllerFocusListener );
			}
			
			this.controller = controller;
			if (controller == null) {
				for ( TabEntry entry : dockables)
					entry.relocator = null;
			} else {				
				controller.addDockControllerListener( controllerFocusListener );
				
				for ( TabEntry entry : dockables)
					entry.relocator = controller.getRelocator().createRemote(entry.dockable);
			}

			//for (Dockable dockable : dockableMap.values()) {
			//	dockable.setController(controller);
			//}
			
			if( controller == null ){
				paintIconsWhenDeselected.setProperties( null );
				tabPainter.setProperties( null );
			}
			else{
				paintIconsWhenDeselected.setProperties( controller.getProperties() );
				tabPainter.setProperties( controller.getProperties() );
			}

			updateFocus();
			updateActions();
		}
	}
	
	private class TabEntry{
		public Tab tab;
		public Dockable dockable;
		public RemoteRelocator relocator;
		public ButtonPanel buttons;
		public Listener listener;
	}
	
	private class FocusListener extends DockControllerAdapter{
		@Override
		public void dockableFocused( DockController controller, Dockable dockable ){
			updateFocus();
		}
	}
}
