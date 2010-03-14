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
package bibliothek.gui.dock.station.support;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;

/**
 * A list consisting of {@link Dockable}s and sets of {@link Path}s as placeholder. 
 * Uses a {@link PlaceholderStrategy} to automatically create and dispose
 * of placeholders.<br>
 * Clients should call {@link #bind()} and {@link #unbind()} to manage the
 * lifecycle of this list.<br>
 * A {@link PlaceholderList} is not thread-safe.
 * @author Benjamin Sigg
 * @param <D> the type which represents a {@link Dockable}
 */
public class PlaceholderList<D extends PlaceholderListItem> {
	/** the current set of valid placeholders */
	private PlaceholderStrategy strategy;
	
	/** all the items of this list */
	private Entry head = null;
	
	/** head of the placeholders sublist */
	private Entry headPlaceholder = null;
	
	/** head of the dockables sublist */
	private Entry headDockable = null;
	
	/** identifiers for the various sublists this list consists of */
	public static enum Level{
		BASE, DOCKABLE, PLACEHOLDER;
	}
	
	/** view on all items */
	private SubList<Item<D>> allItems = new SubList<Item<D>>( Level.BASE ) {
		@Override
		protected Item<D> wrap( Item<D> item ){
			return item;
		}
		
		@Override
		protected boolean visible( Item<D> item ){
			return true;
		}
		
		@Override
		protected Item<D> unwrap( Item<D> item ){
			return item;
		}
	};
	
	/** view on all items as placeholder items */
	private SubList<Set<Path>> allPlaceholders = new SubList<Set<Path>>( Level.BASE ) {
		@Override
		protected Item<D> wrap( Set<Path> object ){
			return new Item<D>( object );
		}
		
		@Override
		protected boolean visible( Item<D> item ){
			return item.isPlaceholder();
		}
		
		@Override
		protected Set<Path> unwrap( Item<D> item ){
			Set<Path> result = item.getPlaceholderSet();
			if( result == null ){
				return Collections.emptySet();
			}
			return result;
		}
	};
	
	/** view on all pure placeholders */
	private SubList<Set<Path>> purePlaceholders = new SubList<Set<Path>>( Level.PLACEHOLDER ) {
		@Override
		protected Item<D> wrap( Set<Path> object ){
			return new Item<D>( object );
		}
		
		@Override
		protected boolean visible( Item<D> item ){
			return item.isPlaceholder();
		}
		
		@Override
		protected Set<Path> unwrap( Item<D> item ){
			return item.getPlaceholderSet();
		}
	};
	
	/** view on all dockables */
	private SubList<D> dockables = new SubList<D>( Level.DOCKABLE ) {
		@Override
		protected Item<D> wrap( D object ){
			return new Item<D>( object );
		}
		
		@Override
		protected boolean visible( Item<D> item ){
			return !item.isPlaceholder();
		}
		
		@Override
		protected D unwrap( Item<D> item ){
			return item.getDockable();
		}
	};
	
	/** a listener to {@link #strategy} */
	private PlaceholderStrategyListener listener = new PlaceholderStrategyListener() {
		public void placeholderInvalidated( Set<Path> placeholders ){
			removeAll( placeholders );
		}
	};
	
	/** whether {@link #bind()} has been called */
	private boolean bound = false;
	

	/**
	 * Creates a new and empty list.
	 */
	public PlaceholderList(){
		// nothing
	}
	
	/**
	 * Creates a new list reading all the data that is stored in <code>map</code>. This
	 * constructor stores all placeholders that are described in <code>map</code>, obsolete
	 * placeholders may be deleted as soon as a {@link PlaceholderStrategy} is set.
	 * @param map the map to read, not <code>null</code>
	 * @throws IllegalArgumentException if <code>map</code> was not written by a {@link PlaceholderList}
	 */
	public PlaceholderList( PlaceholderMap map ){
		read( map );
	}
	
