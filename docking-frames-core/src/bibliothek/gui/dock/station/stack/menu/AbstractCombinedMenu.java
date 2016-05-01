/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.CombinedHandler;
import bibliothek.gui.dock.station.stack.CombinedMenu;
import bibliothek.gui.dock.station.stack.tab.AbstractTabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabMenu;
import bibliothek.gui.dock.station.stack.tab.TabMenuListener;
import bibliothek.gui.dock.station.stack.tab.TabPane;
import bibliothek.gui.dock.station.stack.tab.TabPaneComponent;
import bibliothek.gui.dock.station.stack.tab.TabPaneMenuBackgroundComponent;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.gui.dock.util.UIValue;

/**
 * An abstract implementation of {@link CombinedMenu}, this menu
 * delegates creation and management of its {@link Component} to its
 * subclasses and uses a {@link CombinedMenuContent} to show its content.
 * @author Benjamin Sigg
 */
public abstract class AbstractCombinedMenu extends AbstractTabPaneComponent implements CombinedMenu{
	/** the button of this menu */
	private Component component;
	
	/** the background algorithm of this menu */
	private Background background = new Background();
	
	/** the content of this menu */
	private PropertyValue<CombinedMenuContent> content = new PropertyValue<CombinedMenuContent>( 
			CombinedMenuContent.MENU_CONTENT ){
		@Override
		protected void valueChanged( CombinedMenuContent oldValue, CombinedMenuContent newValue ){
			// ignore
		}
	};
	
	/** the items of this menu */
	private List<Entry> entries = new ArrayList<Entry>();
	
	/** the controller in whose realm this menu is used */
	private DockController controller;
	
	/** handler for making this menu visible or invisible */
	private CombinedHandler<? super AbstractCombinedMenu> handler;
	
	/** All the listeners that were added to this menu */
	private List<TabMenuListener> listeners = new ArrayList<TabMenuListener>();
	
	/**
	 * Creates a new menu.
	 * @param parent the owner of this menu, must not be <code>null</code>
	 * @param handler handler for making this menu visible or invisible and change the z order
	 */
	public AbstractCombinedMenu( TabPane parent, CombinedHandler<? super AbstractCombinedMenu> handler ){
		super( parent );
		this.handler = handler;
	}
	
	/**
	 * Ensures that {@link #createComponent()} is called and its result
	 * stored.
	 */
	protected void ensureComponent(){
		if( component == null ){
			component = createComponent();
		}
	}
	
	/**
	 * Gets an algorithm that can be used to paint the background of this menu.
	 * @return the algorithm, not <code>null</code>
	 */
	protected BackgroundAlgorithm getBackground(){
		return background;
	}
	
	/**
	 * Called if the background algorithm has been exchanged.
	 * @param paint the new background algorithm, can be <code>null</code>
	 */
	protected void backgroundChanged( BackgroundPaint paint ){
		// nothing
	}
	
	/**
	 * Creates the button which will always be visible. The user needs to
	 * click onto that button in order to show the content of this menu. This
	 * method will be called only once and not from a constructor of
	 * {@link AbstractCombinedMenu}.
	 * @return the new button
	 */
	protected abstract Component createComponent();
	
	/**
	 * Opens a menu where the user can select a {@link Dockable}.
	 */
	public void open(){
		Handler handler = new Handler();
		handler.open();
	}
	
	/**
	 * Called once the menu is closed, the default implementation does
	 * nothing.
	 */
	protected void closed(){
		// nothing
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		content.setProperties( controller );
		background.setController( controller );
	}
	
	/**
	 * Gets the controller in whose realm this menu is used.
	 * @return the controller, might be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	public void addTabMenuListener( TabMenuListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );	
	}
	
	public void removeTabMenuListener( TabMenuListener listener ){
		listeners.remove( listener );	
	}
	
	/**
	 * Gets all the {@link TabMenuListener} that are currently registered at this menu.
	 * @return all the listeners
	 */
	protected TabMenuListener[] tabMenuListeners(){
		return listeners.toArray( new TabMenuListener[ listeners.size() ] );
	}
	
	public void setPaneVisible( boolean visible ){
		handler.setVisible( this, visible );
	}
	
	public boolean isPaneVisible(){
		return handler.isVisible( this );
	}
	
	public void setZOrder( int order ){
		handler.setZOrder( this, order );	
	}
	
	public int getZOrder(){
		return handler.getZOrder( this );
	}
	
