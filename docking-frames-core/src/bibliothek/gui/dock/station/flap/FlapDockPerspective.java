
/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.flap;

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/**
 * A representation of a {@link FlapDockStation} in a {@link Perspective}.<br>
 * <b>Note:</b> while this perspective allows to set the <code>hold</code> and <code>size</code>
 * property of a dockable, the final decision of how these properties look like are made
 * by the  {@link FlapLayoutManager} that is installed on the {@link FlapDockStation} which shows
 * the real {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class FlapDockPerspective implements PerspectiveDockable, PerspectiveStation{
	/** the owner of this dockable */
	private PerspectiveStation parent;
	
	/** all the children of this station */
	private PerspectivePlaceholderList<Item> dockables = new PerspectivePlaceholderList<Item>();

	private boolean defaultHold = false;
	private int defaultSize = 150;
	
	/**
	 * Updates the content of this perspective by reading the contents of <code>map</code>.
	 * @param map the placeholders
	 * @param children the possible children of this perspective
	 */
	public void read( PlaceholderMap map, final Map<Integer, PerspectiveDockable> children ){
		dockables.read( map, new PlaceholderListItemAdapter<PerspectiveDockable, Item>(){
			@Override
			public Item convert( ConvertedPlaceholderListItem item ){
				if( children == null ){
					return null;
				}
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				if( dockable != null ){
					boolean hold = item.getBoolean( "hold" );
					int size = item.getInt( "size" );
					
					Item element = new Item();
					element.dockable = dockable;
					element.hold = hold;
					element.size = size;
					dockable.setParent( FlapDockPerspective.this );
					return element;
				}
				return null;
			}
		});
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		if( getDockableCount() > 0 ){
			throw new IllegalStateException( "there are already children present on this station" );
		}
		
		dockables = new PerspectivePlaceholderList<Item>( placeholders );	
	}

	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}
	
	public PlaceholderMap toMap( final Map<PerspectiveDockable, Integer> children ){
		return dockables.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, Item>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, Item dockable ){
				ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "id", children.get( dockable.asDockable() ) );
				item.putInt( "index", index );
				item.putBoolean( "hold", dockable.hold );
				item.putInt( "size", dockable.size );
				
				Path placeholder = item.getPlaceholder();
				if( placeholder != null ){
					item.putString( "placeholder", placeholder.toString() );
					item.setPlaceholder( placeholder );
				}
				return item;
			}
		});
	}
	
	/**
	 * Gets the default size of the window of newly added {@link Dockable}s.
	 * @return the default size
	 */
	public int getDefaultSize(){
		return defaultSize;
	}
	
	/**
	 * Sets the default size of the window of newly added {@link Dockable}s. Changing
	 * this property has no effect on {@link Dockable}s that were already added to this
	 * station.
	 * @param defaultSize the default size, at least 0
	 */
	public void setDefaultSize( int defaultSize ){
		if( defaultSize < 0 ){
			throw new IllegalArgumentException( "defaultSize must be at least 0: " + defaultSize );
		}
		this.defaultSize = defaultSize;
	}
	
	/**
	 * Tells whether newly added {@link Dockable}s normally remain open even if they loose the
	 * focus.
	 * @return <code>true</code> if the elements remain open
	 */
	public boolean isDefaultHold(){
		return defaultHold;
	}
	
	/**
	 * Sets whether newly added {@link Dockable}s remain open even if they lost the focus.
	 * @param defaultHold <code>true</code> if the elements should remain open
	 */
	public void setDefaultHold( boolean defaultHold ){
		this.defaultHold = defaultHold;
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children at
	 * location <code>index</code> to the list of dockables.
	 * @param index the location of <code>dockable</code>
	 * @param dockable the element which is stored as placeholder
	 */
	public void insertPlaceholder( int index, PerspectiveDockable dockable ){
		Item item = new Item();
		item.dockable = dockable;
		
		dockables.dockables().add( index, item );
		dockables.dockables().remove( index );
	}
	
	/**
	 * Adds <code>placeholder</code> at location <code>index</code> in the list of items.
	 * @param index the location of <code>placeholder</code>
	 * @param placeholder the placeholder to add, not <code>null</code>
	 */
	public void insertPlaceholder( int index, Path placeholder ){
		dockables.list().insertPlaceholder( index, placeholder );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children to the end
	 * of the list of dockables.
	 * @param dockable the element which is stored as placeholder
	 */
	public void addPlaceholder( PerspectiveDockable dockable ){
		insertPlaceholder( getDockableCount(), dockable );
	}
	
	/**
	 * Adds <code>placeholder</code> at the end of the list of items.
	 * @param placeholder the placeholder to add, not <code>null</code>
	 */
	public void addPlaceholder( Path placeholder ){
		insertPlaceholder( dockables.list().size(), placeholder );
	}
	
	/**
	 * Adds <code>dockable</code> at the end of the list of children.
	 * @param dockable the new element
	 */
	public void add( PerspectiveDockable dockable ){
		add( dockable, defaultHold, defaultSize );
	}
	
	/**
	 * Adds <code>dockable</code> at the end of the list of children.
	 * @param dockable the new element
	 * @param hold whether <code>dockable</code> should remain open even if the focus is lost
	 * @param size the preferred size of <code>dockable</code>
	 */
	public void add( PerspectiveDockable dockable, boolean hold, int size ){
		insert( getDockableCount(), dockable, hold, size );
	}
	
	/**
	 * Adds a {@link Dockable} to this station.
	 * @param index the location of the new child
	 * @param dockable the new child, not <code>null</code>, must not have a parent
	 */
	public void insert( int index, PerspectiveDockable dockable ){
		insert( index, dockable, defaultHold, defaultSize );
	}
	
	/**
	 * Adds <code>dockable</code> to this station.
	 * @param index the location of <code>dockable</code>
	 * @param dockable the new element
	 * @param hold whether <code>dockable</code> should remain open even if the focus is lost
	 * @param size the preferred size of <code>dockable</code>
	 */
	public void insert( int index, PerspectiveDockable dockable, boolean hold, int size ){
		insert( index, dockable, hold, size, false );
	}
	
	private void insert( int index, PerspectiveDockable dockable, boolean hold, int size, boolean temporary ){
		if( size < 0 ){
			throw new IllegalArgumentException( "size must be >= 0" );
		}
		DockUtilities.ensureTreeValidity( this, dockable );
		
		Item item = new Item();
		item.dockable = dockable;
		item.hold = hold;
		item.size = size;
		
		dockables.dockables().add( index, item );
		dockable.setParent( this );
	}
	
	/**
	 * Removes the <code>index</code>'th element of this perspective.
	 * @param index the location of the element to remove
	 * @return the element that was removed
	 */
	public PerspectiveDockable remove( int index ){
		Item item = dockables.dockables().get( index );
		dockables.remove( item );
		item.dockable.setParent( null );
		return item.dockable;
	}
	
	public boolean remove( PerspectiveDockable dockable ){
		int index = indexOf( dockable );
		if( index >= 0 ){
			remove( index );
			return true;
		}
		return false;
	}
	
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		int index = indexOf( oldDockable );
		if( index < 0 ){
			throw new IllegalArgumentException( "oldDockable is not a child of this station" );
		}
		DockUtilities.ensureTreeValidity( this, newDockable );
		
		boolean hold = isHold( oldDockable );
		int size = getSize( oldDockable );
		remove( index );
		insert( index, newDockable, hold, size );
	}
	
	/**
	 * Gets the current index of <code>dockable</code>.
	 * @param dockable some dockable to search
	 * @return the index or -1 if not found
	 */
	public int indexOf( PerspectiveDockable dockable ){
		int count = 0;
		for( Item item : dockables.dockables() ){
			if( item.asDockable() == dockable ){
				return count;
			}
			count++;
		}
		return -1;
	}
	
	/**
	 * Sets whether <code>dockable</code> should stay open even if it lost focus.
	 * @param dockable the element whose state changes
	 * @param hold whether to keep <code>dockable</code> open
	 * @throws IllegalArgumentException if <code>dockable</code> is not known to this
	 * station
	 */
	public void setHold( PerspectiveDockable dockable, boolean hold ){
		item( dockable ).hold = hold;
	}
	
	/**
	 * Tells whether <code>dockable</code> should stay open even if it lost focus.
	 * @param dockable the element whose state is requested
	 * @return <code>true</code> if <code>dockable</code> should remain open
	 * @throws IllegalArgumentException if <code>dockable</code> is not known to this
	 * station
	 */
	public boolean isHold( PerspectiveDockable dockable ){
		return item( dockable ).hold;
	}
	
	/**
	 * Sets the preferred size of the window that shows <code>dockable</code>.
	 * @param dockable some child of this station
	 * @param size the preferred size, at least 0
	 * @throws IllegalArgumentException if either <code>dockable</code> is not a child
	 * of this station or if <code>size</code> is less than 0
	 */
	public void setSize( PerspectiveDockable dockable, int size ){
		if( size < 0 ){
			throw new IllegalArgumentException( "size must be >= 0: " + size );
		}
		item( dockable ).size = size;
	}
	
	/**
	 * Gets the preferred size of the window that shows <code>dockable</code>.
	 * @param dockable some child of this station
	 * @return the preferred size
	 * @throws IllegalArgumentException if <code>dockable</code> is not known to this station
	 */
	public int getSize( PerspectiveDockable dockable ){
		return item( dockable ).size;
	}
	
	public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target ){
		int index = indexOf( child );
		boolean hold = isHold( child );
		int size = getSize( child );
		
		Path placeholder = null;
		
		if( target != null ){
			placeholder = target.getPlaceholder();
		}
		else{
			placeholder = child.getPlaceholder();
		}
		
		return new FlapDockProperty( index, hold, size, placeholder );
	}
	
	private Item item( PerspectiveDockable dockable ){
		int index = indexOf( dockable );
		if( index < 0 ){
			throw new IllegalArgumentException( "not a child of this station: " + dockable );
		}
		return dockables.dockables().get( index );
	}
	
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;
	}
	
	public PerspectiveStation getParent(){
		return parent;
	}

	public Path getPlaceholder(){
		return null;
	}

	public PerspectiveDockable asDockable(){
		return this;
	}

	public PerspectiveStation asStation(){
		return this;
	}

	public String getFactoryID(){
		return FlapDockStationFactory.ID;
	}

	public PerspectiveDockable getDockable( int index ){
		return dockables.dockables().get( index ).dockable;
	}

	public int getDockableCount(){
		return dockables.dockables().size();
	}
	
	/**
	 * Represents a single child of this perspective
	 * @author Benjamin Sigg
	 */
	private static class Item implements PlaceholderListItem<PerspectiveDockable>{
		/** the child */
		public PerspectiveDockable dockable;
		/** whether the child is pinned down */
		public boolean hold;
		/** the preferred size of the window of the child */
		public int size;
		
		public PerspectiveDockable asDockable(){
			return dockable;
		}
	}
}
