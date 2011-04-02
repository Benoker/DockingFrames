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
package bibliothek.gui.dock.control.relocator;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.DirectRemoteRelocator;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.DockRelocatorMode;
import bibliothek.gui.dock.event.DockRelocatorListener;

/**
 * A manager adding {@link java.awt.event.MouseListener} and
 * {@link java.awt.event.MouseMotionListener} to every 
 * {@link DockElementRepresentative}s and handling the
 * drag and drop events.<br>
 * The behaviour of a drag and drop operation can be made dependent of the 
 * keys that are pressed, using some {@link DockRelocatorMode}s. These modes
 * are added through {@link #addMode(DockRelocatorMode)} 
 * @author Benjamin Sigg
 */
public abstract class AbstractDockRelocator implements DockRelocator{
	/** a set of listeners informed whenever a dockable is moved */
	@Deprecated
	private List<DockRelocatorListener> listeners = new ArrayList<DockRelocatorListener>();
	
	/** a set of listeners that are informed when a drag and drop operation happens */
	private List<VetoableDockRelocatorListener> vetoableListeners = new ArrayList<VetoableDockRelocatorListener>();
	
	/** the controller whose dockables are moved */
	private DockController controller;
	
    /** how many pixels the mouse must be moved until a title is dragged */
    private int dragDistance = 10;
    /** Whether a drag event can only be initialized by dragging a title or not */
    private boolean dragOnlyTitel = false;

    /** the list of all known modes */
    private List<DockRelocatorMode> modes = new ArrayList<DockRelocatorMode>();
    /** the set of the modes that are currently active */
    private Set<DockRelocatorMode> activeModes = new HashSet<DockRelocatorMode>();
    
    /** Algorithm to merge two {@link DockStation}s */
    private Merger merger = null;
    
	/**
	 * Creates a new manager.
	 * @param controller the controller whose dockables are moved
	 */
	public AbstractDockRelocator( DockController controller ){
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
	
	@Deprecated
	public void addDockRelocatorListener( DockRelocatorListener listener ){
		listeners.add( listener );
	}
	
	@Deprecated
	public void removeDockRelocatorListener( DockRelocatorListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets a list of all currently registered listeners.
	 * @return the list of listeners
	 */
	protected DockRelocatorListener[] listListeners(){
		return listeners.toArray( new DockRelocatorListener[ listeners.size() ] );
	}
	
	/**
	 * Informs all listeners that the drag-gesture has been made.
	 * @param dockable the element that will be dragged.
	 */
	protected void fireInit( Dockable dockable ){
	    for( DockRelocatorListener listener : listListeners() )
	        listener.init( controller, dockable );
	}
	
	/**
	 * Informs all listeners that a drag and drop operation has been canceled.
	 * @param dockable the element that was grabbed
	 */
	protected void fireCancel( Dockable dockable ){
	    for( DockRelocatorListener listener : listListeners() )
	        listener.cancel( controller, dockable );
	}
	
    /**
     * Informs all listeners that <code>dockable</code> will be dragged.
     * @param dockable the dragged Dockable
     * @param station the parent of <code>dockable</code>
     */
    protected void fireDrag( Dockable dockable, DockStation station ){
        for( DockRelocatorListener listener : listListeners() )
            listener.drag( controller, dockable, station );
    }
    
    /**
     * Informs all listeners that <code>dockable</code> was dropped on
     * <code>station</code>.
     * @param dockable the dropped Dockable
     * @param station the new owner of <code>dockable</code>
     */
    protected void fireDrop( Dockable dockable, DockStation station ){
        for( DockRelocatorListener listener : listListeners() )
            listener.drop( controller, dockable, station );
    }
    
	public void addVetoableDockRelocatorListener( VetoableDockRelocatorListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		vetoableListeners.add( listener );
	}
	
	public void removeVetoableDockRelocatorListener( VetoableDockRelocatorListener listener ){
		vetoableListeners.remove( listener );	
	}
	
	/**
	 * Gets all the {@link VetoableDockRelocatorListener} that are currently registered.
	 * @return all the listeners
	 */
	protected VetoableDockRelocatorListener[] vetoableListeners(){
		return vetoableListeners.toArray( new VetoableDockRelocatorListener[ vetoableListeners.size() ] );
	}
	
	/**
	 * Calls {@link VetoableDockRelocatorListener#grabbing(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireGrabbing( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.grabbing( event );
		}
	}

	/**
	 * Calls {@link VetoableDockRelocatorListener#grabbed(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireGrabbed( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.grabbed( event );
		}
	}
	
	/**
	 * Calls {@link VetoableDockRelocatorListener#dragged(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireDragged( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.dragged( event );
		}
	}
	
	/**
	 * Calls {@link VetoableDockRelocatorListener#dropping(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireDropping( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.dropping( event );
		}
	}
	
	/**
	 * Calls {@link VetoableDockRelocatorListener#canceled(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireDropped( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.dropped( event );
		}
	}
	
	/**
	 * Calls {@link VetoableDockRelocatorListener#grabbing(DockRelocatorEvent)} on
	 * all listeners that are currently registered.
	 * @param event the event to forward
	 */
	protected void fireCanceled( DockRelocatorEvent event ){
		for( VetoableDockRelocatorListener listener : vetoableListeners() ){
			listener.canceled( event );
		}
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
     * Gets an algorithm useful for merging two {@link DockStation}s.
     * @return the algorithm, can be <code>null</code>
     */
    public Merger getMerger(){
		return merger;
	}
    
    /**
     * Sets an algorithm for merging two {@link DockStation}s.
     * @param merger the new algorithm, can be <code>null</code>
     */
    public void setMerger( Merger merger ){
		this.merger = merger;
	}
    
    /**
     * Adds a mode to this relocator, a mode can be activated or deactivated
     * when the user presses a button like "ctrl" or "shift" during a 
     * drag and drop operation.
     * @param mode the new mode, not <code>null</code>
     */
    public void addMode( DockRelocatorMode mode ){
        if( mode == null )
            throw new IllegalArgumentException( "Mode must not be null" );
        modes.add( mode );
    }
    
    /**
     * Removes a mode that has earlier been added to this relocator.
     * @param mode the mode to remove
     */
    public void removeMode( DockRelocatorMode mode ){
        if( activeModes.remove( mode ))
            mode.setActive( controller, false );
        modes.remove( mode );
    }
    
    /**
     * Sets all {@link DockRelocatorMode}s to inactive.
     */
    protected void disableAllModes(){
        for( DockRelocatorMode mode : activeModes )
            mode.setActive( controller, false );
        activeModes.clear();
    }
    
    /**
     * Ensures that all {@link DockRelocatorMode}s are in the state that
     * fits the current set of modifiers.
     * @param modifiers the state of the last {@link MouseEvent}, see
     * {@link MouseEvent#getModifiersEx()}
     */
    protected void checkModes( int modifiers ){
        for( DockRelocatorMode mode : modes ){
            boolean active = mode.shouldBeActive( controller, modifiers );
            if( active ){
                if( activeModes.add( mode ))
                    mode.setActive( controller, true );
            }
            else{
                if( activeModes.remove( mode ))
                    mode.setActive( controller, false );
            }
        }
    }
}
