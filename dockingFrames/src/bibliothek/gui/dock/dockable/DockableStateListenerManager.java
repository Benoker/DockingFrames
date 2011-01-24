/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * A handler for invoking {@link DockableStateListener}s. Can be used by {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class DockableStateListenerManager {
	/** all the listeners that are currently registered */
	private List<DockableStateListener> listeners = new ArrayList<DockableStateListener>();

	/** whether events can be fired */
	private int count = 0;

	/** the currently pending events */
	private int current = 0;

	/** the currently observer parent {@link DockStation} of {@link #dockable} */
	private DockStation parent;

	/** the element to observe */
	private Dockable dockable;

	/** whether {@link #dockable} currently is visible */
	private boolean dockableVisible;

	/** this listener is added to the parent of {@link #dockable} and forwards an event if it is triggered */
	private DockableStateListener dockableStateListener = new DockableStateListener(){
		public void changed( DockableStateEvent event ){
			arm();

			int flags = event.getFlags();
			if( event.didPositionChange() ) {
				flags &= ~DockableStateEvent.FLAG_POSITION_CHANGED;
				flags |= DockableStateEvent.FLAG_PARENT_POSITION_CHANGED;
			}
			
			if( event.didParentSelectionChange() ){
				flags &= ~DockableStateEvent.FLAG_SELECTION;
				flags |= DockableStateEvent.FLAG_PARENT_SELECTION;
			}

			if( event.didVisibilityChange() ) {
				boolean newVisible = dockable.isDockableVisible();
				if( newVisible != dockableVisible ) {
					dockableVisible = newVisible;
					flags |= DockableStateEvent.FLAG_VISIBILITY;
				}
				else {
					flags &= ~DockableStateEvent.FLAG_VISIBILITY;
				}
			}

			event( flags );
			fire();
		}
	};

	/** hierarchy listener added to {@link #dockable} */
	private DockHierarchyListener dockHierarchyListener = new DockHierarchyListener(){
		public void hierarchyChanged( DockHierarchyEvent event ){
			if( parent != null ) {
				if( parent.asDockable() != null ) {
					parent.asDockable().removeDockableStateListener( dockableStateListener );
				}
				parent.removeDockStationListener( dockStationListener );
				parent = null;
			}
			parent = dockable.getDockParent();
			if( parent != null ) {
				if( parent.asDockable() != null ) {
					parent.asDockable().addDockableStateListener( dockableStateListener );
				}
				parent.addDockStationListener( dockStationListener );
			}
			
			arm();
			event( DockableStateEvent.FLAG_HIERARCHY );
			eventVisibility();
			fire();
		}

		public void controllerChanged( DockHierarchyEvent event ){
			// ignore
		}
	};

	/** this listener is added to {@link #parent} */
	private DockStationListener dockStationListener = new DockStationAdapter(){
		public void dockableVisibiltySet( DockStation station, Dockable changed, boolean visible ){
			if( changed == dockable ){
				eventVisibility();
			}
		}

		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			if( oldSelection == dockable || newSelection == dockable ){
				arm();
				event( DockableStateEvent.FLAG_SELECTION );
				fire();
			}
		}
		
		public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
			for( Dockable check : dockables ){
				if( check == dockable ){
					arm();
					event( DockableStateEvent.FLAG_POSITION_CHANGED );
					fire();		
					break;
				}
			}
		}
	};

	/**
	 * Creates a new manager.
	 * @param dockable the observed element
	 */
	public DockableStateListenerManager( Dockable dockable ){
		this.dockable = dockable;
	}
	
	private void eventVisibility(){
		boolean newVisible = dockable.isDockableVisible();
		
		if( dockableVisible != newVisible ){
			arm();
			dockableVisible = newVisible;
			event( DockableStateEvent.FLAG_VISIBILITY );
			fire();
		}
	}
	
	/**
	 * Adds a listener to this observer.
	 * @param listener the new listener
	 */
	public void addListener( DockableStateListener listener ){
		if( listener == null ) {
			throw new IllegalArgumentException( "listener must not be null" );
		}
		boolean has = hasListeners();
		listeners.add( listener );
		if( !has && hasListeners() ) {
			install();
		}
	}

	/**
	 * Removes a listener from this observer
	 * @param listener the listener to remove
	 */
	public void removeListener( DockableStateListener listener ){
		boolean has = hasListeners();
		listeners.remove( listener );
		if( has && !hasListeners() ) {
			uninstall();
		}
	}

	/**
	 * Tells whether there is at least one {@link DockableStateListener} registered.
	 * @return whether there is at least one listener
	 */
	protected boolean hasListeners(){
		return listeners.size() > 0;
	}

	/**
	 * Prepares this observer for combining several events and fire them together. Has to be
	 * followed by a call to {@link #fire()}. This method may be called more than once in a 
	 * row, in this case {@link #fire()} has to be called multiple times too.
	 */
	public void arm(){
		count++;
	}

	/**
	 * Informs this observer that <code>dockable</code> changed. The flags are created using the 
	 * constants from {@link DockableStateEvent}.
	 * @param dockable the element which changed
	 * @param flags the changes
	 */
	public void event( int flags ){
		if( count == 0 ) {
			throw new IllegalStateException( "this observer is not armed" );
		}

		current |= flags;
	}

	/**
	 * Combines all collected events since the call to {@link #arm()} and fires them. If {@link #arm()}
	 * was called more than once, then this method does nothing until it was called the same number of
	 * times.
	 */
	public void fire(){
		if( count == 0 ) {
			throw new IllegalStateException( "observer is not armed" );
		}
		count--;
		if( count == 0 ) {
			fireNow();
		}
	}

	/**
	 * Combines all collected events and fires them now.
	 */
	public void fireNow(){
		if( current != 0 ) {
			if( listeners.size() > 0 ) {
				DockableStateEvent event = new DockableStateEvent( dockable, current );
				current = 0;
				for( DockableStateListener listener : listeners.toArray( new DockableStateListener[listeners.size()] ) ) {
					listener.changed( event );
				}
			}
			current = 0;
		}
	}

	/**
	 * Activates this observer.
	 */
	protected void install(){
		dockable.addDockHierarchyListener( dockHierarchyListener );
		parent = dockable.getDockParent();
		if( parent != null ) {
			if( parent.asDockable() != null ){
				parent.asDockable().addDockableStateListener( dockableStateListener );
			}
			parent.addDockStationListener( dockStationListener );
		}
		dockableVisible = dockable.isDockableVisible();
	}

	/**
	 * Deactivates this observer.
	 */
	protected void uninstall(){
		dockable.removeDockHierarchyListener( dockHierarchyListener );
		if( parent != null ) {
			if( parent.asDockable() != null ){
				parent.asDockable().removeDockableStateListener( dockableStateListener );
			}
			parent.removeDockStationListener( dockStationListener );
			parent = null;
		}
	}
}
