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

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.layouting.TabPlacement;
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
 * @param <I> the kind of info panel this pane supports
 */
public abstract class AbstractTabPane<T extends Tab, M extends TabMenu, I extends LonelyTabPaneComponent> implements TabPane{
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
	
	/** all the tabs, visible and invisible */
	private Map<Dockable, T> tabs = new HashMap<Dockable, T>();
	
	/** all the menus, visible and invisible */
	private List<M> menus = new ArrayList<M>();
	
	/** tells for each {@link Dockable} on which menu it is */
	private Map<Dockable, M> menuPosition = new HashMap<Dockable, M>();
	
	/** a list of listeners to be informed when the content or the selection changes */
	private List<TabPaneListener> listeners = new ArrayList<TabPaneListener>();
    
    /** additional information shown somewhere on this component */
    private I info;
    
    /** where to place tabs */
    private TabPlacement tabPlacement = TabPlacement.TOP_OF_DOCKABLE;
    
	/**
	 * Connects this pane with <code>controller</code>.
	 * @param controller the realm in which this pane works, may be <code>null</code>
	 */
	public void setController( DockController controller ){
		this.controller = controller;
		layoutManager.setProperties( controller );
		fireControllerChanged();
	}
	
	public DockController getController(){
		return controller;
	}
    
	/**
	 * Tells this pane where to paint the tabs.
	 * @param tabPlacement a side, not <code>null</code>
	 */
	public void setDockTabPlacement( TabPlacement tabPlacement ){
		if( tabPlacement == null )
			throw new IllegalArgumentException( "tab placement must not be null" );
		this.tabPlacement = tabPlacement;
		revalidate();
	}
	
