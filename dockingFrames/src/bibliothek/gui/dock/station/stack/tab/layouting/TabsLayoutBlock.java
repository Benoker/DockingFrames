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
package bibliothek.gui.dock.station.stack.tab.layouting;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.station.stack.tab.TabPane;

/**
 * A block managing a group of {@link Tab}s. This {@link LayoutBlock} is always 
 * visible.
 * @author Benjamin Sigg
 */
public abstract class TabsLayoutBlock implements LayoutBlock{
	/** all the tabs that are visible on this block */
	private List<Tab> tabs = new ArrayList<Tab>();
	/** the boundaries of this block */
	private Rectangle bounds = new Rectangle();
	
	/** the produced of new tabs */
	private TabPane pane;
	
	/**
	 * Sets the producer of new tabs.
	 * @param pane the produced, might be <code>null</code>
	 */
	public void setPane( TabPane pane ){
		this.pane = pane;
	}
	
	/**
	 * Gets the producer of new tabs.
	 * @return the produced, may be <code>null</code>
	 */
	public TabPane getPane(){
		return pane;
	}
	
	/**
	 * Searches the tab that is currently selected. Only tabs that are part
	 * of this block are searched, a tab that exists but is not known is 
	 * not reported.
	 * @return the selected tab or <code>null</code> if no tab is selected
	 */
	public Tab getSelectedTab(){
		if( pane == null )
			return null;
		Dockable dockable = pane.getSelectedDockable();
		for( Tab tab : tabs ){
			if( tab.getDockable() == dockable ){
				return tab;
			}
		}
		return null;
	}
	
	/**
	 * This method compares the tabs of this block with the tabs known
	 * to the owning {@link TabPane}. Any tab not found in the {@link TabPane}
	 * is removed from this block.
	 */
	public void checkExistence(){
		ListIterator<Tab> iterator = tabs.listIterator();
		Tab[] existing = pane.getTabs();
		
		while( iterator.hasNext() ){
			Tab next = iterator.next();
			boolean found = false;
			for( Tab tab : existing ){
				if( next == tab ){
					found = true;
					break;
				}
			}
			if( !found ){
				iterator.remove();
			}
		}
	}
	
	/**
	 * Gets an array containing all the tabs of this block.
	 * @return the tabs
	 */
	public Tab[] getTabs(){
		return tabs.toArray( new Tab[ tabs.size() ] );
	}
	
	public void addTab( Tab tab ){
		insertTab( tab, tabs.size() );
	}
	
	/**
	 * Inserts <code>tab</code> somewhere in this block. In general this
	 * method tries to add <code>tab</code> at a location such that the order
	 * of tabs equals the order of {@link Dockable}s of the underlying 
	 * {@link TabPane}.
	 * @param tab the tab to insert
	 * @throws IllegalStateException if no {@link TabPane} is available
	 * @throws IllegalArgumentException if the {@link Dockable} of <code>tab</code>
	 * is not a child of this blocks {@link TabPane} 
	 */
	public void insertTab( Tab tab ){
		int[] locations = getOriginalTabLocations();
		TabPane pane = getPane();
		
		Dockable[] dockables = pane.getDockables();
		int index = -1;
		for( int i = 0; i < dockables.length; i++ ){
			if( dockables[i] == tab.getDockable() ){
				index = i;
				break;
			}
		}
		
		if( index == -1 )
			throw new IllegalArgumentException( tab.getDockable() + " is not a child of the TabPane" );
		
		int wrongSmaller = 0;
		int wrongBigger = 0;
		for( int i = 0; i < locations.length; i++ ){
			if( locations[i] < index ){
				wrongSmaller++;
			}
		}
		
		// find location where there are only smaller indices to the left, and
		// bigger indices to the right
		int bestLocation = 0;
		int bestCount = wrongBigger + wrongSmaller;
		
		for( int i = 0; i < locations.length; i++ ){
			if( locations[i] < index ){
				wrongSmaller--;
			}
			else if( locations[i] > index ){
				wrongBigger++;
			}
			int count = wrongSmaller + wrongBigger;
			if( count < bestCount ){
				bestCount = count;
				bestLocation = i;
			}
		}
		
		// finally insert
		insertTab( tab, bestLocation );
	}
	
