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
package bibliothek.gui.dock.common.intern;

import java.awt.EventQueue;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.event.CDockableLocationEvent;
import bibliothek.gui.dock.common.event.CDockableLocationListener;
import bibliothek.gui.dock.dockable.DockableStateEvent;
import bibliothek.gui.dock.dockable.DockableStateListener;
import bibliothek.util.FrameworkOnly;

/**
 * Helps {@link CDockable}s to keep track of their location and can automatically
 * fire events to a {@link CDockableLocationListener}.<br>
 * Clients should not instantiate this class directly, they should add a
 * {@link CDockableLocationListener} to a {@link CDockable} instead.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class CDockableLocationListenerManager {
	/** the owner of this manager */
	private CDockable dockable;
	
	/** listener to send events to */
	private CDockableLocationListener listener;
	
	/** the current location */
	private CLocation location;
	
	/** the current visibility state */
	private boolean showing;
	
	/** whether a delayed check is in progress */
	private boolean delayed = false;
	
	/** listener added to the intern {@link Dockable} of {@link #dockable} */
	private DockableStateListener dockableListener = new DockableStateListener(){
		public void changed( DockableStateEvent event ){
			check();
		}
	};
	
	/**
	 * Creates a new manager 
	 * @param dockable the dockable to monitor
	 */
	public CDockableLocationListenerManager( CDockable dockable ){
		this.dockable = dockable;
	}
	
	/**
	 * Sets the listener which will be informed about changes.
	 * @param listener the listener, can be <code>null</code>
	 */
	public void setListener( CDockableLocationListener listener ){
		if( this.listener == null ){
			dockable.intern().removeDockableStateListener( dockableListener );
		}
		this.listener = null;		
		if( listener != null ){
			check();
			this.listener = listener;
			dockable.intern().addDockableStateListener( dockableListener );
		}
	}
	
	private void check(){
		if( !delayed ){
			delayed = true;
			EventQueue.invokeLater( new Runnable(){
				public void run(){
					delayed = false;
					checkNow();
				}
			});
		}
	}

	private void checkNow(){
		boolean newShowing = dockable.isShowing();
		CLocation newLocation = dockable.getBaseLocation();
		
		boolean oldShowing = showing;
		CLocation oldLocation = location;
		
		this.showing = newShowing;
		this.location = newLocation;
		
		boolean locationEvent = false;
		if( !(newLocation == null && oldLocation == null) ){
			if( newLocation == null || !newLocation.equals( oldLocation )){
				locationEvent = true;
			}
		}
		
		if( locationEvent || (newShowing != oldShowing )){
			if( listener != null ){
				CDockableLocationEvent event = new CDockableLocationEvent( dockable, oldShowing, newShowing, oldLocation, newLocation );
				listener.changed( event );
			}
		}		
	}
}
