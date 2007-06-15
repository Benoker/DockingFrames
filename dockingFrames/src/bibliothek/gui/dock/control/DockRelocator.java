package bibliothek.gui.dock.control;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockRelocationManagerListener;

/**
 * A manager adding {@link java.awt.event.MouseListener} and
 * {@link java.awt.event.MouseMotionListener} to every {@link bibliothek.gui.Dockable}
 * and {@link bibliothek.gui.dock.title.DockTitle} and handling the
 * drag and drop.
 * @author Benjamin Sigg
 */
public abstract class DockRelocator {
	/** a set of listeners informed whenever a dockable is moved */
	private List<DockRelocationManagerListener> listeners = new ArrayList<DockRelocationManagerListener>();
	/** the controller whose dockables are moved */
	private DockController controller;
	
    /** how many pixels the mouse must be moved until a title is dragged */
    private int dragDistance = 10;
    /** Whether a drag event can only be initialized by dragging a title or not */
    private boolean dragOnlyTitel = false;
	
	/**
	 * Creates a new manager.
	 * @param controller the controller whose dockables are moved
	 */
	public DockRelocator( DockController controller ){
		if( controller == null )
			throw new IllegalArgumentException( "controller must not be null" );
		
		this.controller = controller;
	}
	
	/**
	 * Gets the controller for which this relocator works.
	 * @return the controller
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Adds a listener to this manager. The listener will be informed whenever
	 * a {@link Dockable} is moved.
	 * @param listener the new listener
	 */
	public void addDockRelocationManagerListener( DockRelocationManagerListener listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes a listener from this manager.
	 * @param listener the listener to remove
	 */
	public void removeDockRelocationManagerListener( DockRelocationManagerListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets a list of all currently registered listeners.
	 * @return the list of listeners
	 */
	protected DockRelocationManagerListener[] listListeners(){
		return listeners.toArray( new DockRelocationManagerListener[ listeners.size() ] );
	}
	
    /**
     * Informs all listeners that <code>dockable</code> will be dragged.
     * @param dockable the dragged Dockable
     * @param station the parent of <code>dockable</code>
     */
    protected void fireDockableDrag( Dockable dockable, DockStation station ){
        for( DockRelocationManagerListener listener : listListeners() )
            listener.dockableDrag( controller, dockable, station );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> was dropped on
     * <code>station</code>.
     * @param dockable the dropped Dockable
     * @param station the new owner of <code>dockable</code>
     */
    protected void fireDockablePut( Dockable dockable, DockStation station ){
        for( DockRelocationManagerListener listener : listListeners() )
            listener.dockablePut( controller, dockable, station );
    }
    
    /**
     * Tells whether dockables can only be dragged through their title or not.
     * @return <code>true</code> if a Dockable must be dragged through their
     * titles, <code>false</code> if every part of the dockable can be
     * grabbed by the mouse.
     * @see #setDragOnlyTitel(boolean)
     */
    public boolean isDragOnlyTitel(){
		return dragOnlyTitel;
	}
    
    /**
     * Tells whether dockables can only be dragged through their title or not. 
     * @param dragOnlyTitel <code>true</code> if a Dockable must be dragged through its
     * title, <code>false</code> if every part of the dockable can be
     * grabbed by the mouse.
     */
    public void setDragOnlyTitel( boolean dragOnlyTitel ){
		this.dragOnlyTitel = dragOnlyTitel;
	}
    
    /**
     * Gets the distance the user must move the mouse in order to begin a 
     * drag operation.
     * @return the distance in pixel
     */
    public int getDragDistance(){
		return dragDistance;
	}

    /**
     * Sets the distance the user must move the mouse in order to begin a 
     * drag operation.
     * @param dragDistance the distance in pixel
     */
    public void setDragDistance( int dragDistance ){
		this.dragDistance = dragDistance;
	}
    
    /**
     * Tells whether the user has currently grabbed a dockable and moves
     * the dockable around.
     * @return <code>true</code> if a Dockable is currently dragged
     */
    public abstract boolean isOnMove();
    
    /**
     * Tells whether this relocator currently puts a Dockable. A Dockable
     * is put as soon as the user releases the mouse.
     * @return <code>true</code> if a Dockable is moved
     */
    public abstract boolean isOnPut();    
}