	/**
	 * Reads the contents of <code>map</code> and adds them at the end of this list.
	 * @param map the map to read
	 * @throws IllegalArgumentException if the map is in the wrong format
	 */
	public void read( PlaceholderMap map ){
		if( !map.getFormat().equals( new Path( "dock.PlaceholderList") )){
			throw new IllegalArgumentException( "unknown format: " + map.getFormat() );
		}
		if( map.getVersion() != 0 ){
			throw new IllegalArgumentException( "version unknown: " + map.getVersion() );
		}
		
		Key[] placeholders = map.getPlaceholders();
		for( int i = placeholders.length-1; i>=0; i-- ){
			Path[] list = placeholders[i].getPlaceholders();
			Set<Path> paths = null;
			if( list.length > 0 ){
				paths = new HashSet<Path>();
				for( Path path : list ){
					paths.add( path );
				}
			}
			Item<D> item = new Item<D>( paths );
			
			if( map.contains( placeholders[i], "map" )){
				item.setPlaceholderMap( map.getMap( placeholders[i], "map" ) );
			}
			
			list().add( 0, item );
		}		
	}
	
	/**
	 * Converts this list into a {@link PlaceholderMap}, any remaining {@link Dockable} or
	 * {@link DockStation} will be converted into its placeholder using the currently installed
	 * {@link PlaceholderStrategy}. 
	 * @return the new map, not <code>null</code>
	 */
	public PlaceholderMap toMap(){
		PlaceholderMap map = new PlaceholderMap( new Path( "dock.PlaceholderList" ), 0 );
		
		for( Item<D> entry : list() ){
			Set<Path> placeholderSet = entry.getPlaceholderSet();
			PlaceholderMap placeholderMap = entry.getPlaceholderMap();
			
			Path additional = null;
			D dockable = entry.getDockable();
			if( strategy != null && dockable != null ){
				additional = strategy.getPlaceholderFor( dockable.asDockable() );
				if( placeholderMap == null ){
					DockStation station = dockable.asDockable().asDockStation();
					if( station != null ){
						placeholderMap = station.getPlaceholders();
					}
				}
			}
			
			Path[] placeholders = new Path[placeholderSet.size() + (additional == null ? 0 : 1)];
			placeholderSet.toArray( placeholders );
			if( additional != null ){
				placeholders[placeholders.length-1] = additional;
			}
			
			if( placeholders.length > 0 ){
				Key key =  map.newUniqueKey( placeholders );
				
				if( placeholderMap != null ){
					map.put( key, "map", placeholderMap );
				}
			}
		}
		
		return map;
	}
	
	/**
	 * Connects this list with its strategy.
	 */
	public void bind(){
		if( !bound ){
			bound = true;
			if( strategy != null ){
				strategy.addListener( listener );
				checkAllPlaceholders();
			}
		}
	}
	
	/**
	 * Disconnects this list from its strategy.
	 */
	public void unbind(){
		if( bound ){
			bound = false;
			if( strategy != null ){
				strategy.removeListener( listener );
			}
		}
	}
	
	/**
	 * Gets the current strategy of this list.
	 * @return the current strategy
	 */
	public PlaceholderStrategy getStrategy(){
		return strategy;
	}
	
	/**
	 * Sets the new strategy of this list. If the strategy is not <code>null</code>,
	 * then all current placeholders are checked and the invalid placeholders
	 * are removed.
	 * @param strategy the new strategy
	 */
	public void setStrategy( PlaceholderStrategy strategy ){
		if( bound ){
			if( this.strategy != null ){
				this.strategy.removeListener( listener );
			}
			this.strategy = strategy;
			if( this.strategy != null ){
				this.strategy.addListener( listener );
			}
			checkAllPlaceholders();
		}
		else{
			this.strategy = strategy;
		}
	}
	
	private void checkAllPlaceholders(){
		Iterator<Item<D>> iter = list().iterator();
		while( iter.hasNext() ){
			Item<D> item = iter.next();
			Set<Path> placeholders = item.getPlaceholderSet();
			
			Iterator<Path> paths = placeholders.iterator();
			while( paths.hasNext() ){
				if( !strategy.isValidPlaceholder( paths.next() )){
					paths.remove();
				}
			}
			if( placeholders.isEmpty() && item.isPlaceholder() ){
				iter.remove();
			}
		}
	}
	
	/**
	 * Gets a mutable view of all {@link Dockable}s of this list.
	 * @return the dockables
	 */
	public Filter<D> dockables(){
		return dockables;
	}
	
