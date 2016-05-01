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
package bibliothek.gui.dock.facile.mode.status;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;

/**
 * Abstract implementation of an {@link ExtendedModeEnablement}. This implementation
 * handles {@link ExtendedModeEnablementListener}s and tells subclasses when to
 * add or remove listeners to or from {@link Dockable}s.<br>
 * <b>Note:</b> Subclasses must call {@link #init()}.
 * @author Benjamin Sigg
 */
public abstract class AbstractExtendedModeEnablement implements ExtendedModeEnablement{
	/** the listeners registered at this enablement */
	private List<ExtendedModeEnablementListener> listeners = new ArrayList<ExtendedModeEnablementListener>();
	
	/** the manager in whose realm this enablement is used */
	private LocationModeManager<?> manager;
	
	/** a listener to {@link #manager} */
	private ModeManagerListener<Location, LocationMode> modeManagerListener = 
		new ModeManagerListener<Location, LocationMode>() {
			public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
				// ignore
			}
			
			public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
				// ignore	
			}
			
			public void modeAdded( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
				// ignore
			}
			
			public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
				disconnect( dockable );
			}
			
			public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
				connect( dockable );
			}
		};
	
	/**
	 * Creates a new enablement. Subclasses should call {@link #init()} afterwards.
	 * @param manager the manager in whose realm this enablement is used, not <code>null</code>
	 */
	public AbstractExtendedModeEnablement( LocationModeManager<?> manager ){
		this.manager = manager;
	}
	
	/**
	 * Gets the manager in whose realm this enablement is used.
	 * @return the manager, <code>null</code> if {@link #destroy()} has
	 * been called.
	 */
	public LocationModeManager<?> getManager(){
		return manager;
	}
	
	/**
	 * Initializes this enablement: adds a listener to {@link #getManager() the manager}
	 * and calls {@link #connect(Dockable)} for all known {@link Dockable}s.
	 */
	protected void init(){
		manager.addModeManagerListener( modeManagerListener );
		
		for( Dockable dockable : manager.listDockables() ){
			connect( dockable );
		}
	}
	
	public void addListener( ExtendedModeEnablementListener listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		listeners.add( listener );
	}

	public void removeListener( ExtendedModeEnablementListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Calls {@link ExtendedModeEnablementListener#availabilityChanged(Dockable, ExtendedMode, boolean)} for
	 * all listeners that are registered.
	 * @param dockable the element which is affected
	 * @param mode the mode which is affected
	 * @param available the new availability state
	 */
	protected void fire( Dockable dockable, ExtendedMode mode, boolean available ){
		ExtendedModeEnablementListener[] array = listeners.toArray( new ExtendedModeEnablementListener[ listeners.size() ] );
		for( ExtendedModeEnablementListener listener : array ){
			listener.availabilityChanged( dockable, mode, available );
		}
	}
	
	public void destroy(){
		if( manager != null ){
			manager.removeModeManagerListener( modeManagerListener );
			
			for( Dockable dockable : manager.listDockables() ){
				disconnect( dockable );
			}
			
			manager = null;
		}
	}

	/**
	 * Adds listeners to <code>dockable</code> in order to call {@link #fire(Dockable, ExtendedMode, boolean)}
	 * once the availability of some mode changed.
	 * @param dockable the element which needs new listeners
	 */
	protected abstract void connect( Dockable dockable );
	
	/**
	 * Removes listeners from <code>dockable</code> that were added earlier.
	 * @param dockable the element which is no longer to be connected
	 */
	protected abstract void disconnect( Dockable dockable );
}
