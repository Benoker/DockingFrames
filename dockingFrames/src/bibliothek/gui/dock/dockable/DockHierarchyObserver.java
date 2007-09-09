package bibliothek.gui.dock.dockable;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;

/**
 * A helper class that is able to send {@link DockHierarchyEvent}s to
 * {@link DockHierarchyListener}s. A {@link Dockable} might have one instance
 * of an observer. Whenever the parent-station of the Dockable changes, it
 * calls {@link #update()} and the observer ensures that all events are
 * send properly. The observer is notified when the any super-parent changes.
 * @author Benjamin Sigg
 */
public class DockHierarchyObserver implements DockHierarchyListener{
	/** the currently observed parent */
	private Dockable parent;
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
		Dockable old = parent;
		if( parent != null )
			parent.removeDockHierarchyListener( this );
		
		DockStation station = owner.getDockParent();
		
		if( station != null && station.asDockable() != null ){
			parent = station.asDockable();
			parent.addDockHierarchyListener( this );
		}
		else
			parent = null;
		
		if( old != parent ){
			fireHierarchyChanged();
		}
	}
}