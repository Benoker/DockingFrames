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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.util.Path;

/**
 * A list consisting of {@link Dockable}s and sets of {@link Path}s as placeholder. 
 * Uses a {@link PlaceholderStrategy} to automatically create and dispose
 * of placeholders.<br>
 * Clients should call {@link #bind()} and {@link #unbind()} to manage the
 * lifecycle of this list.<br>
 * A {@link PlaceholderList} is not thread-safe.
 * @author Benjamin Sigg
 * @param <D> the kind of object that should be treated as {@link Dockable}
 * @param <S> the kind of object that should be treated as {@link DockStation}
 * @param <P> the type of item which represents a {@link Dockable}
 */
public abstract class PlaceholderList<D, S, P extends PlaceholderListItem<D>> {
	/**
	 * The {@link PlaceholderMap#getFormat() format} of the {@link PlaceholderMap}s that are created by this class.
	 */
	public static final Path PLACEHOLDER_MAP_FORMAT = new Path("dock.PlaceholderList");

	/** the current set of valid placeholders */
	private PlaceholderStrategy strategy;

	/** all the items of this list */
	private Entry head = null;

	/** head of the placeholders sublist */
	private Entry headPlaceholder = null;

	/** head of the dockables sublist */
	private Entry headDockable = null;

	/** identifiers for the various sublists this list consists of */
	public static enum Level {
		BASE, DOCKABLE, PLACEHOLDER;
	}

	/** view on all items */
	private SubList<Item> allItems = new SubList<Item>( Level.BASE ){
		@Override
		protected Item wrap( Item item ){
			return item;
		}

		@Override
		protected boolean visible( Item item ){
			return true;
		}

		@Override
		protected Item unwrap( Item item ){
			return item;
		}
	};

	/** view on all items as placeholder items */
	private SubList<Set<Path>> allPlaceholders = new SubList<Set<Path>>( Level.BASE ){
		@Override
		protected Item wrap( Set<Path> object ){
			return new Item( object );
		}

		@Override
		protected boolean visible( Item item ){
			return true;
		}

		@Override
		protected Set<Path> unwrap( Item item ){
			Set<Path> result = item.getPlaceholderSet();
			if( result == null ) {
				return Collections.emptySet();
			}
			return result;
		}
	};

	/** view on all pure placeholders */
	private SubList<Set<Path>> purePlaceholders = new SubList<Set<Path>>( Level.PLACEHOLDER ){
		@Override
		protected Item wrap( Set<Path> object ){
			return new Item( object );
		}

		@Override
		protected boolean visible( Item item ){
			return item.isPlaceholder();
		}

		@Override
		protected Set<Path> unwrap( Item item ){
			return item.getPlaceholderSet();
		}
	};

	/** view on all dockables */
	private SubList<P> dockables = new SubList<P>( Level.DOCKABLE ){
		@Override
		protected Item wrap( P object ){
			return new Item( object );
		}

		@Override
		protected boolean visible( Item item ){
			return !item.isPlaceholder();
		}

		@Override
		protected P unwrap( Item item ){
			return item.getDockable();
		}

		public void add( int index, P object ){
			super.add( index, object );
			removeDockable( object.asDockable() );
		}

		private void removeDockable( D dockable ){
			Path placeholder = getPlaceholder( dockable );
			if( placeholder != null ) {
				removeAll( placeholder );
			}

			S station = toStation( dockable );
			if( station != null ) {
				for( D child : getChildren( station ) ) {
					removeDockable( child );
				}
			}
		}
	};