	/**
	 * This method maps each {@link Tab} of this {@link LayoutBlock} to the
	 * location its {@link Dockable} has in the owning {@link TabPane}.
	 * @return the location of the original elements
	 * @throws IllegalStateException if there is no {@link TabPane} present
	 */
	public int[] getOriginalTabLocations(){
		TabPane pane = getPane();
		if( pane == null )
			throw new IllegalStateException( "no TabPane available" );
		
		Tab[] tabs = getTabs();
		Dockable[] dockables = pane.getDockables();
		
		int[] locations = new int[ tabs.length ];
		
		int index = 0;
		for( int i = 0; i < tabs.length; i++ ){
			Dockable check = tabs[i].getDockable();
			for( int j = 0; j < dockables.length; j++ ){
				if( dockables[index] == check ){
					locations[i] = index;
					break;
				}
				
				index = (index+1) % locations.length;
			}
		}
		
		return locations;
	}
	
	/**
	 * Adds <code>tab</code> at <code>index</code> in the list of tabs.
	 * @param tab a new tab, not <code>null</code>
	 * @param index the index of the new tab
	 */
	public void insertTab( Tab tab, int index ){
		if( tab == null )
			throw new IllegalArgumentException( "tab must not be null" );
		tabs.add( index, tab );
	}
	
	/**
	 * Removes <code>tab</code> from this block.
	 * @param tab the tab to remove
	 * @return <code>true</code> if <code>tab</code> was removed, <code>false</code>
	 * otherwise
	 */
	public boolean removeTab( Tab tab ){
		return tabs.remove( tab );
	}
	
	/**
	 * Removes the tab of location <code>index</code>.
	 * @param index the index of the tab
	 * @return the tab that was removed
	 */
	public Tab removeTab( int index ){
		return tabs.remove( index );
	}
	
	/**
	 * Removes all tabs of this block.
	 */
	public void removeAllTabs(){
		tabs.clear();
	}
	
	/**
	 * Updates the layout of this block. The layout includes the location
	 * and size of all the {@link Tab}s managed by this block.
	 */
	public abstract void doLayout();
	
	/**
	 * Automatically determines which tabs should be shown in this block.
	 * This decision might depend on the currently selected tab, the
	 * number of tabs, the size of this block, etc...<br>
	 * In general this method should choose as many tabs as possible and
	 * should also choose the selected tab.<br>
	 * Note: this method might create tabs in order to check whether they
	 * can be used.
	 */
	public abstract void autoSelectTabs();
	
	public Dimension getMinimumSize(){
		return getMinimumSize( getTabs() );
	}
	
	/**
	 * Gets the minimum size this {@link LayoutBlock} would need to
	 * layout <code>tabs</code>.
	 * @param tabs some tabs
	 * @return the minimum size
	 */
	public abstract Dimension getMinimumSize( Tab[] tabs );
	
	public Dimension getPreferredSize(){
		return getPreferredSize( getTabs() );
	}
	
	/**
	 * Gets the preferred size this {@link LayoutBlock} would need to
	 * layout <code>tabs</code>.
	 * @param tabs some tabs
	 * @return the preferred size
	 */
	public abstract Dimension getPreferredSize( Tab[] tabs );
	
	public boolean isVisible(){
		return true;
	}
	
	public void setBounds( int x, int y, int width, int height ){
		bounds.setBounds( x, y, width, height );
	}
	
	/**
	 * Gets the boundaries of this block.
	 * @return the boundaries
	 */
	public Rectangle getBounds(){
		return new Rectangle( bounds );
	}
}
