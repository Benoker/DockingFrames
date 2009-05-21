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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * An abstract implementation of {@link TabPane}. This class handles creation,
 * storage and destruction of {@link Tab}s and {@link TabMenu}s, it also stores
 * a list of currently visible {@link Dockable}s.<br>
 * Subclasses should call {@link #setController(DockController)} to make sure
 * this pane can use all available information.
 * @author Benjamin Sigg
 * @param <T> the kind of tabs this pane supports
 * @param <M> the kind of menus this pane supports
 */
public abstract class AbstractTabPane<T extends Tab, M extends TabMenu, I extends TabPaneComponent> implements TabPane{
	/** the layout manager that is responsible of updating this pane */
	private PropertyValue<TabLayoutManager> layoutManager = 
		new PropertyValue<TabLayoutManager>( TabPane.LAYOUT_MANAGER ){
			@Override
			protected void valueChanged( TabLayoutManager oldValue, TabLayoutManager newValue ){
				if( oldValue != null )
					oldValue.uninstall( AbstractTabPane.this );
				
				if( newValue != null )
					newValue.install( AbstractTabPane.this );
			}
		};
	
		
	/** the controller in whose realm this pane works */
	private DockController controller;
	
	/** the children of this pane */
	private List<Dockable> dockables = new ArrayList<Dockable>();
	
	/** the current selection, can be <code>null</code> */
	private Dockable selection;
	
	/** all the tabs */
	private Map<Dockable, T> tabs = new HashMap<Dockable, T>();
	
	/** all the menus */
	private Map<Dockable, M> menus = new HashMap<Dockable, M>();
	
	/** a list of listeners to be informed when the content or the selection changes */
	private List<TabPaneListener> listeners = new ArrayList<TabPaneListener>();
    
    /** additional information shown somewhere on this component */
    private I info;
    
	/**
	 * Connects this pane with <code>controller</code>.
	 * @param controller the realm in which this pane works, may be <code>null</code>
	 */
	public void setController( DockController controller ){
		this.controller = controller;
		layoutManager.setProperties( controller );
	}
	
	public DockController getController(){
		return controller;
	}
    
	/**
	 * Updates the layout of this pane, assuming a {@link TabLayoutManager}
	 * is installed.
	 */
	public void doLayout(){
		TabLayoutManager layout = layoutManager.getValue();
		
		if( layout != null ){
			layout.layout( this );
		}
	}
	
	/**
	 * Gets the minimal size that {@link #getAvailableArea()} should return. 
	 * @return the minimal size
	 */
	public Dimension getMinimumSize(){
		TabLayoutManager layout = layoutManager.getValue();
		
		if( layout != null ){
			return layout.getMinimumSize( this );
		}
		
		return new Dimension( 1, 1 );
	}
	
	/**
	 * Gets the preferred size that {@link #getAvailableArea()} should return.
	 * @return the preferred size
	 */
	public Dimension getPreferredSize(){
		TabLayoutManager layout = layoutManager.getValue();
		
		if( layout != null ){
			return layout.getPreferredSize( this );
		}
		
		return new Dimension( 1, 1 );
	}
	
	/**
	 * Called when the layout of this pane has become invalid, the default
	 * behavior is to call {@link #doLayout()}. Subclasses may override to
	 * update the layout lazily.
	 */
	public void revalidate(){
		doLayout();
	}


	public void addTabPaneListener( TabPaneListener listener ){
		listeners.add( listener );
	}
	
	public void removeTabPaneListener( TabPaneListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all the {@link TabPaneListener}s that are known to this {@link TabPane}
	 * @return an array of listeners
	 */
	protected TabPaneListener[] listeners(){
		return listeners.toArray( new TabPaneListener[ listeners.size() ] );
	}

    /**
     * Informs all {@link TabPaneListener}s that the selection changed.
     */
    protected void fireSelectionChanged(){
       for( TabPaneListener listener : listeners() ){
    	   listener.selectionChanged( this );
       }
    }
    
    /**
     * Informs all {@link TabPaneListener}s that <code>dockable</code>
     * has been added.
     * @param dockable the new child
     */
    protected void fireAdded( Dockable dockable ){
    	for( TabPaneListener listener : listeners() ){
    		listener.added( this, dockable );
    	}
    }
    
    /**
     * Informs all {@link TabPaneListener}s that <code>dockable</code>
     * has been removed. 
     * @param dockable the removed child
     */
    protected void fireRemoved( Dockable dockable ){
    	for( TabPaneListener listener : listeners() ){
    		listener.removed( this, dockable );
    	}
    }
	
    /**
     * Gets the layout manager that is currently used to layout the contents
     * of this pane.
     * @return the layout manager, may be <code>null</code>
     */
	public TabLayoutManager getLayoutManager(){
		return layoutManager.getValue();
	}
	
	/**
	 * Sets the layout manager that will layout the contents of this pane, a
	 * value of <code>null</code> will reinstall the default layout manager.
	 * @param layoutManager the new manager, may be <code>null</code>
	 */
	public void setLayoutManager( TabLayoutManager layoutManager ){
		this.layoutManager.setValue( layoutManager );
	}
	
	/**
	 * Adds <code>dockable</code> as child to this tab-pane.
	 * @param index the index of the new child
	 * @param dockable the new child
	 */
	public void insert( int index, Dockable dockable ){
		int size = getDockableCount();
		dockables.add( index, dockable );
		fireAdded( dockable );
		
		if( size == 0 ){
			setSelectedDockable( dockable );
		}
		
		revalidate();
	}
	
	/**
	 * Removes the <code>index</code>'th element of this pane.
	 * @param index the index of the element to remove
	 */
	public void remove( int index ){
		Dockable dockable = dockables.remove( index );
		cleanOut( dockable );
		fireRemoved( dockable );
		revalidate();
	}
	
	/**
	 * Removes all elements from this pane.
	 */
	public void removeAll(){
		for( T tab : tabs.values() ){
			setVisibleTab( tab, false );
			destroyTab( tab );
		}
		tabs.clear();
		
		Set<M> menus = new HashSet<M>( this.menus.values() );
		for( M menu : menus ){
			setVisibleMenu( menu, false );
			destroyMenu( menu );
		}
		this.menus.clear();
		
		for( Dockable dockable : dockables ){
			fireRemoved( dockable );
		}
		dockables.clear();
		
		revalidate();
	}
	
	public Dockable getSelectedDockable(){
		return selection; 
	}
	
	/**
	 * Selects the child <code>dockable</code> of this pane as the one visible
	 * element.
	 * @param dockable the newly selected element
	 */
	public void setSelectedDockable( Dockable dockable ){
		if( this.selection != dockable ){
			this.selection = dockable;
			revalidate();
			fireSelectionChanged();
		}
	}
	
	public Dockable[] getDockables(){
		return dockables.toArray( new Dockable[ dockables.size() ] );
	}
	
	/**
	 * Gets the number of elements that are displayed on this pane.
	 * @return the number of elements
	 */
	public int getDockableCount(){
		return dockables.size();
	}
	
	/**
	 * Gets the <code>index</code>'th element of this pane.
	 * @param index the index of an element
	 * @return element at position <code>index</code>
	 */
	public Dockable getDockable( int index ){
		return dockables.get( index );
	}
	
	/**
	 * Gets the index of <code>dockable</code> on this pane.
	 * @param dockable the element to search
	 * @return the index or -1 if <code>dockable</code> was not found
	 */
	public int indexOf( Dockable dockable ){
		return dockables.indexOf( dockable );
	}
	
	public Tab[] getTabs(){
		Collection<T> values = tabs.values();
		return values.toArray( new Tab[ values.size() ] );
	}
	
	/**
	 * Gets the tabs of this pane, the list is not ordered.
	 * @return the tabs of this pane
	 */
	public List<T> getTabsList(){
		return new ArrayList<T>( tabs.values() );
	}
	
	/**
	 * Gets the tab that is used to display <code>dockable</code>.
	 * @param dockable the element to search
	 * @return the tab or <code>null</code>
	 */
	public T getTab( Dockable dockable ){
		return tabs.get( dockable );
	}
	
	public Tab putOnTab( Dockable dockable ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		if( !dockables.contains( dockable ))
			throw new IllegalArgumentException( "dockable not child of this pane" );
		
		T tab = tabs.get( dockable );
		if( tab == null ){
			cleanOut( dockable );
			tab = createTab( dockable );
			setVisibleTab( tab, true );
			tabs.put( dockable, tab );
		}
		return tab;
	}
	
	/**
	 * Sets the info component.
	 * @param info the new component, can be <code>null</code>
	 * @see #getInfoComponent()
	 */
	public void setInfoComponent( I info ){
		if( this.info != info ){
			if( this.info != null )
				setVisibleInfo( this.info, false );
			
			this.info = info;
			
			if( this.info != null )
				setVisibleInfo( this.info, true );
		}
	}
	
	public TabPaneComponent getInfoComponent(){
		return info;
	}
	
	/**
	 * Gets a list of all the menus of this pane.
	 * @return the list of menus
	 */
	public List<M> getMenuList(){
		return new ArrayList<M>( menus.values() );
	}
	
	public TabMenu[] getMenus(){
		Set<M> result = new HashSet<M>();
		for( M menu : menus.values() ){
			result.add( menu );
		}
		return result.toArray( new TabMenu[ result.size() ] );
	}
	
	/**
	 * Gets the menu on which <code>dockable</code> is shown.
	 * @param dockable some child of this pane
	 * @return the menu or <code>null</code>
	 */
	public M getMenu( Dockable dockable ){
		return menus.get( dockable );
	}
	
	public TabMenu putInMenu( Dockable... dockables ){
		if( dockables == null )
			throw new IllegalArgumentException( "dockables must not be null" );
		if( dockables.length == 0 )
			throw new IllegalArgumentException( "dockables must contains at least one element" );
		for( int i = 0; i < dockables.length; i++ ){
			if( dockables[i] == null )
				throw new IllegalArgumentException( "entry '" + i + "' is null" );
			
			if( !this.dockables.contains( dockables[i] ))
				throw new IllegalArgumentException( "not child of this pane: " + dockables[i] );
			
			for( int j = i+1; j < dockables.length; j++ ){
				if( dockables[i] == dockables[j] )
					throw new IllegalArgumentException( "a dockable is twice in the list: '" + i + "' and '" + j + "'" );
			}
		}
		
		// check whether a menu can be reused
		M menu = menus.get( dockables[0] );
		if( correctMenu( menu, dockables ))
			return menu;
		
		// ... we were not that lucky, clean up tabs and menus and create new menu
		for( Dockable dockable : dockables ){
			cleanOut( dockable );
		}
		
		// build up new menu
		menu = createMenu( dockables );
		for( Dockable dockable : dockables ){
			menus.put( dockable, menu );
		}
		setVisibleMenu( menu, true );
		return menu;
	}
	
	private void cleanOut( Dockable dockable ){
		T tab = tabs.remove( dockable );
		if( tab != null ){
			setVisibleTab( tab, false );
			destroyTab( tab );
		}
		else{
			M menu = menus.remove( dockable );
			if( menu != null ){
				for( Dockable menuChild : menu.getDockables() ){
					menus.remove( menuChild );
				}
				setVisibleMenu( menu, false );
				destroyMenu( menu );
			}
		}
	}
	
	private boolean correctMenu( M menu, Dockable[] expected ){
		if( menu == null )
			return false;
		
		Dockable[] dockables = menu.getDockables();
		if( dockables.length != expected.length )
			return false;
		
		for( int i = 0; i < dockables.length; i++ ){
			if( dockables[i] != expected[i] ){
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Creates a new {@link Tab} that has <code>this</code> as parent and
	 * represents <code>dockable</code>. The new tab should not be stored in
	 * any collection. 
	 * @param dockable the element for which a new tab is required
	 * @return the new tab
	 */
	protected abstract T createTab( Dockable dockable );
	
	/**
	 * Creates a new {@link TabMenu} that has <code>this</code> as parent
	 * and represents <code>dockables</code>. The new menu should not be
	 * stored in any collection.
	 * @param dockables the elements which will be represented by the new menu
	 * @return the new menu
	 */
	protected abstract M createMenu( Dockable[] dockables );
	
	/**
	 * Changes the visibility of <code>tab</code> to <code>visible</code>. For example
	 * if <code>tab</code> is a {@link Component} then making it visible would mean
	 * to add it to some {@link Container}, making it invisible would mean to
	 * remove if from the {@link Container}.
	 * @param tab the tab whose visibility needs to be changed
	 * @param visible the new state
	 */
	protected abstract void setVisibleTab( T tab, boolean visible );

	/**
	 * Changes the visibility of <code>menu</code> to <code>visible</code>. For example
	 * if <code>menu</code> is a {@link Component} then making it visible would mean
	 * to add it to some {@link Container}, making it invisible would mean to
	 * remove if from the {@link Container}.
	 * @param menu menu tab whose visibility needs to be changed
	 * @param visible the new state
	 */
	protected abstract void setVisibleMenu( M menu, boolean visible );
	
	/**
	 * Changes the visibility state of <code>info</code>.
	 * @param info the info to show or hide
	 * @param visible whether to show or hide
	 */
	protected abstract void setVisibleInfo( I info, boolean visible );
	
	/**
	 * Informs this pane that <code>tab</code> will never be used again and
	 * all resources associated with <code>tab</code> should be freed. This
	 * method is only called if <code>tab</code> is invisible.
	 * @param tab the tab to destroy
	 */
	protected abstract void destroyTab( T tab );

	/**
	 * Informs this pane that <code>menu</code> will never be used again and
	 * all resources associated with <code>menu</code> should be freed. This
	 * method is only called if <code>menu</code> is invisible.
	 * @param tab the tab to destroy
	 */
	protected abstract void destroyMenu( M menu );
}
