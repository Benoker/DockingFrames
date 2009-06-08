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
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
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
	
	/** the producer of new tabs */
	private TabPane pane;
	
	/** used to call {@link #doLayout()} asynchronously */
	private Revalidate revalidate = new Revalidate();
	
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
	
	/**
	 * Returns the number of tabs currently on this block.
	 * @return the nubmer of {@link Tab}s
	 */
	public int getTabsCount(){
		return tabs.size();
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
	 * Tells the index at which <code>tab</code> appears on this block. 
	 * @param tab some tab
	 * @return its location or -1 if not found
	 */
	public int indexOfTab( Tab tab ){
		return tabs.indexOf( tab );
	}
	
	/**
	 * Gets all tabs that could, in theory, be displayed on this block. The
	 * tabs are ordered by importance, the most important tabs come first. In
	 * the default implementation the fist tab is always {@link #getSelectedTab()},
	 * then all the currently displayed tabs follow, only after them the invisible
	 * tabs follow.
	 * @return all tabs, ordered by importance
	 * @throws IllegalStateException if {@link #getPane()} returns <code>null</code>
	 */
	public Tab[] getTabsOrderedByImportance(){
		if( pane == null )
			throw new IllegalStateException( "no TabPane available" );
		
		Dockable[] dockables = pane.getDockables();
		Tab[] allTabs = new Tab[ dockables.length ];
		int[] visibleToInvisible = getOriginalTabLocations();
		    
		for( int i = 0; i < allTabs.length; i++ ){
			allTabs[i] = pane.getOnTab( dockables[i] );
		}
		
		Tab[] result = new Tab[ allTabs.length ];
		int resultIndex = 0;
		
		// search selected tab
		Dockable selectedDockable = pane.getSelectedDockable();
		Tab selected = null;
		for( Tab tab : allTabs ){
			if( tab.getDockable() == selectedDockable ){
				selected = tab;
				break;
			}
		}
		
		for( int i = 0; i < allTabs.length; i++ ){
			if( selected == allTabs[i] ){
				allTabs[i] = null;
				result[ resultIndex++ ] = selected;
				break;
			}
		}
		
		// search visible tabs
		if( selected == null ){
			for( int i = 0; i < visibleToInvisible.length; i++ ){
				result[ resultIndex++ ] = allTabs[ visibleToInvisible[ i ]];
				allTabs[ visibleToInvisible[i] ] = null;
			}
		}
		else{
			int selectedIndex = indexOfTab( selected );
			for( int i = selectedIndex-1; i >= 0; i-- ){
				result[ resultIndex++ ] = allTabs[ visibleToInvisible[ i ]];
				allTabs[ visibleToInvisible[i] ] = null;
			}
			for( int i = selectedIndex+1; i < visibleToInvisible.length; i++ ){
				result[ resultIndex++ ] = allTabs[ visibleToInvisible[ i ]];
				allTabs[ visibleToInvisible[i] ] = null;
			}
		}
		
		// list remaining, invisible tabs
		
		// start by filling gaps between visible tabs
		int leftMostVisible = -1;
		int rightMostVisible = -1;
		
		for( int i = 0; i < allTabs.length; i++ ){
			if( allTabs[i] == null ){
				leftMostVisible = i;
				break;
			}
		}
		for( int i = allTabs.length-1; i >= 0; i-- ){
			if( allTabs[i] == null ){
				rightMostVisible = i;
				break;
			}
		}
		
		for( int i = leftMostVisible+1; i < rightMostVisible; i++ ){
			if( allTabs[i] != null ){
				result[ resultIndex++ ] = allTabs[i];
				allTabs[i] = null;
			}
		}
		
		// now fill up tabs to the left of the visible tabs
		for( int i = leftMostVisible-1; i >= 0; i-- ){
			if( allTabs[i] != null ){
				result[ resultIndex++ ] = allTabs[i];
				allTabs[i] = null;
			}
		}
		
		// now fill up tabs to the right of the visible tabs
		for( int i = Math.max( 0, rightMostVisible ); i < allTabs.length; i++ ){
			if( allTabs[i] != null ){
				result[ resultIndex++ ] = allTabs[i];
				allTabs[i] = null;
			}
		}
		
		return result;
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
				
				index = (index+1) % dockables.length;
			}
		}
		
		return locations;
	}
	
	/**
	 * Gets an array that has the same size as {@link TabPane#getDockables()},
	 * the tab at location <code>x</code> has the same {@link Dockable} as
	 * in the array returned by <code>getDockables</code>. This includes the
	 * possibility to have no tab at some locations.
	 * @return the tabs, may contain <code>null</code> values
	 * @throws IllegalStateException if there is no {@link TabPane} known
	 * to this block.
	 */
	public Tab[] getDockableTabMap(){
		TabPane pane = getPane();
		if( pane == null )
			throw new IllegalStateException( "missing the TabPane" );
		
		Tab[] tabs = getTabs();
		Dockable[] dockables = pane.getDockables();
		
		Tab[] result = new Tab[ dockables.length ];
		
		int index = 0;
		for( int i = 0; i < tabs.length; i++ ){
			Dockable check = tabs[i].getDockable();
			for( int j = 0; j < dockables.length; j++ ){
				if( dockables[index] == check ){
					result[index] = tabs[i];
					break;
				}
				
				index = (index+1) % dockables.length;
			}
		}
		return result;
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
	 * and size of all the {@link Tab}s managed by this block. This method
	 * must not add or remove tabs from the block.
	 */
	public abstract void doLayout();
	
	public void revalidate(){
		revalidate.revalidate();
	}
	
	public void setLayout( Size size ){
		if( size instanceof TabsSize ){
			Tab[] tabs = ((TabsSize)size).getTabs();
			
			// check current layout before doing too much work
			Tab[] current = getTabs();
			if( tabs.length == current.length ){
				boolean same = true;
				for( int i = 0; i < tabs.length && same; i++ ){
					same = tabs[i] == current[i];
				}
				if( same )
					return;
			}
			
			// the tabs are not the current tabs
			removeAllTabs();
			
			for( Tab tab : tabs ){
				tab = pane.putOnTab( tab.getDockable() );
				insertTab( tab );
			}
		}
		else{
			throw new IllegalArgumentException( "not a size created by this block" );
		}
	}
	
	public boolean isVisible(){
		return true;
	}
	
	public void setBounds( int x, int y, int width, int height ){
		bounds.setBounds( x, y, width, height );
		revalidate();
	}
	
	/**
	 * Gets the boundaries of this block.
	 * @return the boundaries
	 */
	public Rectangle getBounds(){
		return new Rectangle( bounds );
	}
	
	/**
	 * This {@link Size} contains an array of {@link Tab}s which are required
	 * to get this size. Instances of this class can be used for {@link TabsLayoutBlock#setLayout(Size)}
	 * @author Benjamin Sigg
	 */
	protected class TabsSize extends Size{
		/** tabs required for this size */
		private Tab[] tabs;
		
		public TabsSize( Type type, int width, int height, Tab[] tabs ){
			super( type, width, height );
			this.tabs = tabs;
		}

		public TabsSize( Type type, Dimension size, Tab[] tabs ){
			super( type, size );
			this.tabs = tabs;
		}
		
		/**
		 * Gets the tabs that are required for this size.
		 * @return the tabs
		 */
		public Tab[] getTabs(){
			return tabs;
		}
		
		/**
		 * Gets the number of tabs shown with this size.
		 * @return the number of tabs
		 */
		public int getTabCount(){
			return tabs.length;
		}
		
		@Override
		public String toString(){
			return "[width=" + getWidth() + ", height=" + getHeight() + ", tabs=" + Arrays.toString( tabs ) + "]";
		}
	}
	
	/**
	 * Calls {@link TabsLayoutBlock#doLayout()} from the event queue.
	 * @author Benjamin Sigg
	 */
	private class Revalidate implements Runnable{
		private boolean started = false;
		
		/**
		 * Schedules the revalidate event.
		 */
		public void revalidate(){
			if( !started ){
				started = true;
				EventQueue.invokeLater( this );
			}
		}
		
		public void run(){
			started = false;
			doLayout();
		}
	}
}
