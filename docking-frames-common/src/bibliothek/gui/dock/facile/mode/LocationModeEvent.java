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
package bibliothek.gui.dock.facile.mode;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * Information given to a {@link LocationModeListener}.
 * @author Benjamin Sigg
 */
public class LocationModeEvent {
	/** the source of the event */
	private LocationMode mode;
	/** the element which got the new mode */
	private Dockable dockable;
	/** the new location of {@link #dockable} */
	private Location location;
	/** the set of affected elements */
	private AffectedSet affected;
	/** the objects that are stored for some listeners */
	private Map<LocationModeListener, Object> map = new HashMap<LocationModeListener, Object>();
	
	/** flag indicating that the mode transition has been done */
	private boolean done = false;
	
	/** flag telling whether the operation was a success */
	private boolean success;
	
	/**
	 * Creates a new event.
	 * @param mode the source of the event
	 * @param location the new location of <code>dockable</code>, may be <code>null</code>
	 * @param dockable the element with the new mode
	 * @param affected the affected elements
	 */
	public LocationModeEvent( LocationMode mode, Location location, Dockable dockable, AffectedSet affected ){
		this.mode = mode;
		this.location = location;
		this.dockable = dockable;
		this.affected = affected;
	}
	
	/**
	 * Marks the mode transition as over. This method is normally called after 
	 * {@link Mode#apply(Dockable, Object, AffectedSet) apply} has finished its job. 
	 * {@link LocationModeListener}s might however prematurely call this method. In this case
	 * <code>apply</code> is not executed, but all remaining events are sent anyway.
	 * @param success whether the operation was a success  
	 */
	public void done(boolean success){
		done = true;
		this.success = success;
	}
	
	/**
	 * Tells whether the mode transition has been done or not.
	 * @return <code>true</code> if the transition is over
	 */
	public boolean isDone(){
		return done;
	}
	
	/**
	 * Assuming {@link #isDone()} is <code>true</code>, then this flag tells whether the operation was a success or not.
	 * If the operation was not a success, then the {@link #getDockable() dockable} was not moved at all, or was not
	 * moved to the correct location.
	 * @return whether the operation was a success
	 */
	public boolean isSuccess(){
		return success;
	}
	
	/**
	 * Gets the source of the event, the mode whose <code>apply</code> method was called.
	 * @return the source of the event
	 */
	public LocationMode getMode(){
		return mode;
	}
	
	/**
	 * Gets the location which {@link #getDockable() dockable} should have after
	 * <code>apply</code>. Note: this might not be the actual location the element gets.
	 * @return the expected location, not <code>null</code>
	 */
	public Location getLocation(){
		return location;
	}
	
	/**
	 * Gets the {@link Dockable} whose mode was, or is going to be, changed.
	 * @return the element
	 */
	public Dockable getDockable(){
		return dockable;
	}

	/**
	 * The set of elements that is affected.
	 * @return the elements
	 */
	public AffectedSet getAffected(){
		return affected;
	}
	
	/**
	 * Stores <code>object</code> in a map using <code>listener</code> as key. If this
	 * method is called by {@link LocationModeListener#applyStarting(LocationModeEvent)},
	 * then the object is available when {@link LocationModeListener#applyDone(LocationModeEvent)}
	 * is called.
	 * @param key the key, not <code>null</code>
	 * @param value the value, may be <code>null</code>
	 */
	public void setClientObject( LocationModeListener key, Object value ){
		map.put( key, value );
	}
	
	/**
	 * Gets some object that was stored earlier using key <code>key</code>.
	 * @param key the key
	 * @return the value, may be <code>null</code>
	 */
	public Object getClientObject( LocationModeListener key ){
		return map.get( key );
	}
}
