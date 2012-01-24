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

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * A handler for invoking {@link DockableStateListener}s. Can be used by {@link Dockable}s.<br>
 * <b>Note:</b>
 * <ul>
 * 	<li>This listener does not create any new information, it only combines events from other listeners.</li>
 * 	<li>This listener receives its events delayed in order to put as much information as possible into one event.</li>
 * 	<li>Due to this delay, this listener may seem to receive events too late. However, once all events are received, the
 * 	state described by this listener and by other listeners matches again.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public class DockableStateListenerManager {
	/** all the listeners that are currently registered */
	private List<DockableStateListener> listeners = new ArrayList<DockableStateListener>();

	/** the currently pending events */
	private int current = 0;

	/** the currently observer parent {@link DockStation} of {@link #dockable} */
	private DockStation parent;

	/** the element to observe */
	private Dockable dockable;

	/** whether {@link #dockable} currently is visible */
	private boolean dockableShowing;
	
	/** whether an event is about to be fired */
	private boolean firing = false;

	/** this listener is added to the parent of {@link #dockable} and forwards an event if it is triggered */
	private DockableStateListener dockableStateListener = new DockableStateListener(){
		public void changed( DockableStateEvent event ){
			int flags = event.getFlags();
			if( event.didLocationChange() ) {
				flags &= ~DockableStateEvent.FLAG_LOCATION_CHANGED;
				flags |= DockableStateEvent.FLAG_PARENT_LOCATION_CHANGED;
			}
			
			if( event.didParentSelectionChange() ){
				flags &= ~DockableStateEvent.FLAG_SELECTION;
				flags |= DockableStateEvent.FLAG_PARENT_SELECTION;
			}

			if( event.didShowingChange() ) {
				boolean newShowing = dockable.isDockableShowing();
				if( newShowing != dockableShowing ) {
					dockableShowing = newShowing;
					flags |= DockableStateEvent.FLAG_SHOWING;
				}
				else {
					flags &= ~DockableStateEvent.FLAG_SHOWING;
				}
			}

			event( flags );
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
			
			event( DockableStateEvent.FLAG_HIERARCHY );
			checkShowing();
		}

		public void controllerChanged( DockHierarchyEvent event ){
			checkShowing();
		}
	};

	/** this listener is added to {@link #parent} */
	private DockStationListener dockStationListener = new DockStationAdapter(){
		public void dockableShowingChanged( DockStation station, Dockable changed, boolean visible ){
			if( changed == dockable ){
				checkShowing();
			}
		}

		public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
			if( oldSelection == dockable || newSelection == dockable ){
				event( DockableStateEvent.FLAG_SELECTION );
			}
		}
		
		public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
			for( Dockable check : dockables ){
				if( check == dockable ){
					event( DockableStateEvent.FLAG_LOCATION_CHANGED );
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
	
	/**
	 * Checks whether the {@link Dockable} is showing and fires an event if the property changed.
	 */
	public void checkShowing(){
		boolean newShowing = dockable.isDockableShowing();
		
		if( dockableShowing != newShowing ){
			dockableShowing = newShowing;
			event( DockableStateEvent.FLAG_SHOWING );
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
	 * Informs this observer that <code>dockable</code> changed. The flags are created using the 
	 * constants from {@link DockableStateEvent}.
	 * @param flags the changes
	 */
	public void event( int flags ){
		current |= flags;
		if( !firing ){
			firing = true;
			EventQueue.invokeLater( new Runnable(){
				public void run(){
					firing = false;
					fireNow();
				}
			});
		}
	}

	/**
	 * Combines all collected events and fires them now.
	 */
	private void fireNow(){
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
		dockableShowing = dockable.isDockableShowing();
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