	/**
	 * Called if this menu was open, an element was selected and the menu closed.
	 * @param dockable the selected element
	 */
	protected abstract void selected( Dockable dockable );
	
	public Component getComponent(){
		ensureComponent();
		return component;
	}

	public void setIcon( int index, Icon icon ){
		entries.get( index ).icon = icon;
	}

	public void setText( int index, String text ){
		entries.get( index ).text = text;
	}

	public void setTooltip( int index, String tooltip ){
		entries.get( index ).tooltip = tooltip;
	}
	
	public void setEnabled( int index, boolean enabled ){
		entries.get( index ).enabled = enabled;
	}
	
	public void insert( int index, Dockable dockable ){
		Entry entry = new Entry();
		entry.dockable = dockable;
		entry.icon = dockable.getTitleIcon();
		entry.text = dockable.getTitleText();
		entry.tooltip = dockable.getTitleToolTip();
		entries.add( index, entry );
		
		for( TabMenuListener listener : tabMenuListeners() ){
			listener.dockablesAdded( this, index, 1 );
		}
	}
	
	public void remove( Dockable dockable ){
		for( int i = 0, n = entries.size(); i<n; i++ ){
			if( entries.get( i ).dockable == dockable ){
				entries.remove( i );
				
				for( TabMenuListener listener : tabMenuListeners() ){
					listener.dockablesRemoved( this, i, 1 );
				}
				
				return;
			}
		}
	}

	public Dockable[] getDockables(){
		Dockable[] result = new Dockable[ entries.size() ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = entries.get( i ).dockable;
		}
		return result;
	}
	
	public int getDockableCount(){
		return entries.size();
	}
	
	public Dockable getDockable( int index ){
		return entries.get( index ).dockable;
	}
	
	/**
	 * A handler for handling opening, closing and clean up of a {@link CombinedMenuContent}.
	 * @author Benjamin Sigg
	 */
	private class Handler implements CombinedMenuContentListener{
		private CombinedMenuContent menu;
		
		/**
		 * Creates a new handler using the current menu.
		 */
		public Handler(){
			menu = content.getValue();
		}
		
		/**
		 * Opens the menu of this handler.
		 */
		public void open(){
			if( menu == null )
				return;
			
			Component component = getComponent();
			
			CombinedMenuContent.Item[] items = new CombinedMenuContent.Item[ entries.size() ];
			for( int i = 0; i < items.length; i++ ){
				items[i] = entries.get( i ).toItem();
			}
			menu.addCombinedMenuContentListener( this );
			menu.open( controller, component, 0, component.getHeight(), items );
		}
		
		public void opened( CombinedMenuContent menu ){
			// ignore	
		}
		
		public void canceled( CombinedMenuContent menu ){
			menu.removeCombinedMenuContentListener( this );
		}
		
		public void selected( CombinedMenuContent menu, Dockable selection ){
			menu.removeCombinedMenuContentListener( this );
			AbstractCombinedMenu.this.selected( selection );
		}
	}
	
	/**
	 * One entry of this menu. 
	 */
	private static class Entry{
		/** the element of this entry */
		public Dockable dockable;
		/** the title */
		public String text;
		/** description of this entry */
		public String tooltip;
		/** small icon */
		public Icon icon;
		/** whether this menu is active */
		public boolean enabled = true;
		
		/**
		 * Creates a new item out of this entry.
		 * @return the new item
		 */
		public CombinedMenuContent.Item toItem(){
			return new CombinedMenuContent.Item( dockable, text, tooltip, icon, enabled );
		}
	}
	
	/**
	 * An {@link UIValue} representing the background of this menu.
	 * @author Benjamin Sigg
	 */
	private class Background extends BackgroundAlgorithm implements TabPaneMenuBackgroundComponent{
		public Background(){
			super( TabPaneMenuBackgroundComponent.KIND, ThemeManager.BACKGROUND_PAINT + ".tabPane.child.menu" );
		}

		@Override
		public void set( BackgroundPaint value ){
			super.set( value );
			backgroundChanged( getPaint() );
		}
		
		public TabMenu getMenu(){
			return AbstractCombinedMenu.this;
		}

		public TabPaneComponent getChild(){
			return AbstractCombinedMenu.this;
		}

		public TabPane getPane(){
			return getMenu().getTabParent();
		}

		public Component getComponent(){
			return component;
		}
	}
}