	public TabPlacement getDockTabPlacement(){
		return tabPlacement;
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
	 * Gets the minimal size required to have a big enough {@link #getAvailableArea()} to show
	 * all content. 
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
	 * Gets the preferred size required to have a big enough {@link #getAvailableArea()} to show
	 * all content.
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
     * Informs all {@link TabPaneListener}s that the info component has been
     * replaced.
     * @param oldInfo the old info component
     * @param newInfo the new info component
     */
    protected void fireInfoComponentChanged( I oldInfo, I newInfo ){
    	for( TabPaneListener listener : listeners() ){
    		listener.infoComponentChanged( this, oldInfo, newInfo );
    	}
    }
    
    /**
     * Informs all {@link TabPaneListener} that the current {@link DockController} changed.
     */
    protected void fireControllerChanged(){
    	for( TabPaneListener listener : listeners() ){
    		listener.controllerChanged( this, controller );
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
	 * Moves the element at location <code>source</code> to <code>destination</code>.
	 * @param source where to find the element to move
	 * @param destination the target location
	 */
	public void move( int source, int destination ){
		if( destination < 0 || destination >= getDockableCount() ){
			throw new ArrayIndexOutOfBoundsException();
		}
		
		Dockable dockable = dockables.remove( source );
		cleanOut( dockable );
		dockables.add( destination, dockable );
		revalidate();
	}
	
	/**
	 * Removes the <code>index</code>'th element of this pane.
	 * @param index the index of the element to remove
	 */
	public void remove( int index ){
		Dockable dockable = dockables.remove( index );
		boolean selected = false;
		
		if( selection == dockable ){
			selected = true;
			setSelectedDockable( null );
		}
		
		cleanOut( dockable );
		fireRemoved( dockable );
		
		// select other tab
		if( selected ){
			if( index >= getDockableCount() )
				index = getDockableCount()-1;
			if( index >= 0 )
				setSelectedDockable( getDockable( index ) );
		}
		
		revalidate();
	}
	
	/**
	 * Removes all elements from this pane.
	 */
	public void removeAll(){
		for( T tab : tabs.values() ){
			tab.setPaneVisible( false );
			tabRemoved( tab );
		}
		clearTabs();
		
		for( Map.Entry<Dockable, M> item : menuPosition.entrySet() ){
			removeFromMenu( item.getValue(), item.getKey() );
		}
		menuPosition.clear();
		
		for( Dockable dockable : dockables ){
			fireRemoved( dockable );
		}
		
		setSelectedDockable(null);
		
		dockables.clear();
		
		revalidate();
	}
	
	/**
	 * Deletes all {@link Tab}s and {@link TabMenu}s of this {@link TabPane}
	 * and rebuilds them.
	 */
	public void discardComponentsAndRebuild(){
		for( T tab : tabs.values() ){
			tab.setPaneVisible( false );
			tabRemoved( tab );
		}
		clearTabs();
		
		for( Map.Entry<Dockable, M> item : menuPosition.entrySet() ){
			removeFromMenu( item.getValue(), item.getKey() );
		}
		menuPosition.clear();
		
		doLayout();
	}
	
	public Dockable getSelectedDockable(){
		return selection; 
	}
	
	/**
	 * Selects the child <code>dockable</code> of this pane as the one visible
	 * element.
	 * @param dockable the newly selected element, can be <code>null</code>
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
		List<Tab> list = new ArrayList<Tab>();
		for( Tab tab : tabs.values() ){
			if( tab.isPaneVisible() ){
				list.add( tab );
			}
		}
		
		return list.toArray( new Tab[ list.size() ] );
	}
	
	/**
	 * Returns the index of <code>tab</code> following the indices of
	 * {@link #indexOf(Dockable) Dockables} but ignoring invisible tabs.
	 * @param tab the tab to search
	 * @return its index or -1 if not found or invisible
	 */
	public int indexOfVisible( Tab tab ){
		int index = 0;
		for( int i = 0, n = getDockableCount(); i<n; i++ ){
			Tab check = tabs.get( dockables.get( i ) );
			if( check != null && check.isPaneVisible() ){
				if( tab == check )
					return index;
				
				index++;
			}
		}
		
		return -1;
	}
	
	/**
	 * Gets the index'th visible tab. 
	 * @param index the index of some visible tab
	 * @return the visible tab or <code>null</code> if <code>index</code>
	 * is too big.
	 * @see #indexOfVisible(Tab)
	 * @throws IllegalArgumentException if <code>index</code> is smaller than <code>0</code>.
	 */
	public T getVisibleTab( int index ){
		if( index < 0 )
			throw new IllegalArgumentException( "index to small" );
		
		for( int i = 0, n = getDockableCount(); i<n; i++ ){
			T check = tabs.get( dockables.get( i ) );
			if( check != null && check.isPaneVisible() ){
				if( index == 0 )
					return check;
				index--;
			}
		}
		return null;
	}
	
	/**
	 * Gets the number of tabs that are currently visible.
	 * @return the number of visible tabs
	 * @see #getVisibleTab(int)
	 */
	public int getVisibleTabCount(){
		int count = 0;
		for( T check : tabs.values() ){
			if( check.isPaneVisible() ){
				count++;
			}
		}
		return count;
	}
	
	/**
	 * Gets all known tabs of this pane, including invisible tabs. The list is not ordered.
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
	
	public T putOnTab( Dockable dockable ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		if( !dockables.contains( dockable ))
			throw new IllegalArgumentException( "dockable not child of this pane" );
		
		T tab = tabs.get( dockable );
		if( tab == null ){
			tab = newTab( dockable );
			tab.setOrientation( getDockTabPlacement() );
			putTab( dockable, tab );
		}
		tab.setPaneVisible( true );
		
		M menu = menuPosition.remove( dockable );
		if( menu != null ){
			removeFromMenu( menu, dockable );
		}
		
		return tab;
	}
	
	public T getOnTab( Dockable dockable ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		if( !dockables.contains( dockable ))
			throw new IllegalArgumentException( "dockable not child of this pane" );
		
		T tab = tabs.get( dockable );
		if( tab == null ){
			tab = newTab( dockable );
			tab.setOrientation( getDockTabPlacement() );
			putTab( dockable, tab );
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
				this.info.setPaneVisible( false );
			
			I oldInfo = this.info;
			this.info = info;
			
			if( this.info != null )
				this.info.setPaneVisible( true );
			
			fireInfoComponentChanged( oldInfo, info );
		}
	}
	
	public I getInfoComponent(){
		return info;
	}
	
	/**
	 * Gets a list of all the menus of this pane, includes visible and
	 * invisible menus
	 * @return the list of menus
	 */
	public List<M> getMenuList(){
		return new ArrayList<M>( menus );
	}
	
	/**
	 * Gets all the menus of this pane, visible and invisible
	 * @return all the menus
	 */
	public TabMenu[] getMenus(){
		return menus.toArray( new TabMenu[ menus.size() ] );
	}
	
	/**
	 * Gets the menu on which <code>dockable</code> is shown.
	 * @param dockable some child of this pane
	 * @return the menu or <code>null</code>
	 */
	public M getMenu( Dockable dockable ){
		return menuPosition.get( dockable );
	}
	
	@SuppressWarnings("unchecked")
	public void putInMenu( TabMenu menu, Dockable dockable ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockables must not be null" );
		
		if( !this.dockables.contains( dockable ))
			throw new IllegalArgumentException( "not child of this pane: " + dockable );
		
		if( menu == null )
			throw new IllegalArgumentException( "menu is null" );
		
		if( !menus.contains( menu ))
			throw new IllegalArgumentException( "menu not created by this pane" );
		
		// check current menu
		M currentMenu = menuPosition.get( dockable );
		if( currentMenu == menu )
			return;
		
		if( currentMenu != null ){
			removeFromMenu( currentMenu, dockable );
		}
		
		addToMenu( (M)menu, dockable );
		menuPosition.put( dockable, (M)menu );
		
		T tab = tabs.get( dockable );
		if( tab != null ){
			tab.setPaneVisible( false );
		}
	}
	
	public TabMenu createMenu(){
		M menu = newMenu();
		menus.add( menu );
		return menu;
	}
	
	@SuppressWarnings("unchecked")
	public void destroyMenu( TabMenu menu ){
		if( menu == null )
			throw new IllegalArgumentException( "menu is null" );
		
		if( !menus.remove( menu ))
			throw new IllegalArgumentException( "menu not created by this pane" );
		
		menu.setPaneVisible( false );
		
		for( Dockable dockable : menu.getDockables() ){
			menuPosition.remove( dockable );
		}
		
		menuRemoved( (M)menu );
	}

	/**
	 * Adds <code>dockable</code> somewhere to <code>menu</code>
	 * @param menu a menu of this pane
	 * @param dockable a new child of <code>menu</code>
	 */
	protected abstract void addToMenu( M menu, Dockable dockable );
	
	/**
	 * Removes <code>dockable</code> from <code>menu</code>.
	 * @param menu some menu of this pane
	 * @param dockable a child of <code>menu</code>
	 */
	protected abstract void removeFromMenu( M menu, Dockable dockable );
	
	/**
	 * Removes <code>dockable</code> from all tabs and menus, also removes
	 * tabs that are no longer needed.
	 * @param dockable the element to remove
	 */
	private void cleanOut( Dockable dockable ){
		// tab
		T tab = removeTab( dockable );
		if( tab != null ){
			tab.setPaneVisible( false );
			tabRemoved( tab );
		}
		
		// menus
		M menu = menuPosition.remove( dockable );
		if( menu != null ){
			removeFromMenu( menu, dockable );
		}
	}
	
	/**
	 * Associates <code>tab</code> with <code>dockable</code>. this method
	 * modifies the internal data structure in order to store the change.<br>
	 * Subclasses may override this method to be informed about the exact time when 
	 * a tab changes, but the overridden method must call the original method.
	 * @param dockable the key for the tab-map
	 * @param tab the value for the tab-map
	 * @return the old tab at <code>dockable</code>
	 */
	protected T putTab( Dockable dockable, T tab ){
		return tabs.put( dockable, tab );
	}
	
	/**
	 * Removes the tab of <code>dockable</code> from the internal data structure.
	 * @param dockable the key of the element to be removed from the tab-map<br>
	 * Subclasses may override this method to be informed about the exact time when 
	 * a tab changes, but the overridden method must call the original method.
	 * @return the removed element
	 */
	protected T removeTab( Dockable dockable ){
		return tabs.remove( dockable );
	}
	
	/**
	 * Removes all tabs from the internal data structure.<br>
	 * Subclasses may override this method to be informed about the exact time when 
	 * a tab changes, but the overridden method must call the original method.
	 */
	protected void clearTabs(){
		tabs.clear();
	}
	
	/**
	 * Creates a new {@link Tab} that has <code>this</code> as parent and
	 * represents <code>dockable</code>. The new tab should not be stored in
	 * any collection. 
	 * @param dockable the element for which a new tab is required
	 * @return the new tab
	 */
	protected abstract T newTab( Dockable dockable );
	
	/**
	 * Creates a new {@link TabMenu} that has <code>this</code> as parent.
	 * @return the new menu
	 */
	public abstract M newMenu();
	
	/**
	 * Informs this pane that <code>tab</code> will never be used again and
	 * all resources associated with <code>tab</code> should be freed. This
	 * method is only called if <code>tab</code> is invisible.
	 * @param tab the tab to destroy
	 */
	protected abstract void tabRemoved( T tab );

	/**
	 * Informs this pane that <code>menu</code> will never be used again and
	 * all resources associated with <code>menu</code> should be freed. This
	 * method is only called if <code>menu</code> is invisible.
	 * @param menu the destroyed menu
	 */
	protected abstract void menuRemoved( M menu );
}