	/**
	 * Gets a mutable view of all pure placeholders of this list. A
	 * pure placeholder is an entry in this list with the dockable
	 * set to <code>null</code>
	 * @return the placeholders
	 */
	public Filter<Set<Path>> purePlaceholders(){
		return purePlaceholders();
	}
	
	/**
	 * Gets a mutable view of all elements of this list.
	 * @return the elements
	 */
	public Filter<Item<D>> list(){
		return allItems;
	}
	
	/**
	 * Gets a mutable view of all elements of this list.
	 * @return the elements, viewed as placeholders
	 */
	public Filter<Set<Path>> listPlaceholders(){
		return allPlaceholders;
	}
	
	/**
	 * Checks all entries of this list and removes all occurrences of all 
	 * paths stored in <code>placeholders</code>. If an entry remains with
	 * 0 placeholders and no {@link Dockable} it is removed.
	 * @param placeholders the placeholders to remove
	 */
	public void removeAll( Set<Path> placeholders ){
		Iterator<Item<D>> iter = list().iterator();
		while( iter.hasNext() ){
			Item<D> item = iter.next();
			item.removeAll( placeholders );
			if( item.getPlaceholderSet() == null && item.isPlaceholder() ){
				iter.remove();
			}
		}
	}
	
	/**
	 * Checks all entries of this list and removes all occurrences of all 
	 * <code>placeholder</code>. If an entry remains with 0 placeholders 
	 * and no {@link Dockable} it is removed.
	 * @param placeholder the placeholder to remove
	 */
	public void removeAll( Path placeholder ){
		Iterator<Item<D>> iter = list().iterator();
		while( iter.hasNext() ){
			Item<D> item = iter.next();
			item.remove( placeholder );
			if( item.getPlaceholderSet() == null && item.isPlaceholder() ){
				iter.remove();
			}
		}
	}
	
	/**
	 * Removes the <code>index</code>'th {@link Dockable} from this list were
	 * <code>index</code> is an index used in {@link #dockables()}.
	 * @param index the index of the element to remove
	 * @return the placeholder that replaces the element or <code>null</code>
	 */
	public Path remove( int index ){
		Entry entry = search( index, Level.DOCKABLE );
		if( entry == null ){
			throw new IllegalArgumentException( "no such dockable: " + index );
		}
		return removeDockable( entry );
	}
	
	/**
	 * Searches for <code>dockable</code> and replaces it by a placeholder. If <code>dockable</code>
	 * is a {@link DockStation}, then its {@link PlaceholderMap} is stored. 
	 * @param dockable the element to remove
	 * @return the placeholder that was inserted, <code>null</code> if the current strategy does
	 * not assign a placeholder to <code>dockable</code> or if <code>dockable</code> was not found in this list
	 */
	public Path remove( D dockable ){
		Entry entry = search( dockable );
		if( entry == null ){
			return null;
		}
		return removeDockable( entry );
	}
		
	private Path removeDockable( Entry entry ){
		D dockable = entry.item.getDockable();
		Path placeholder = strategy == null ? null : strategy.getPlaceholderFor( dockable.asDockable() );
		if( placeholder == null ){
			if( entry.item.hasPlaceholders() ){
				entry.item.setDockable( null );
			}
			else{
				entry.remove();
			}
		}
		else{
			entry.item.add( placeholder );
			entry.item.setDockable( null );
			DockStation station = dockable.asDockable().asDockStation();
			if( station != null ){
				entry.item.setPlaceholderMap( station.getPlaceholders() );
			}
		}
		return placeholder;
	}
	
	/**
	 * Searches for the first occurrence of <code>placeholder</code> and replaces
	 * it with <code>dockable</code>. If there is already another dockable stored at that
	 * location, then the other dockable is replaced silently. If <code>dockable</code> is a 
	 * {@link DockStation} and a {@link PlaceholderMap} is set, then this map is transfered to 
	 * <code>dockable</code> and removed from this list.<br>
	 * This method also removes all occurrences of <code>placeholder</code> from this list.
	 * @param placeholder
	 * @param dockable
	 * @return the index in {@link #dockables()} where <code>dockable</code> was inserted or -1 if
	 * <code>placeholder</code> was not found
	 */
	public int put( Path placeholder, D dockable ){
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		
		Entry entry = search( placeholder );
		if( entry == null ){
			return -1;
		}
		entry.set( new Item<D>( dockable, entry.item.getPlaceholderSet(), entry.item.getPlaceholderMap() ));
		DockStation station = dockable.asDockable().asDockStation();
		PlaceholderMap map = entry.item.getPlaceholderMap();
		if( station != null && map != null ){
			entry.item.setPlaceholderMap( null );
			station.setPlaceholders( map );
		}
		removeAll( placeholder );
		return entry.index( Level.DOCKABLE );
	}
	
