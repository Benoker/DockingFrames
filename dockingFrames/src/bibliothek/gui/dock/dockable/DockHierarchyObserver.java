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
package bibliothek.gui.dock.dockable;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;

/**
 * A helper class that is able to send {@link DockHierarchyEvent}s to
 * {@link DockHierarchyListener}s. A {@link Dockable} might have one instance
 * of an observer. Whenever the parent-station of the Dockable changes, it
 * calls {@link #update()} and the observer ensures that all events are
 * send properly. The client should call {@link #controllerChanged(DockController)} whenever
 * the {@link DockController} changes. This observer will automatically monitor the super-parents
 * of its owner and fire events when necessary.
 * @author Benjamin Sigg
 */
public class DockHierarchyObserver implements DockHierarchyListener{
	/** the currently observed parent */
	private DockElement parent;
    /** a list of listeners which are informed when the hierarchy changes */
    private List<DockHierarchyListener> hierarchyListeners = new ArrayList<DockHierarchyListener>();
    
    /** The Dockable for which events are generated */
    private Dockable owner;
    
    /**
     * Creates a new observer.
     * @param owner the Dockable for which events are fired
     */
    public DockHierarchyObserver( Dockable owner ){
    	this.owner = owner;
    }
	
	public void hierarchyChanged( DockHierarchyEvent event ){
		fireHierarchyChanged();
	}
	
	public void controllerChanged( DockHierarchyEvent event ){
		// ignore
	}
	
	/**
	 * Invoked by the owner in order to fire a {@link DockHierarchyEvent}
	 * @param controller the new controller
	 */
	public void controllerChanged( DockController controller ){
		fireControllerChanged( controller );
	}
	
	/**
	 * Stores a listener which is informed when the path of the owner has changed.
	 * @param listener the new listener
	 */
	public void addDockHierarchyListener( DockHierarchyListener listener ){
    	hierarchyListeners.add( listener );
    }
    
	/**
	 * Removes an earlier stored listener.
	 * @param listener the listener to remove
	 */
    public void removeDockHierarchyListener( DockHierarchyListener listener ){
    	hierarchyListeners.remove( listener );
    }
	
    /**
     * Fires a {@link DockHierarchyEvent} to all registered listeners.
     */
    protected void fireHierarchyChanged(){
    	if( !hierarchyListeners.isEmpty() ){
	    	DockHierarchyEvent event = new DockHierarchyEvent( owner );
	    	for( DockHierarchyListener listener : hierarchyListeners.toArray( new DockHierarchyListener[ hierarchyListeners.size() ] )){
	    		listener.hierarchyChanged( event );
	    	}
    	}
    }
    
    /**
     * Informs all listeners that the controller of the owner has been changed.
     * @param controller the new controller
     */
    protected void fireControllerChanged( DockController controller ){
    	if( !hierarchyListeners.isEmpty() ){
	    	DockHierarchyEvent event = new DockHierarchyEvent( owner, controller );
	    	for( DockHierarchyListener listener : hierarchyListeners.toArray( new DockHierarchyListener[ hierarchyListeners.size() ] )){
	    		listener.controllerChanged( event );
	    	}
    	}
    }
	
	/**
	 * Builds up a new path of parents and adds this listeners to
	 * each parent.
	 */
	public void update(){
	    DockElement old = parent;
	    parent = owner.getDockParent();
	    
	    if( old != parent ){
	        if( old != null && old.asDockable() != null )
	            old.asDockable().removeDockHierarchyListener( this );
	        
	        if( parent != null && parent.asDockable() != null )
	            parent.asDockable().addDockHierarchyListener( this );

	        fireHierarchyChanged();
	    }
	}
}