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
package bibliothek.gui.dock.common.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CControlRegister;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.mode.LocationModeManagerPerspective;
import bibliothek.gui.dock.common.perspective.mode.LocationModePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;

/**
 * A {@link CPerspective} is a lightweight, modifiable representation of all {@link Dockable}s and {@link DockStation}s
 * handled by a {@link CControl}.<br>
 * When using a {@link CPerspective} clients have to be aware of:
 * <ul>
 * 	<li>Neither single- nor multiple-dockables need to be registered.</li>
 *  <li>Any root-{@link CStation} used by a {@link CControl} needs to be registered using {@link #addStation(CStationPerspective)}</li>
 * </ul> 
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.MAJOR, description="remove the warning about modes without perspective" )
public class CPerspective {
	/** All the stations of this perspective */
	private Map<String, CStationPerspective> stations = new HashMap<String, CStationPerspective>();
	
	/** All the dockables known to this perspective, only updated on a call to {@link #storeLocations()} */
	private Map<String, CDockablePerspective> dockables = new HashMap<String, CDockablePerspective>();
	
	/** a manager for finding the location of {@link CDockablePerspective}s */
	private LocationModeManagerPerspective locationModeManager;
	
	/** information about the {@link CControl} in whose realm this perspective is used */
	private CControlAccess control;
	
	/**
	 * Creates a new perspective
	 * @param control the owner of this perspective
	 */
	public CPerspective( CControlAccess control ){
		this.control = control;
		initLocations();
	}
	
	private void initLocations(){
		locationModeManager = new LocationModeManagerPerspective( this, control );
		CLocationModeManager manager = control.getLocationManager();
		
		for( CLocationMode mode : manager.modes() ){
			LocationModePerspective perspective = mode.createPerspective();
			if( perspective != null ){
				locationModeManager.addMode( perspective );
			}
			else{
				System.err.println( "warning: mode " + mode.getClass() + " does not provide perspective" );
			}
		}
	}
	
	/**
	 * Gets the representation of the {@link CLocationModeManager}, the representation
	 * is responsible for finding out what {@link ExtendedMode} and location a 
	 * {@link CDockablePerspective} has.
	 * @return the location manager, not <code>null</code>
	 */
	public LocationModeManagerPerspective getLocationManager(){
		return locationModeManager;
	}
	
	/**
	 * Stores the current location of all {@link CDockablePerspective}s currently known to this 
	 * {@link CPerspective}. The location is stored in the {@link LocationHistory} of each
	 * dockable.
	 */
	public void storeLocations(){
		Iterator<PerspectiveElement> elements = elements();
		while( elements.hasNext() ){
			PerspectiveElement dockable = elements.next();
			if( dockable instanceof CommonElementPerspective ){
				CDockablePerspective cdockable = ((CommonElementPerspective)dockable).getElement().asDockable();
				if( cdockable != null ){
					storeLocation( cdockable );
				}
			}
		}
	}
	
	/**
	 * Determines the current location of <code>dockable</code> and stores that location
	 * in a map using the {@link ExtendedMode} of the {@link Location} as key. If the
	 * user later clicks on one of the buttons like "minimize" or "externalize" this 
	 * location information is read and applied.<br>
	 * Also stores the dockables itself, if they are removed from their parents the perspective
	 * still knows of their existence
	 * @param dockable the element whose location should be stored
	 * @return the location that was stored or <code>null</code> if the location of
	 * <code>dockable</code> could not be determined
	 */
	public Location storeLocation( CDockablePerspective dockable ){
	    Location location = getLocationManager().getLocation( dockable );
	    if( location != null ){
	    	dockable.getLocationHistory().add( getLocationManager().getMode( location.getMode() ), location );

	    	String id = getId( dockable );
	    	
	    	if( id != null ){
	    		dockables.put( id, dockable );
	    	}
	    }
	    return location;
	}
	
	private String getId( CDockablePerspective dockable ){
		String id = null;
		if( dockable instanceof SingleCDockablePerspective ){
    		id = ((SingleCDockablePerspective)dockable).getUniqueId();
    		if( id != null ){
    			id = control.getRegister().toSingleId( id );
    		}
    	}
    	else if( dockable instanceof MultipleCDockablePerspective ){
    		id = ((MultipleCDockablePerspective)dockable).getUniqueId();
    		if( id != null ){
    			id = control.getRegister().toMultiId( id );
    		}
    	}
		return id;
	}
	
	/**
	 * Adds a new station to this perspective. If a station with name <code>id</code> is
	 * already registered, then this station gets replaced.<br>
	 * <b>WARNING: </b> the framework will not automatically create a {@link CStation}. The client needs 
	 * to register a {@link SingleCDockableFactory} in order to create the station when it is missing. 
	 * @param station the new station
	 */
	public void addStation( CStationPerspective station ){
		if( station == null ){
			throw new IllegalArgumentException( "station must not be null" );
		}
		stations.put( station.getUniqueId(), station );
		station.setPerspective( this );
	}
		
	/**
	 * Gets the station which was registered with the unique identifier <code>id</code>.
	 * @param id some unique identifier
	 * @return the station associated with <code>id</code>, can be <code>null</code>
	 */
	public CStationPerspective getStation( String id ){
		return stations.get( id );
	}
	
	/**
	 * Searches for the {@link SingleCDockablePerspective} or {@link MultipleCDockablePerspective} whose
	 * unique identifier is <code>id</code>. This method requires a call to {@link #storeLocations()}
	 * before it will return any results.
	 * @param id the unique identifier of a dockable, after {@link CControlRegister#toSingleId(String)}
	 * or {@link CControlRegister#toMultiId(String)} has been applied.
	 * @return the stored dockable, <code>null</code> if <code>id</code> is unknown or if 
	 * {@link #storeLocations()} was not executed
	 */
	public CDockablePerspective getDockable( String id ){
		return dockables.get( id );
	}
	
	/**
	 * Gets all the unique keys for {@link SingleCDockablePerspective}s and {@link MultipleCDockablePerspective}s.
	 * For this method to return the correct keys, {@link #storeLocations()} must have been executed.
	 * @return the keys of all the dockables that are currently known
	 */
	public String[] getDockableKeys(){
		return dockables.keySet().toArray( new String[ dockables.size() ] );
	}
	
	/**
	 * Removes the dockable with unique key <code>key</code> from the list of known dockables. If the
	 * dockable is still part of the tree, and {@link #storeLocations()} is called, then the dockable
	 * is reinserted into the list.
	 * @param key the unique identifier of the element to remove
	 * @return the element that was removed
	 */
	public CDockablePerspective removeDockable( String key ){
		return dockables.remove( key );
	}
	
	/**
	 * Stores <code>dockable</code> in the list of known dockables. This allows
	 * clients to add "invisible" dockables: {@link CDockable}s which are not yet visible
	 * but which already have some location information stored.
	 * @param dockable the new element, not <code>null</code>
	 */
	public void putDockable( CDockablePerspective dockable ){
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		String id = getId( dockable );
		if( id != null ){
			dockables.put( id, dockable );
		}
	}
		
	/**
	 * Gets the names of all the stations that were registered
	 * @return the names, not <code>null</code>
	 */
	public String[] getStationKeys(){
		return stations.keySet().toArray( new String[ stations.size() ] );
	}
	
	/**
	 * Gets a representation of the default {@link CContentArea}. If there are no
	 * stations for the perspective, then the missing stations are automatically 
	 * added to this perspective. 
	 * @return the area
	 */
	public CContentPerspective getContentArea(){
		return getContentArea( CControl.CONTENT_AREA_STATIONS_ID );
	}
	
	/**
	 * Gets a representation of the {@link CContentArea} with identifier <code>id</code>. If there are no
	 * stations for the perspective, then the missing stations are automatically 
	 * added to this perspective.
	 * @param id the unique identifier of the area 
	 * @return the area
	 */
	public CContentPerspective getContentArea( String id ){
		return new CContentPerspective( this, id );
	}
	
	/**
	 * Gets the {@link CStationPerspective} for the station that represents free floating dockables. This
	 * is equivalent of calling <code>getRoot( CControl.EXTERNALIZED_STATION_ID )</code>.<br>
	 * @return the station or <code>null</code> if there is no station registered with key
	 * {@link CControl#EXTERNALIZED_STATION_ID}
	 * @throws ClassCastException if the station named {@link CControl#EXTERNALIZED_STATION_ID} is not
	 * of type {@link CExternalizePerspective}
	 */
	public CExternalizePerspective getScreenStation(){
		return (CExternalizePerspective) getStation( CControl.EXTERNALIZED_STATION_ID );
	}
	
	/**
	 * Searches all occurrences of a {@link ShrinkablePerspectiveStation} and calls
	 * {@link ShrinkablePerspectiveStation#shrink() shrink} on them.
	 */
	public void shrink(){
		List<ShrinkablePerspectiveStation> elements = new ArrayList<ShrinkablePerspectiveStation>();
		Iterator<PerspectiveElement> iter = elements();
		while( iter.hasNext() ){
			PerspectiveElement next = iter.next();
			if( next instanceof ShrinkablePerspectiveStation ){
				elements.add( (ShrinkablePerspectiveStation)next );
			}
		}
		
		for( ShrinkablePerspectiveStation station : elements ){
			station.shrink();
		}
	}
	
	/**
	 * Gets an iterator that will visit all the {@link PerspectiveElement}s of this {@link CPerspective}. The
	 * iterator does not check whether this perspective is modified while it is in use.
	 * @return the iterator over all elements
	 */
	public Iterator<PerspectiveElement> elements(){
		return new ElementIterator();
	}
	
	private static class ElementFrame{
		public PerspectiveElement[] items;
		public int offset;
		
		public ElementFrame( PerspectiveElement[] items ){
			this.items = items;
		}
	}
	
	/**
	 * An iterator over all the {@link PerspectiveElement}s that are currently stored in this
	 * perspective. 
	 * @author Benjamin Sigg
	 */
	private class ElementIterator implements Iterator<PerspectiveElement>{
		private LinkedList<ElementFrame> stack = new LinkedList<ElementFrame>();
		
		public ElementIterator(){
			List<PerspectiveElement> items = new ArrayList<PerspectiveElement>();
			for( CStationPerspective station : stations.values() ){
				if( station.asDockable() == null || station.asDockable().getParent() == null ){
					items.add( station.intern() );
				}
			}
			
			stack.addFirst( new ElementFrame( items.toArray( new PerspectiveElement[ items.size() ] ) ) );
		}
		
		public boolean hasNext(){
			for( ElementFrame frame : stack ){
				if( frame.offset < frame.items.length ){
					return true;
				}
			}
			return false;
		}
		
		public PerspectiveElement next(){
			while( stack.size() > 0 ){
				ElementFrame top = stack.peek();
				if( top.offset < top.items.length ){
					PerspectiveElement result = top.items[top.offset++];
					
					PerspectiveStation station = result.asStation();
					if( station != null ){
						PerspectiveElement[] children = new PerspectiveElement[ station.getDockableCount() ];
						for( int i = 0; i < children.length; i++ ){
							children[ i ] = station.getDockable( i );
						}
						stack.addFirst( new ElementFrame( children ) );
					}
					
					return result;
				}
				stack.poll();
			}
			throw new NoSuchElementException();
		}
		
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}
}