	/**
	 * Emulates the insertion of a {@link Dockable} at location <code>placeholder</code> and
	 * returns the index that the inserted dockable would have in the dockable-list.
	 * @param placeholder the placeholder of the element to insert
	 * @return the location or -1 if <code>placeholder</code> was not found
	 */
	public int getDockableIndex( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ){
			return -1;
		}
		
		while( entry != null && entry.item.isPlaceholder() ){
			entry = entry.previous( Level.BASE );
		}
		
		if( entry == null ){
			return 0;
		}
		else{
			return entry.index( Level.DOCKABLE ) + 1;
		}
	}
	
	
	/**
	 * Searches for the entry containing <code>dockable</code> and adds <code>placeholder</code> to the
	 * placeholder set. This method removes <code>placeholder</code> from all the other entries.
	 * @param dockable the key
	 * @param placeholder the placeholder to insert
	 * @return <code>true</code> if <code>dockable</code> was found, <code>false</code> otherwise
	 */
	public boolean put( D dockable, Path placeholder ){
		Entry entry = search( dockable );
		if( entry == null ){
			return false;
		}
		removeAll( placeholder );
		entry.item.add( placeholder );
		return true;
	}
	
	/**
	 * Searches the first occurrence of <code>placeholder</code> and returns the {@link Dockable}
	 * that is stored at that location.
	 * @param placeholder the placeholder to search
	 * @return either the dockable or <code>null</code> if there is no dockable stored or
	 * <code>placeholder</code> is not found
	 */
	public D getDockableAt( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ){
			return null;
		}
		return entry.item.getDockable();
	}
	
	private Entry search( Path placeholder ){
		Entry entry = this.head;
		while( entry != null ){
			Set<Path> set = entry.item.getPlaceholderSet();
			if( set != null && set.contains( placeholder )){
				return entry;
			}
			entry = entry.next( Level.BASE );
		}
		return null;
	}
	
	private Entry search( D dockable ){
		Entry entry = head( Level.DOCKABLE );
		while( entry != null ){
			if( entry.item.getDockable() == dockable ){
				return entry;
			}
			entry = entry.next( Level.DOCKABLE );
		}
		return null;
	}
	
	private Entry search( int index, Level level ){
		Entry entry = head( level );
		
		while( entry != null && index > 0 ){
			entry = entry.next( level );
			index--;
		}
		return entry;
	}

	/**
	 * Searches the base entry at <code>index</code> and returns
	 * its location in sublist <code>level</code>.
	 * @param index the index of some entry
	 * @param level the sublist
	 * @return the index in the sublist or -1 if the entry is not part of <code>level</code>
	 * @throws IndexOutOfBoundsException if <code>index</code> is illegal
	 */
	public int baseToLevel( int index, Level level ){
		Entry entry = search( index, Level.BASE );
		if( entry == null ){
			throw new IndexOutOfBoundsException();
		}
		return entry.index( level );
	}
	

	/**
	 * Searches the base entry at <code>index</code> in the sublist <code>level</code> and returns
	 * its location in the base list.
	 * @param index the index of some entry
	 * @param level the sublist
	 * @return the index in the base list
	 * @throws IndexOutOfBoundsException if <code>index</code> is illegal
	 */
	public int levelToBase( int index, Level level ){
		Entry entry = search( index, level );
		if( entry == null ){
			throw new IndexOutOfBoundsException();
		}
		return entry.index( Level.BASE );
	}
	
	private Entry head( Level level ){
		switch( level ){
			case BASE: return head;
			case PLACEHOLDER: return headPlaceholder;
			case DOCKABLE: return headDockable;
		}
		throw new IllegalArgumentException();
	}
	
	private void invalidate(){
		dockables.invalidate();
		allPlaceholders.invalidate();
		purePlaceholders.invalidate();
		allItems.invalidate();
	}
	
	@Override
	public String toString(){
		return list().toString();
	}
	
	private class Entry{
		private Item<D> item;
		private boolean itemWasPlaceholder;
		
		private Entry next, previous;
		private Entry nextLevel, previousLevel;
		
		public Entry( Entry predecessor, Item<D> item ){
			this.item = item;
			insertAfter( predecessor );
		}
		
		public void insertAfter( Entry predecessor ){
			invalidate();
		
			item.owner = this;
			itemWasPlaceholder = item.isPlaceholder();
			
			Entry predecessorLevel = null;
			
			if( predecessor == null ){
				next = head;
				if( head != null ){
					head.previous = this;
				}
				
				head = this;
				predecessorLevel = null;
			}
			else{
				next = predecessor.next;
				if( next != null ){
					next.previous = this;
				}
				
				predecessor.next = this;
				this.previous = predecessor;
				
				Entry search = predecessor;
				while( search != null && predecessorLevel == null ){
					if( search.item.isPlaceholder() == item.isPlaceholder() ){
						predecessorLevel = search;
					}
					search = search.previous( Level.BASE );
				}
			}
	
			Entry successorLevel = null;
			if( predecessorLevel == null ){
				if( item.isPlaceholder() ){
					successorLevel = headPlaceholder;
					headPlaceholder = this;
				}
				else{
					successorLevel = headDockable;
					headDockable = this;
				}
			}
			
			if( predecessorLevel != null ){
				previousLevel = predecessorLevel;
				nextLevel = predecessorLevel.nextLevel;
				
				if( nextLevel != null ){
					nextLevel.previousLevel = this;
				}
				previousLevel.nextLevel = this;
			}
			else if( successorLevel != null ){
				nextLevel = successorLevel;
				successorLevel.previousLevel = this;
			}
		}
		
		public void move( int delta, Level level ){
			if( delta == 0 ){
				return;
			}
			Entry newPredecessor = this;
			if( delta > 0 ){
				for( int i = 0; i < delta; i++ ){
					newPredecessor = newPredecessor.next( level );
					if( newPredecessor == null ){
						throw new IllegalArgumentException( "delta too big" );
					}
				}
			}
			else{
				for( int i = -delta; i >= 0; i-- ){
					if( newPredecessor == null ){
						throw new IllegalArgumentException( "delta too big" );
					}
					newPredecessor = newPredecessor.previous( level );
				}
			}
			
			remove();
			insertAfter( newPredecessor );
		}
		
		public Entry next( Level level ){
			switch( level ){
				case BASE: return next;
				case PLACEHOLDER: return item.isPlaceholder() ? nextLevel : null;
				case DOCKABLE: return item.isPlaceholder() ? null : nextLevel;
			}
			throw new IllegalArgumentException();
		}
		
		public Entry previous( Level level ){
			switch( level ){
				case BASE: return previous;
				case PLACEHOLDER: return item.isPlaceholder() ? previousLevel : null;
				case DOCKABLE: return item.isPlaceholder() ? null : previousLevel;
			}
			throw new IllegalArgumentException();
		}
		
		public int index( Level level ){
			Entry entry = head( level );
			int index = 0;
			while( entry != this && entry != null ){
				entry = entry.next( level );
				index++;
			}
			if( entry == null ){
				return -1;
			}
			return index;
		}
		
		public void refresh(){
			set( item );
		}
		
		public void set( Item<D> item ){
			this.item.owner = null;
			item.owner = this;
			
			if( itemWasPlaceholder != item.isPlaceholder() ){
				itemWasPlaceholder = item.isPlaceholder();
				
				invalidate();
				removeLevel();
				
				Entry levelPredecessor = findLevelPredecessor( item.isPlaceholder() );
				Entry levelSuccessor = findLevelSuccessor( item.isPlaceholder() );
				
				if( levelPredecessor == null ){
					if( item.isPlaceholder() ){
						headPlaceholder = this;
					}
					else{
						headDockable = this;
					}
					previousLevel = null;
				}
				else{
					levelPredecessor.nextLevel = this;
					previousLevel = levelPredecessor;
				}
				
				if( levelSuccessor != null ){
					nextLevel = levelSuccessor;
					levelSuccessor.previousLevel = this;
				}
				else{
					nextLevel = null;
				}
			}
			this.item = item;
		}
		
		private Entry findLevelPredecessor( boolean placeholder ){
			Entry entry = previous;
			while( entry != null ){
				if( entry.item.isPlaceholder() == placeholder ){
					return entry;
				}
				entry = entry.previous;
			}
			return null;
		}
		
		private Entry findLevelSuccessor( boolean placeholder ){
			Entry entry = next;
			while( entry != null ){
				if( entry.item.isPlaceholder() == placeholder ){
					return entry;
				}
				entry = entry.next;
			}
			return null;
		}
		
		public void remove(){
			invalidate();
			
			if( next != null ){
				next.previous = previous;
			}
			if( previous != null ){
				previous.next = next;
			}
			
			if( this == head ){
				head = next;
			}
			
			next = null;
			previous = null;
			
			this.item.owner = null;
			
			removeLevel();
		}
		
		private void removeLevel(){
			invalidate();
			
			if( nextLevel != null ){
				nextLevel.previousLevel = previousLevel;
			}
			if( previousLevel != null ){
				previousLevel.nextLevel = nextLevel;
			}
			
			if( this == headDockable ){
				headDockable = nextLevel;
			}
			if( this == headPlaceholder ){
				headPlaceholder = nextLevel;
			}
			
			nextLevel = null;
			previousLevel = null;
		}
		
		@Override
		public String toString(){
			return item.toString();
		}
	}
	
	/**
	 * A single item in a {@link PlaceholderList}
	 * @author Benjamin Sigg
	 * @param <D> the type that represents a {@link Dockable}
	 */
	public static class Item<D extends PlaceholderListItem>{
		/** the value of this item, not <code>null</code> */
		private D value;
		/** all the placeholders that are associated with this item */
		private Set<Path> placeholderSet = null;
		/** Additional information about the placeholders of a child that is a {@link DockStation} */
		private PlaceholderMap placeholderMap;
		
		/** the container of this item */
		private PlaceholderList<D>.Entry owner;
		
		/**
		 * Creates a new item.
		 * @param dockable the value of this item, not <code>null</code>
		 */
		public Item( D dockable ){
			if( dockable == null )
				throw new IllegalArgumentException( "dockable must not be null" );
			this.value = dockable;
		}
		
		/**
		 * Creates a new item.
		 * @param dockable the value of this item, not <code>null</code>
		 * @param placeholderSet the placeholders of this item
		 * @param placeholderMap the childrens placeholder info
		 */
		public Item( D dockable, Set<Path> placeholderSet, PlaceholderMap placeholderMap ){
			if( dockable == null )
				throw new IllegalArgumentException( "dockable must not be null" );
			this.value = dockable;
			this.placeholderSet = placeholderSet;
			this.placeholderMap = placeholderMap;
		}
		
		/**
		 * Creates a new item.
		 * @param placeholders the value of this item, not <code>null</code>
		 */
		public Item( Set<Path> placeholders ){
			if( placeholders == null || placeholders.isEmpty() )
				throw new IllegalArgumentException( "placeholder must not be null nor empty" );
			placeholderSet = placeholders;
		}
		
		/**
		 * Tells whether this item is a pure placeholder or not.
		 * @return <code>true</code> if this item is only a placeholder, <code>false</code>
		 * if this item is a {@link Dockable}.
		 */
		public boolean isPlaceholder(){
			return value == null;
		}
		
		/**
		 * Returns the value of this placeholder.
		 * @return the placeholder or <code>null</code> if <code>this</code>
		 * has no a placeholders
		 * @see #isPlaceholder()
		 */
		public Set<Path> getPlaceholderSet(){
			return placeholderSet;
		}
		
		/**
		 * Sets the set of placeholders that are associated with this entry.
		 * @param placeholderSet the placeholders, can be <code>null</code>
		 */
		public void setPlaceholderSet( Set<Path> placeholderSet ){
			this.placeholderSet = placeholderSet;
		}
		
		/**
		 * Removes all placeholders that are in <code>placeholders</code>.
		 * @param placeholders the paths to remove
		 */
		public void removeAll( Set<Path> placeholders ){
			if( placeholderSet != null ){
				placeholderSet.removeAll( placeholders );
				if( placeholderSet.isEmpty() ){
					placeholderSet = null;
				}
			}
		}
		
		/**
		 * Removes <code>placeholder</code> from this entry.
		 * @param placeholder the placeholder to remove
		 */
		public void remove( Path placeholder ){
			if( placeholderSet != null ){
				placeholderSet.remove( placeholder );
				if( placeholderSet.isEmpty() ){
					placeholderSet = null;
				}
			}
		}
		
		/**
		 * Adds <code>placeholder</code> to the set of placeholders of this entry.
		 * @param placeholder the new placeholder
		 */
		public void add( Path placeholder ){
			if( placeholderSet == null ){
				placeholderSet = new HashSet<Path>();
			}
			placeholderSet.add( placeholder );
		}
		
		/**
		 * Tells whether this entry contains at least one placeholder
		 * @return <code>true</code> if there is at least one placeholder stored in this entry
		 */
		public boolean hasPlaceholders(){
			return placeholderSet != null && !placeholderSet.isEmpty();
		}
		
		/**
		 * Returns the value of this dockable item.
		 * @return the dockable or <code>null</code> if <code>this</code> 
		 * is a placeholder
		 * @see #isPlaceholder()
		 */
		public D getDockable(){
			return value;
		}
		
		/**
		 * Sets the value of this item.
		 * @param dockable the new value, can be <code>null</code>
		 */
		public void setDockable( D dockable ){
			this.value = dockable;
			owner.refresh();
		}
		
		/**
		 * Assuming this item represents a {@link Dockable} that is a {@link DockStation},
		 * sets the placeholder information of that {@link DockStation}.
		 * @param placeholders the placeholders, may be <code>null</code>
		 */
		public void setPlaceholderMap( PlaceholderMap placeholders ){
			this.placeholderMap = placeholders;
		}
		
		/**
		 * Gets the placeholder information of a child {@link DockStation}.
		 * @return the placeholder information or <code>null</code>
		 */
		public PlaceholderMap getPlaceholderMap(){
			return placeholderMap;
		}
		
		@Override
		public int hashCode(){
			return value.hashCode();
		}
		
		@Override
		public boolean equals( Object obj ){
			if( obj == this )
				return true;
			
			if( obj.getClass() == getClass() ){
				return value.equals( ((Item<?>)obj).value );
			}
			
			return false;
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			builder.append( "(dockable=" );
			if( value != null ){
				builder.append( value.asDockable().getTitleText() );
			}
			builder.append( ", placeholders={" );
			if( placeholderSet != null ){
				boolean first = true;
				for( Path path : placeholderSet ){
					if( first ){
						first = false;
					}
					else{
						builder.append( ", " );
					}
					builder.append( path.toString() );
				}
			}
			builder.append( "})" );
			return builder.toString();
		}
	}
	
	/**
	 * A sublist of a {@link PlaceholderList}, the elements in this
	 * list are filtered by a strategy that is defined by the
	 * {@link PlaceholderList}. Modifying this list has an effect
	 * in the enclosing {@link PlaceholderList}.
	 * @author Benjamin Sigg
	 *
	 * @param <M> the kind of data this filter offers
	 */
	public interface Filter<M> extends Iterable<M>{
		/**
		 * The number of elements that are in this list
		 * @return the length
		 */
		public int size();
		
		/**
		 * Gets the index'th element of this list.
		 * @param index the location of some element
		 * @return the element, not <code>null</code>
		 */
		public M get( int index );
		
		/**
		 * Adds <code>object</code> to this list, the object
		 * is added at the end of the list.
		 * @param object the new object, not <code>null</code>
		 */
		public void add( M object );
		
		/**
		 * Inserts <code>object</code> at location <code>index</code>
		 * of this list.
		 * @param index the location
		 * @param object the new element, not <code>null</code>
		 */
		public void add( int index, M object );
		
		/**
		 * Replaces the object at location <code>index</code> by <code>object</code>.
		 * @param index the location
		 * @param object the new element, not <code>null</code>
		 * @return the old object at that place
		 */
		public M set( int index, M object );
		
		/**
		 * Removes the object at location <code>index</code> from this list.
		 * @param index the location of some object
		 * @return the object that was removed, not <code>null</code>
		 */
		public M remove( int index );
		
		/**
		 * Removes the first occurrence of <code>object</code>. This method searches
		 * with help of the {@link Object#equals(Object)} method.
		 * @param object the object to remove, not <code>null</code>
		 * @return the index of the removed object or -1 if it was not found
		 */
		public int remove( M object );
		
		/**
		 * Searches the first occurrence of <code>object</code>. 
		 * @param object the object to search, not <code>null</code>
		 * @return the location or -1 if the object was not found
		 */
		public int indexOf( M object );
		
		/**
		 * Moves the item at location <code>source</code> to location <code>destination</code>.
		 * @param source the current location of some item
		 * @param destination the new location
		 */
		public void move( int source, int destination );
	}
	
	private abstract class SubList<A> implements Filter<A>{
		private Level level;
		private int size = -1;
		
		protected abstract A unwrap( Item<D> item );
		protected abstract Item<D> wrap( A item );
		protected abstract boolean visible( Item<D> value );
		
		public SubList( Level level ){
			this.level = level;
		}
		
		public void invalidate(){
			size = -1;
		}
		
		private Entry getEntry( int index ){
			if( index < 0 )
				throw new IndexOutOfBoundsException();
			
			Entry entry = head( level );
			while( index > 0 ){
				entry = entry.next( level );
				index--;
				if( entry == null ){
					throw new IndexOutOfBoundsException();
				}
			}
			
			return entry;
		}
		
		public void add( A object ){
			add( size(), object );
		}
		
		public void add( int index, A object ){
			if( size() == index ){
				Entry entry = head;
				Entry predecessor = null;
				while( entry != null ){
					predecessor = entry;
					entry = entry.next;
				}
				new Entry( predecessor, wrap( object ));
			}
			else{
				Entry entry = getEntry( index );
				new Entry( entry.previous( level ), wrap( object ));
			}
		}
		
		public A get( int index ){
			return unwrap( getEntry( index ).item );
		}
		
		public int indexOf( A object ){
			int index = 0;
			Entry entry = head( level );
			while( entry != null ){
				if( unwrap( entry.item ).equals( object )){
					return index;
				}
				entry = entry.next( level );
				index++;
			}
			return -1;
		}
		
		public A remove( int index ){
			Entry entry = getEntry( index );
			entry.remove();
			return unwrap( entry.item );
		}
		
		public int remove( A object ){
			int index = 0;
			Entry entry = head( level );
			while( entry != null ){
				if( unwrap( entry.item ).equals( object )){
					entry.remove();
					return index;
				}
				entry = entry.next( level );
				index++;
			}
			return -1;
		}
		
		public A set( int index, A object ){
			Entry entry = getEntry( index );
			A result = unwrap( entry.item );
			entry.set( wrap( object ));
			return result;
		}
		
		public int size(){
			if( size == -1 ){
				size = 0;
				Entry entry = head( level );
				while( entry != null ){
					size++;
					entry = entry.next( level );
				}
			}
			return size;
		}
		
		public void move( int source, int destination ){
			Entry entry = search( source, level );
			if( entry == null ){
				throw new IllegalArgumentException( "no entry for index: " + source );
			}
			int delta = destination - source;
			entry.move( delta, level );
		}
		
		public Iterator<A> iterator(){
			return new Iterator<A>() {
				private Entry current = null;
				private Entry next = head( level );
				
				public boolean hasNext(){
					return next != null;
				}
				
				public A next(){
					if( next == null ){
						throw new NoSuchElementException();
					}
					current = next;
					next = next.next( level );
					return unwrap( current.item );
				}
				
				public void remove(){
					if( current == null ){
						throw new IllegalStateException();
					}
					current.remove();
					current = null;
				}
			};
		}
		
		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for( A value : this ){
				if( first ){
					first = false;
				}
				else{
					builder.append( ", " );
				}
				builder.append( value );
			}
			return builder.toString();
		}
	}
}