	/** a listener to {@link #strategy} */
	private PlaceholderStrategyListener listener = new PlaceholderStrategyListener(){
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
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @throws IllegalArgumentException if <code>map</code> was not written by a {@link PlaceholderList}
	 */
	public PlaceholderList( PlaceholderMap map, PlaceholderListItemConverter<D, P> converter ){
		read( map, converter );
	}

	/**
	 * Gets the placeholder which matches <code>dockable</code>.
	 * @param dockable some random dockable
	 * @return the placeholder for <code>dockable</code>, can be <code>null</code>
	 */
	protected abstract Path getPlaceholder( D dockable );

	/**
	 * Gets a representation of <code>dockable</code> as string.
	 * @param dockable some random dockable, not <code>null</code>
	 * @return the text
	 */
	protected abstract String toString( D dockable );

	/**
	 * Converts <code>dockable</code> to the representation of a {@link DockStation}.
	 * @param dockable some random dockable
	 * @return <code>dockable</code> as station, can be <code>null</code>
	 */
	protected abstract S toStation( D dockable );

	/**
	 * Gets all the placeholders that are used by <code>station</code>.
	 * @param station some random representation of a {@link DockStation}
	 * @return the placeholders, can be <code>null</code>
	 */
	protected abstract PlaceholderMap getPlaceholders( S station );

	/**
	 * Sets all the placeholders that should be used by <code>station</code>.
	 * @param station a representation of a {@link DockStation}
	 * @param map the map of placeholders, not <code>null</code>
	 */
	protected abstract void setPlaceholders( S station, PlaceholderMap map );

	private boolean hasChildren( S station ){
		return getChildren( station ).length > 0;
	}
	
	/**
	 * Gets all the children of <code>station</code>.
	 * @param station some station whose children are searched
	 * @return the children
	 */
	protected abstract D[] getChildren( S station );

	/**
	 * Reads the contents of <code>map</code> and adds them at the end of this list.
	 * @param map the map to read
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @throws IllegalArgumentException if the map is in the wrong format
	 */
	public void read( PlaceholderMap map, PlaceholderListItemConverter<D, P> converter ){
		read( map, converter, false );
	}

	/**
	 * Reads the contents of <code>map</code>. This method can either add the contents at
	 * the end of this list, or just simulate a read. If a read is simulated, then the methods
	 * of <code>converter</code> are called just as if this would be an actual read, but
	 * in reality no data is changed in this list.
	 * @param map the data to read
	 * @param converter  used to convert items back to dockables, not <code>null</code> 
	 * @param simulate whether this list should actually be changed or not
	 * @throws IllegalArgumentException if the map is in the wrong format
	 */
	protected void read( PlaceholderMap map, PlaceholderListItemConverter<D, P> converter, boolean simulate ){
		if( converter == null ) {
			throw new IllegalArgumentException( "converter must not be null" );
		}

		if( !map.getFormat().equals( PLACEHOLDER_MAP_FORMAT ) ) {
			throw new IllegalArgumentException( "unknown format: " + map.getFormat() );
		}
		if( map.getVersion() != 0 ) {
			throw new IllegalArgumentException( "version unknown: " + map.getVersion() );
		}

		Key[] placeholders = map.getPlaceholders();
		for( int i = 0, n = placeholders.length; i < n; i++ ) {
			Set<Path> paths = null;
			if( !simulate ) {
				Path[] list = placeholders[i].getPlaceholders();

				if( list.length > 0 ) {
					paths = new HashSet<Path>();
					for( Path path : list ) {
						paths.add( path );
					}
				}
			}

			P dockable = null;

			if( map.contains( placeholders[i], "convert" ) ) {
				ConvertedPlaceholderListItem converted = new ConvertedPlaceholderListItem();

				Object[] keys = map.getArray( placeholders[i], "convert-keys" );

				for( Object convertKey : keys ) {
					String metaKey = (String) convertKey;
					converted.put( metaKey, map.get( placeholders[i], "dock." + metaKey ) );
				}

				if( map.contains( placeholders[i], "map" ) ) {
					converted.setPlaceholderMap( map.getMap( placeholders[i], "map" ) );
				}

				dockable = converter.convert( converted );
			}

			if( !simulate ) {
				Item item = null;
				if( dockable == null ) {
					if( paths != null && !paths.isEmpty() ) {
						item = new Item( paths );
					}
				}
				else {
					item = new Item( dockable, paths, null );
				}

				if( item != null ) {
					if( map.contains( placeholders[i], "map" ) ) {
						item.setPlaceholderMap( map.getMap( placeholders[i], "map" ) );
					}

					if( map.contains( placeholders[i], "item" ) ) {
						Object[] keys = map.getArray( placeholders[i], "item-keys" );

						for( Object itemKey : keys ) {
							String key = (String) itemKey;
							item.put( key, map.get( placeholders[i], "item." + key ) );
						}
					}

					list().add( item );
				}
			}
			if( dockable != null ) {
				converter.added( dockable );
			}
		}
	}

	/**
	 * Converts this list into a {@link PlaceholderMap}, any remaining {@link Dockable} or
	 * {@link DockStation} will be converted using <code>converter</code>.
	 * @param converter converter to translate dockables into persistent data, not <code>null</code>
	 * @return the new map, not <code>null</code>
	 */
	public PlaceholderMap toMap( PlaceholderListItemConverter<?, ? super P> converter ){
		if( converter == null ) {
			throw new IllegalArgumentException( "converter must not be null" );
		}

		PlaceholderMap map = new PlaceholderMap( PLACEHOLDER_MAP_FORMAT, 0 );
		int dockableIndex = 0;

		for( Item entry : list() ) {
			Set<Path> placeholderSet = entry.getPlaceholderSet();
			if( placeholderSet == null ) {
				placeholderSet = Collections.emptySet();
			}
			placeholderSet = new HashSet<Path>( placeholderSet );
			PlaceholderMap placeholderMap = entry.getPlaceholderMap();

			Path additional = null;
			P dockable = entry.getDockable();
			ConvertedPlaceholderListItem converted = null;

			if( dockable != null ) {
				converted = converter.convert( dockableIndex, dockable );
				if( converted != null ) {
					additional = converted.getPlaceholder();
					PlaceholderMap convertedMap = converted.getPlaceholderMap();
					if( convertedMap != null ){
						placeholderMap = convertedMap;
					}
				}
			}

			if( !entry.isPlaceholder() ) {
				dockableIndex++;
			}
			if( additional != null ) {
				placeholderSet.add( additional );
			}

			Path[] placeholders = placeholderSet.toArray( new Path[ placeholderSet.size() ] );
			
			if( placeholders.length > 0 || converted != null ) {
				Key key = map.newUniqueKey( placeholders );
				map.add( key );

				if( placeholderMap != null ) {
					map.put( key, "map", placeholderMap );
				}

				if( converted != null ) {
					map.put( key, "convert", true );
					String[] keys = converted.keys();
					map.put( key, "convert-keys", keys );

					for( String metaKey : keys ) {
						map.put( key, "dock." + metaKey, converted.get( metaKey ) );
					}
				}

				String[] itemKeys = entry.keys();
				if( itemKeys.length > 0 ) {
					map.put( key, "item", true );
					map.put( key, "item-keys", itemKeys );

					for( String itemKey : itemKeys ) {
						map.put( key, "item." + itemKey, entry.get( itemKey ) );
					}
				}
			}
		}

		return map;
	}

	/**
	 * Connects this list with its strategy.
	 */
	public void bind(){
		if( !bound ) {
			bound = true;
			if( strategy != null ) {
				strategy.addListener( listener );
				for( Item item : list() ) {
					item.setStrategy( strategy );
				}
				checkAllPlaceholders();
			}
		}
	}

	/**
	 * Disconnects this list from its strategy.
	 */
	public void unbind(){
		if( bound ) {
			bound = false;
			if( strategy != null ) {
				strategy.removeListener( listener );
				for( Item item : list() ) {
					item.setStrategy( null );
				}
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
		if( bound ) {
			if( this.strategy != null ) {
				this.strategy.removeListener( listener );
			}
			this.strategy = strategy;
			if( this.strategy != null ) {
				this.strategy.addListener( listener );
			}
			for( Item item : list() ) {
				item.setStrategy( strategy );
			}
			checkAllPlaceholders();
		}
		else {
			this.strategy = strategy;
		}
	}

	private void checkAllPlaceholders(){
		if( strategy != null ) {
			Iterator<Item> iter = list().iterator();
			while( iter.hasNext() ) {
				Item item = iter.next();
				Set<Path> placeholders = item.getPlaceholderSet();
				if( placeholders != null ) {
					Iterator<Path> paths = placeholders.iterator();
					while( paths.hasNext() ) {
						if( !strategy.isValidPlaceholder( paths.next() ) ) {
							paths.remove();
						}
					}
				}
				if( (placeholders == null || placeholders.isEmpty()) && item.isPlaceholder() ) {
					iter.remove();
				}
			}
		}
	}

	/**
	 * Inserts a placeholder for all {@link Dockable}s that are stored in this list.
	 */
	public void insertAllPlaceholders(){
		if( strategy != null ) {
			for( Item item : list() ) {
				P dockable = item.getDockable();
				if( dockable != null ) {
					Path placeholder = getPlaceholder( dockable.asDockable() );
					if( placeholder != null ) {
						item.add( placeholder );
					}
				}
			}
		}
	}

	/**
	 * Gets a mutable view of all {@link Dockable}s of this list.
	 * @return the dockables
	 */
	public Filter<P> dockables(){
		return dockables;
	}

	/**
	 * Gets a mutable view of all pure placeholders of this list. A
	 * pure placeholder is an entry in this list with the dockable
	 * set to <code>null</code>.
	 * @return the placeholders
	 */
	public Filter<Set<Path>> purePlaceholders(){
		return purePlaceholders;
	}

	/**
	 * Gets a mutable view of all elements of this list.
	 * @return the elements
	 */
	public Filter<Item> list(){
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
		Iterator<Item> iter = list().iterator();
		while( iter.hasNext() ) {
			Item item = iter.next();
			item.removeAll( placeholders );
			if( item.getPlaceholderSet() == null && item.isPlaceholder() ) {
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
		ensureRemoved( null, placeholder );
	}
	
	private void ensureRemoved( Item ignore, Path placeholder ){
		Iterator<Item> iter = list().iterator();
		while( iter.hasNext() ) {
			Item item = iter.next();
			if( item != ignore ){
				item.remove( placeholder );
				if( item.getPlaceholderSet() == null && item.isPlaceholder() ) {
					iter.remove();
				}
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
		if( entry == null ) {
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
	public Path remove( P dockable ){
		Entry entry = search( dockable );
		if( entry == null ) {
			return null;
		}
		return removeDockable( entry );
	}

	private Path removeDockable( Entry entry ){
		P dockable = entry.item.getDockable();
		Path placeholder = getPlaceholder( dockable.asDockable() );

		if( placeholder == null ) {
			entry.item.setDockable( null );
		}
		else {
			entry.item.add( placeholder );
			entry.item.setDockable( null );
		}
		
		S station = toStation( dockable.asDockable() );
		if( station != null ) {
			PlaceholderMap map = getPlaceholders( station );
			entry.item.setPlaceholderMap( map );
			if( map != null ){
				for( Key key : map.getPlaceholders() ){
					for( Path keyPlaceholder : key.getPlaceholders() ){
						entry.item.add( keyPlaceholder );
					}
				}
			}
		}
		
		if( !entry.item.hasPlaceholders() ){
			entry.remove();
		}
		
		return placeholder;
	}

	/**
	 * Searches for the first occurrence of <code>placeholder</code> and replaces
	 * it with <code>dockable</code>. If there is already another dockable stored at that
	 * location, then the other dockable is replaced silently. If <code>dockable</code> is a 
	 * {@link DockStation} and a {@link PlaceholderMap} is set, then this map is transferred to
	 * <code>dockable</code> and removed from this list, but only if the {@link DockStation} does not
	 * already have children.<br>
	 * This method also removes all occurrences of <code>placeholder</code> and the placeholder that is assigned
	 * by the current {@link PlaceholderStrategy} from this list.
	 * @param placeholder the placeholder to search, not <code>null</code>
	 * @param dockable the element which will replace <code>placeholder</code>, not <code>null</code>
	 * @return the index in {@link #dockables()} where <code>dockable</code> was inserted or -1 if
	 * <code>placeholder</code> was not found
	 */
	public int put( Path placeholder, P dockable ){
		if( dockable == null ) {
			throw new IllegalArgumentException( "dockable must not be null" );
		}

		Entry entry = search( placeholder );
		if( entry == null ) {
			return -1;
		}
		entry.set( new Item( dockable, entry.item.getPlaceholderSet(), entry.item.getPlaceholderMap() ) );
		S station = toStation( dockable.asDockable() );
		PlaceholderMap map = entry.item.getPlaceholderMap();
		if( station != null && map != null && !hasChildren( station )) {
			entry.item.setPlaceholderMap( null );
			setPlaceholders( station, map );
		}
		removeAll( placeholder );
		if( strategy != null ) {
			Path other = getPlaceholder( dockable.asDockable() );
			if( other != null && !other.equals( placeholder ) ) {
				removeAll( other );
			}
		}
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
		if( entry == null ) {
			return -1;
		}

		while( entry != null && entry.item.isPlaceholder() ) {
			entry = entry.previous( Level.BASE );
		}

		if( entry == null ) {
			return 0;
		}
		else {
			return entry.index( Level.DOCKABLE ) + 1;
		}
	}

	/**
	 * Searches the first occurrence of <code>placeholder</code> in a placeholder-set and returns
	 * the index of that entry on the {@link Level#BASE BASE level}.
	 * @param placeholder the placeholder to search
	 * @return its location or -1 if not found
	 */
	public int getListIndex( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ) {
			return -1;
		}

		return entry.index( Level.BASE );
	}
	
	/**
	 * Searches for an index in {@link #list()} described by
	 * <code>placeholder</code> or <code>index</code>.<br>
	 * This method calls {@link #insertAllPlaceholders()} if <code>placeholder</code> cannot
	 * be found.
	 * @param index the backup index, used if <code>placeholder</code> cannot be found
	 * @param placeholder a placeholder to search, can be <code>null</code>
	 * @return the location after <code>placeholder</code> or <code>index</code>
	 */
	public int getListIndex( int index, Path placeholder ){
		int result = -1;
		
		if( placeholder != null ){
			result = getListIndex( placeholder );
			if( result == -1 ){
				insertAllPlaceholders();
				result = getListIndex( placeholder );
			}
		}
		if( result == -1 ){
			result = index;
		}
		result = Math.min( result, list().size() );
		return result;
	}
	
	/**
	 * Searches for an index in {@link #list()} that follows the item described by
	 * <code>placeholder</code> or <code>index</code>.<br>
	 * This method calls {@link #insertAllPlaceholders()} if <code>placeholder</code> cannot
	 * be found.
	 * @param index the backup index, used if <code>placeholder</code> cannot be found
	 * @param placeholder a placeholder to search, can be <code>null</code>
	 * @return the location after <code>placeholder</code> or <code>index</code>
	 */
	public int getNextListIndex( int index, Path placeholder ){
		int result = getListIndex( index, placeholder );
		
		result++;
		result = Math.min( result, list().size() );
		return result;
	}

	/**
	 * Tells whether this list contains a reference to <code>placeholder</code>.
	 * @param placeholder the placeholder to search
	 * @return whether the placeholder was found
	 */
	public boolean hasPlaceholder( Path placeholder ){
		return search( placeholder ) != null;
	}

	/**
	 * Searches for the entry containing <code>dockable</code> and adds <code>placeholder</code> to the
	 * placeholder set. This method removes <code>placeholder</code> from all the other entries.
	 * @param dockable the key
	 * @param placeholder the placeholder to insert
	 * @return <code>true</code> if <code>dockable</code> was found, <code>false</code> otherwise
	 */
	public boolean put( P dockable, Path placeholder ){
		Entry entry = search( dockable );
		if( entry == null ) {
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
	public P getDockableAt( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ) {
			return null;
		}
		return entry.item.getDockable();
	}

	/**
	 * Gets the meta-map that is associated with the set of placeholders that include <code>placeholder</code>.
	 * @param placeholder some placeholder to search
	 * @return the meta map of the group containing <code>placeholder</code>, can be <code>null</code>
	 */
	public PlaceholderMetaMap getMetaMap( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ) {
			return null;
		}
		return entry.item;
	}

	/**
	 * Gets the {@link PlaceholderMap} that was used for a station at location <code>placeholder</code>.
	 * @param placeholder some placeholder to search
	 * @return the {@link PlaceholderMap} that was stored at the place where <code>placeholder</code> was found,
	 * can be <code>null</code>
	 */
	public PlaceholderMap getMap( Path placeholder ){
		Entry entry = search( placeholder );
		if( entry == null ) {
			return null;
		}
		return entry.item.placeholderMap;
	}
	
	/**
     * Searches for the one {@link Item} in the {@link #dockables} list that contains
     * <code>placeholder</code>.
     * @param placeholder the placeholder used for searching
     * @return the item with <code>placeholder</code> or <code>null</code>
     */
    public Item getItem( Path placeholder ){
    	int index = getListIndex( placeholder );
		if( index == -1 ){
			insertAllPlaceholders();
			index = getListIndex( placeholder );
		}
		if( index != -1 ){
			return list().get( index );
		}
		return null;
    }
    
    /**
     * Searches for the one {@link Item} in the {@link #dockables() dockables} list that
     * contains <code>dockable</code>.
     * @param dockable the dockable used for searching
     * @return the item representing <code>dockable</code> or <code>null</code>
     */
    public Item getItem( D dockable ){
    	Entry entry = search( dockable );
    	if( entry == null ){
    		return null;
    	}
    	return entry.item;
    }
	
    /**
     * Adds <code>placeholder</code> at the location of <code>dockable</code>. This method will remove <code>placeholder</code> from all
     * other locations.
     * @param dockable some dockable that is known to this list
     * @param placeholder a placeholder that should be added to the {@link Item} that represents <code>dockable</code>
     * @throws IllegalArgumentException if either argument is <code>null</code>, or if the location of <code>dockable</code> cannot be found
     */
    public void addPlaceholder( D dockable, Path placeholder ){
    	if( dockable == null ){
    		throw new IllegalArgumentException( "dockable must not be null" );
    	}
    	Item item = getItem( dockable );
    	if( item == null ){
    		throw new IllegalArgumentException( "unable to find item for dockable" );
    	}
    	
    	ensureRemoved( item, placeholder );
    	item.add( placeholder );
    }
    
	/**
	 * Gets the number of entries in the level <code>level</code>.
	 * @param level some level to count
	 * @return the size of that level
	 */
	public int size( Level level ){
		switch( level ){
			case BASE: return list().size();
			case DOCKABLE: return dockables().size();
			case PLACEHOLDER: return purePlaceholders().size();
			default: throw new IllegalArgumentException( "unknown level: " + level );
		}
	}

	private Entry search( Path placeholder ){
		Entry entry = this.head;
		while( entry != null ) {
			Set<Path> set = entry.item.getPlaceholderSet();
			if( set != null && set.contains( placeholder ) ) {
				return entry;
			}
			entry = entry.next( Level.BASE );
		}
		return null;
	}
	
	private Entry search( D dockable ){
		Entry entry = head( Level.DOCKABLE );
		while( entry != null ) {
			if( entry.item.getDockable().asDockable() == dockable ) {
				return entry;
			}
			entry = entry.next( Level.DOCKABLE );
		}
		return null;
	}

	private Entry search( P dockable ){
		Entry entry = head( Level.DOCKABLE );
		while( entry != null ) {
			if( entry.item.getDockable() == dockable ) {
				return entry;
			}
			entry = entry.next( Level.DOCKABLE );
		}
		return null;
	}

	private Entry search( int index, Level level ){
		Entry entry = head( level );

		while( entry != null && index > 0 ) {
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
		if( entry == null ) {
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
		if( entry == null ) {
			throw new IndexOutOfBoundsException();
		}
		return entry.index( Level.BASE );
	}

	/**
	 * Clears this list, all entries are removed
	 */
	public void clear(){
		head = null;
		headDockable = null;
		headPlaceholder = null;
		invalidate();
	}

	private Entry head( Level level ){
		switch( level ){
			case BASE:
				return head;
			case PLACEHOLDER:
				return headPlaceholder;
			case DOCKABLE:
				return headDockable;
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

	private class Entry {
		private Item item;
		private boolean itemWasPlaceholder;

		private Entry next, previous;
		private Entry nextLevel, previousLevel;

		public Entry( Entry predecessor, Item item ){
			this.item = item;
			insertAfter( predecessor );
		}

		public void insertAfter( Entry predecessor ){
			invalidate();

			item.setOwner( this );
			itemWasPlaceholder = item.isPlaceholder();

			Entry predecessorLevel = null;

			if( predecessor == null ) {
				next = head;
				if( head != null ) {
					head.previous = this;
				}

				head = this;
				predecessorLevel = null;
			}
			else {
				next = predecessor.next;
				if( next != null ) {
					next.previous = this;
				}

				predecessor.next = this;
				this.previous = predecessor;

				Entry search = predecessor;
				while( search != null && predecessorLevel == null ) {
					if( search.item.isPlaceholder() == item.isPlaceholder() ) {
						predecessorLevel = search;
					}
					search = search.previous( Level.BASE );
				}
			}

			Entry successorLevel = null;
			if( predecessorLevel == null ) {
				if( item.isPlaceholder() ) {
					successorLevel = headPlaceholder;
					headPlaceholder = this;
				}
				else {
					successorLevel = headDockable;
					headDockable = this;
				}
			}

			if( predecessorLevel != null ) {
				previousLevel = predecessorLevel;
				nextLevel = predecessorLevel.nextLevel;

				if( nextLevel != null ) {
					nextLevel.previousLevel = this;
				}
				previousLevel.nextLevel = this;
			}
			else if( successorLevel != null ) {
				nextLevel = successorLevel;
				successorLevel.previousLevel = this;
			}
		}

		public void move( int delta, Level level ){
			if( delta == 0 ) {
				return;
			}
			Entry newPredecessor = this;
			if( delta > 0 ) {
				for( int i = 0; i < delta; i++ ) {
					newPredecessor = newPredecessor.next( level );
					if( newPredecessor == null ) {
						throw new IllegalArgumentException( "delta too big" );
					}
				}
			}
			else {
				for( int i = -delta; i >= 0; i-- ) {
					if( newPredecessor == null ) {
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
				case BASE:
					return next;
				case PLACEHOLDER:
					return item.isPlaceholder() ? nextLevel : null;
				case DOCKABLE:
					return item.isPlaceholder() ? null : nextLevel;
			}
			throw new IllegalArgumentException();
		}

		public Entry previous( Level level ){
			switch( level ){
				case BASE:
					return previous;
				case PLACEHOLDER:
					return item.isPlaceholder() ? previousLevel : null;
				case DOCKABLE:
					return item.isPlaceholder() ? null : previousLevel;
			}
			throw new IllegalArgumentException();
		}

		public int index( Level level ){
			Entry entry = head( level );
			int index = 0;
			while( entry != this && entry != null ) {
				entry = entry.next( level );
				index++;
			}
			if( entry == null ) {
				return -1;
			}
			return index;
		}

		public void refresh(){
			set( item );
		}

		public void set( Item item ){
			this.item.setOwner( null );
			item.setOwner( this );

			if( itemWasPlaceholder != item.isPlaceholder() ) {
				itemWasPlaceholder = item.isPlaceholder();

				invalidate();
				removeLevel();

				Entry levelPredecessor = findLevelPredecessor( item.isPlaceholder() );
				Entry levelSuccessor = findLevelSuccessor( item.isPlaceholder() );

				if( levelPredecessor == null ) {
					if( item.isPlaceholder() ) {
						headPlaceholder = this;
					}
					else {
						headDockable = this;
					}
					previousLevel = null;
				}
				else {
					levelPredecessor.nextLevel = this;
					previousLevel = levelPredecessor;
				}

				if( levelSuccessor != null ) {
					nextLevel = levelSuccessor;
					levelSuccessor.previousLevel = this;
				}
				else {
					nextLevel = null;
				}
			}
			this.item = item;
		}

		private Entry findLevelPredecessor( boolean placeholder ){
			Entry entry = previous;
			while( entry != null ) {
				if( entry.item.isPlaceholder() == placeholder ) {
					return entry;
				}
				entry = entry.previous;
			}
			return null;
		}

		private Entry findLevelSuccessor( boolean placeholder ){
			Entry entry = next;
			while( entry != null ) {
				if( entry.item.isPlaceholder() == placeholder ) {
					return entry;
				}
				entry = entry.next;
			}
			return null;
		}

		public void remove(){
			invalidate();

			if( next != null ) {
				next.previous = previous;
			}
			if( previous != null ) {
				previous.next = next;
			}

			if( this == head ) {
				head = next;
			}

			next = null;
			previous = null;

			this.item.setOwner( null );

			removeLevel();
		}

		private void removeLevel(){
			invalidate();

			if( nextLevel != null ) {
				nextLevel.previousLevel = previousLevel;
			}
			if( previousLevel != null ) {
				previousLevel.nextLevel = nextLevel;
			}

			if( this == headDockable ) {
				headDockable = nextLevel;
			}
			if( this == headPlaceholder ) {
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
	 */
	public class Item extends PlaceholderMetaMap {
		/** the value of this item, can be <code>null</code> */
		private P value;
		/** all the placeholders that are associated with this item */
		private Set<Path> placeholderSet = null;
		/** Additional information about the placeholders of a child that is a {@link DockStation} */
		private PlaceholderMap placeholderMap;

		/** the container of this item */
		private PlaceholderList<D, S, P>.Entry owner;

		/**
		 * Creates a new item.
		 * @param dockable the value of this item, not <code>null</code>
		 */
		public Item( P dockable ){
			if( dockable == null )
				throw new IllegalArgumentException( "dockable must not be null" );
			this.value = dockable;
		}

		/**
		 * Creates a new item.
		 * @param dockable the value of this item, not <code>null</code>
		 * @param placeholderSet the placeholders of this item, can be <code>null</code>
		 * @param placeholderMap the children's placeholder info, can be <code>null</code>
		 */
		public Item( P dockable, Set<Path> placeholderSet, PlaceholderMap placeholderMap ){
			if( dockable == null )
				throw new IllegalArgumentException( "dockable must not be null" );
			this.value = dockable;
			this.placeholderMap = placeholderMap;
			setPlaceholderSet( placeholderSet );
		}

		/**
		 * Creates a new item.
		 * @param placeholders the value of this item, not <code>null</code>
		 */
		public Item( Set<Path> placeholders ){
			if( placeholders == null || placeholders.isEmpty() )
				throw new IllegalArgumentException( "placeholder must not be null nor empty" );
			setPlaceholderSet( placeholders );
		}

		/**
		 * Forwards <code>strategy</code> to the current {@link PlaceholderMap}.
		 * @param strategy the new strategy, can be <code>null</code>
		 */
		public void setStrategy( PlaceholderStrategy strategy ){
			if( placeholderMap != null ) {
				placeholderMap.setPlaceholderStrategy( strategy );
			}
		}

		/**
		 * Sets the owner of this list.
		 * @param owner the new owner, can be <code>null</code>
		 */
		protected void setOwner( PlaceholderList<D, S, P>.Entry owner ){
			if( bound && strategy != null ) {
				if( placeholderMap != null ) {
					if( owner == null ) {
						placeholderMap.setPlaceholderStrategy( null );
					}
					else {
						placeholderMap.setPlaceholderStrategy( strategy );
					}
				}
			}

			this.owner = owner;
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
			if( placeholderSet != null && placeholderSet.contains( null ) ) {
				throw new IllegalArgumentException( "placeholderSet contains a null value" );
			}
			this.placeholderSet = placeholderSet;
		}

		/**
		 * Removes all placeholders that are in <code>placeholders</code>.
		 * @param placeholders the paths to remove
		 */
		public void removeAll( Set<Path> placeholders ){
			if( placeholderSet != null ) {
				placeholderSet.removeAll( placeholders );
				if( placeholderSet.isEmpty() ) {
					placeholderSet = null;
				}
			}
			if( placeholderMap != null ) {
				placeholderMap.removeAll( placeholders, true );
				if( placeholderMap.isEmpty() ) {
					setPlaceholderMap( null );
				}
			}
		}

		/**
		 * Removes <code>placeholder</code> from this entry.
		 * @param placeholder the placeholder to remove
		 */
		public void remove( Path placeholder ){
			if( placeholderSet != null ) {
				placeholderSet.remove( placeholder );
				if( placeholderSet.isEmpty() ) {
					placeholderSet = null;
				}
			}
			if( placeholderMap != null ) {
				placeholderMap.removeAll( placeholder, true );
				if( placeholderMap.isEmpty() ) {
					setPlaceholderMap( null );
				}
			}
		}

		/**
		 * Adds <code>placeholder</code> to the set of placeholders of this entry.
		 * @param placeholder the new placeholder
		 */
		public void add( Path placeholder ){
			if( placeholder == null ) {
				throw new IllegalArgumentException( "placeholder must not be null" );
			}

			if( placeholderSet == null ) {
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
		 * Tells whether <code>placeholder</code> is known to this item or not.
		 * @param placeholder some placeholder to search
		 * @return <code>true</code> if this item stores <code>placeholder</code>
		 */
		public boolean hasPlaceholder( Path placeholder ){
			return placeholderSet != null && placeholderSet.contains( placeholder );
		}

		/**
		 * Returns the value of this dockable item.
		 * @return the dockable or <code>null</code> if <code>this</code> 
		 * is a placeholder
		 * @see #isPlaceholder()
		 */
		public P getDockable(){
			return value;
		}

		/**
		 * Sets the value of this item.
		 * @param dockable the new value, can be <code>null</code>
		 */
		public void setDockable( P dockable ){
			this.value = dockable;
			owner.refresh();
		}

		/**
		 * Assuming this item represents a {@link Dockable} that is a {@link DockStation},
		 * sets the placeholder information of that {@link DockStation}.
		 * @param placeholders the placeholders, may be <code>null</code>
		 */
		public void setPlaceholderMap( PlaceholderMap placeholders ){
			if( bound && strategy != null ) {
				if( this.placeholderMap != null ) {
					this.placeholderMap.setPlaceholderStrategy( null );
				}
				this.placeholderMap = placeholders;
				if( this.placeholderMap != null ) {
					this.placeholderMap.setPlaceholderStrategy( strategy );
				}
			}
			else {
				this.placeholderMap = placeholders;
			}
		}

		/**
		 * Gets the placeholder information of a child {@link DockStation}.
		 * @return the placeholder information or <code>null</code>
		 */
		public PlaceholderMap getPlaceholderMap(){
			return placeholderMap;
		}

		@Override
		public String toString(){
			StringBuilder builder = new StringBuilder();
			builder.append( "(dockable=" );
			if( value != null ) {
				builder.append( PlaceholderList.this.toString( value.asDockable() ) );
			}
			builder.append( ", placeholders={" );
			if( placeholderSet != null ) {
				boolean first = true;
				for( Path path : placeholderSet ) {
					if( first ) {
						first = false;
					}
					else {
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
	public interface Filter<M> extends Iterable<M> {
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
		 * Gets a mutable map which is associated with the <code>index</code>'th entry,
		 * the content of this map is stored persistently.
		 * @param index the location of some element
		 * @return the map associated with that element
		 */
		public PlaceholderMetaMap getMetaMap( int index );

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
		 * Adds a placeholder at location <code>index</code>, ensures that this
		 * placeholder is only present at <code>index</code>.
		 * @param index some location
		 * @param placeholder the placeholder to insert
		 */
		public void addPlaceholder( int index, Path placeholder );
		
		/**
		 * Generates a new entry containing only <code>placeholder</code>. Filters not able to
		 * show entries with only placeholders will still insert the entry, but the client cannot
		 * access it. 
		 * @param index the location of the new entry
		 * @param placeholder the content of the new entry
		 */
		public void insertPlaceholder( int index, Path placeholder );

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
		 * Searches the first occurence of <code>placeholder</code>.
		 * @param placeholder the placeholder to search
		 * @return the location of <code>placeholder</code> or <code>-1</code>
		 */
		public int indexOfPlaceholder( Path placeholder );
		
		/**
		 * Moves the item at location <code>source</code> to location <code>destination</code>.
		 * @param source the current location of some item
		 * @param destination the new location
		 */
		public void move( int source, int destination );

		/**
		 * Moves an item from <code>sourceList</code> to this list. 
		 * <ul>
		 * <li>If <code>sourceList</code> is <code>this</code>, then calling this method is equivalent of calling {@link #move(int, int)}.</li>
		 * <li>If <code>sourceList</code> is an inner object from the same {@link PlaceholderList}, then calling {@link #move(int, int)} of {@link PlaceholderList#list()} with adapted arguments is equivalent of calling this method. </li>
		 * <li>Otherwise the item will be removed from the <code>sourceList</code>, and a copy will be inserted into this list.</li>
		 * </ul>
		 * This method has to assume that <code>sourceList</code> is of the same type as <code>this</code>. This is <code>true</code> for
		 * all {@link Filter}s that were created by a {@link PlaceholderList}, but if clients implement the interface then this method will
		 * throw an {@link IllegalArgumentException} 
		 * @param sourceList the list to read from
		 * @param sourceIndex the current location of some item
		 * @param destination the new location
		 * @throws IllegalArgumentException if <code>sourceList</code> is of a type that is
		 * not recognized by this class, or if any of <code>sourceIndex</code> of <code>destination</code> is invalid
		 */
		public void move( Filter<M> sourceList, int sourceIndex, int destination );
	}

	private abstract class SubList<A> implements Filter<A> {
		private Level level;
		private int size = -1;

		protected abstract A unwrap( Item item );

		protected abstract Item wrap( A item );

		protected abstract boolean visible( Item value );

		public SubList( Level level ){
			this.level = level;
		}

		/**
		 * Gets the owner of this inner object.
		 * @return the owner
		 */
		public PlaceholderList<D, S, P> getPlaceholderList(){
			return PlaceholderList.this;
		}
		
		public void invalidate(){
			size = -1;
		}

		private Entry getEntry( int index ){
			if( index < 0 )
				throw new IndexOutOfBoundsException("index < 0: " + index);

			Entry entry = head( level );
			int start = index;
			
			while( index > 0 ) {
				entry = entry.next( level );
				index--;
				if( entry == null ) {
					throw new IndexOutOfBoundsException( "index=" + start + ", size=" + size() );
				}
			}

			return entry;
		}

		public void add( A object ){
			add( size(), object );
		}

		public void add( int index, A object ){
			insert( index, wrap( object ));
		}
		
		public void insertPlaceholder( int index, Path placeholder ){
			if( placeholder == null ){
				throw new IllegalArgumentException( "placeholder must not be null" );
			}
			
			Set<Path> placeholders = new HashSet<Path>();
			placeholders.add( placeholder );
			insert( index, new Item( placeholders ));
		}

		public void addPlaceholder( int index, Path placeholder ){
			Entry entry = search( index, level );
			if( entry == null ) {
				throw new IndexOutOfBoundsException();
			}
			if( !entry.item.hasPlaceholder( placeholder ) ) {
				removeAll( placeholder );
				entry.item.add( placeholder );
			}
		}
		
		private void insert( int index, Item item ){
			if( size() == index ) {
				Entry entry = head;
				Entry predecessor = null;
				while( entry != null ) {
					predecessor = entry;
					entry = entry.next;
				}
				new Entry( predecessor, item );
			}
			else {
				Entry entry = getEntry( index );
				new Entry( entry.previous( level ), item );
			}			
		}

		public PlaceholderMetaMap getMetaMap( int index ){
			return getEntry( index ).item;
		}

		public A get( int index ){
			return unwrap( getEntry( index ).item );
		}

		public int indexOf( A object ){
			int index = 0;
			Entry entry = head( level );
			while( entry != null ) {
				if( unwrap( entry.item ).equals( object ) ) {
					return index;
				}
				entry = entry.next( level );
				index++;
			}
			return -1;
		}
		
		public int indexOfPlaceholder( Path placeholder ){
			int index = 0;
			Entry entry = head( level );
			while( entry != null ) {
				if( entry.item.hasPlaceholder( placeholder )){
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
			while( entry != null ) {
				if( unwrap( entry.item ).equals( object ) ) {
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
			entry.set( wrap( object ) );
			return result;
		}
		
		public int size(){
			if( size == -1 ) {
				size = 0;
				Entry entry = head( level );
				while( entry != null ) {
					size++;
					entry = entry.next( level );
				}
			}
			return size;
		}

		public void move( int source, int destination ){
			Entry entry = search( source, level );
			if( entry == null ) {
				throw new IllegalArgumentException( "no entry for index: " + source );
			}
			int delta = destination - source;
			entry.move( delta, level );
		}

		public void move( Filter<A> sourceList, int sourceIndex, int destination ){
			if( sourceList == this ) {
				move( sourceIndex, destination );
				return;
			}
			if( !(sourceList instanceof SubList) ) {
				throw new IllegalArgumentException( "The type " + sourceList.getClass().getName() + " is not recognized" );
			}
			SubList<?> list = (SubList<?>) sourceList;
			if( list.getPlaceholderList() == getPlaceholderList() ){
				sourceIndex = levelToBase( sourceIndex, list.level );
				destination = levelToBase( destination, level );
				list.move( sourceIndex, destination );
				return;
			}
			
			
			Entry entry = list.getPlaceholderList().search( sourceIndex, list.level );
			if( entry == null ){
				throw new IllegalArgumentException( "sourceIndex out of bounds: " + sourceIndex );
			}
			
			if(destination == size()){
				destination = list().size();
			}
			else{
				destination = levelToBase( destination, level );
			}
			if( destination < 0 || destination > list().size() ){
				throw new IllegalArgumentException( "destination out of bounds: " + destination );
			}
			
			PlaceholderMap map = entry.item.placeholderMap;
			Set<Path> placeholderSet = entry.item.placeholderSet;
			P value = entry.item.value;
			entry.remove();
			
			Item item = new Item( value, placeholderSet, map );
			list().add( destination, item );
			if( bound ){
				item.setStrategy( getStrategy() );
				checkAllPlaceholders();
			}
		}

		public Iterator<A> iterator(){
			return new Iterator<A>(){
				private Entry current = null;
				private Entry next = head( level );

				public boolean hasNext(){
					return next != null;
				}

				public A next(){
					if( next == null ) {
						throw new NoSuchElementException();
					}
					current = next;
					next = next.next( level );
					return unwrap( current.item );
				}

				public void remove(){
					if( current == null ) {
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
			for( A value : this ) {
				if( first ) {
					first = false;
				}
				else {
					builder.append( ", " );
				}
				builder.append( value );
			}
			return builder.toString();
		}
	}
}
