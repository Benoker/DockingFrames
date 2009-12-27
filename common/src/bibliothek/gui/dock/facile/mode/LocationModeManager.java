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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.action.CExtendedModeAction;
import bibliothek.gui.dock.facile.mode.action.LocationModeAction;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.util.IconManager;

/**
 * {@link ModeManager} for the location of a {@link Dockable}. This manager is able to
 * work together with {@link CControl} or together with {@link DockController}. Clients
 * using it together with a {@link DockController} need to set icons for the 
 * modes manually, they can use the {@link IconManager} and the keys provided in
 * each mode (e.g. {@link NormalMode#ICON_IDENTIFIER}).
 * @author Benjamin Sigg
 */
public class LocationModeManager extends ModeManager<Location, LocationMode>{
	private NormalMode normalMode;
	private MaximizedMode maximizedMode;
	private MinimizedMode minimizedMode;
	private ExternalizedMode externalizedMode;
	
	/** a set of listeners that will be automatically added or removed from a {@link LocationMode} */
	private Map<Path, List<LocationModeListener>> listeners = new HashMap<Path, List<LocationModeListener>>();
	
	/**
	 * Creates a new manager. This manager will create modes using {@link CExtendedModeAction}s.
	 * @param control the control in whose realm this manager will work, not <code>null</code>
	 */
	public LocationModeManager( CControl control ){
		super( control.intern().getController() );
		
		normalMode = new NormalMode( control );
		maximizedMode = new MaximizedMode( control );
		minimizedMode = new MinimizedMode( control );
		externalizedMode = new ExternalizedMode( control );
		
		init( control );
	}
	
	/**
	 * Creates a new manager. This manager will create modes using {@link LocationModeAction}s.
	 * @param controller the controller in whose realm this manager will work
	 */
	public LocationModeManager( DockController controller ){
		super( controller );
		
		normalMode = new NormalMode( controller );
		maximizedMode = new MaximizedMode( controller );
		minimizedMode = new MinimizedMode( controller );
		externalizedMode = new ExternalizedMode( controller );
		
		init( null );
	}
	
	private void init( CControl control ){
		addModeManagerListener( new LocationModeListenerAdapter() );

		putMode( minimizedMode );
		putMode( normalMode );
		putMode( maximizedMode );
		putMode( externalizedMode );
	}
	
	/**
	 * Adds a listener to the mode with unique identifier <code>identifier</code>. If the
	 * mode is exchanged then this listener is automatically removed and may be re-added
	 * to the new mode.
	 * @param identifier the identifier of some mode (not necessarily registered yet).
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addListener( Path identifier, LocationModeListener listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		
		List<LocationModeListener> list = listeners.get( identifier );
		if( list == null ){
			list = new ArrayList<LocationModeListener>();
			listeners.put( identifier, list );
		}
		list.add( listener );
		
		LocationMode mode = getMode( identifier );
		if( mode != null ){
			mode.addLocationModeListener( listener );
		}
	}
	
	/**
	 * Removes a listener from the mode <code>identifier</code>.
	 * @param identifier the name of a mode
	 * @param listener the listener to remove
	 */
	public void removeListener( Path identifier, LocationModeListener listener ){
		List<LocationModeListener> list = listeners.get( identifier );
		if( list == null )
			return;
		
		list.remove( listener );
		if( list.isEmpty() )
			listeners.remove( identifier );
		
		LocationMode mode = getMode( identifier );
		if( mode != null ){
			mode.removeLocationModeListener( listener );
		}
	}
	
	/**
	 * Direct access to the mode handling "normal" {@link Dockable}s.
	 * @return the mode
	 */
	public NormalMode getNormalMode(){
		return normalMode;
	}

	/**
	 * Direct access to the mode handling "maximized" {@link Dockable}s.
	 * @return the mode
	 */
	public MaximizedMode getMaximizedMode(){
		return maximizedMode;
	}
	
	/**
	 * Direct access to the mode handling "minimized" {@link Dockable}s.
	 * @return the mode
	 */
	public MinimizedMode getMinimizedMode(){
		return minimizedMode;
	}
	
	/**
	 * Direct access to the mode handling "externalized" {@link Dockable}s.
	 * @return the mode
	 */
	public ExternalizedMode getExternalizedMode(){
		return externalizedMode;
	}
	
	@Override
	public LocationMode getCurrentMode( Dockable dockable ){
		while( dockable != null ){
			for( LocationMode mode : modes() ){
				if( mode.isCurrentMode( dockable ))
					return mode;
			}
			DockStation station = dockable.getDockParent();
			dockable = station == null ? null : station.asDockable();
		}
		
		return null;
	}
	
	/**
	 * Adds and removes listeners from {@link LocationMode}s according to the map
	 * {@link LocationModeManager#listeners}.
	 * @author Benjamin Sigg
	 */
	private class LocationModeListenerAdapter implements ModeManagerListener<Location, LocationMode>{
		public void modeAdded(	ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.setManager( LocationModeManager.this );
			
			List<LocationModeListener> list = listeners.get( mode.getUniqueIdentifier() );
			if( list != null ){
				for( LocationModeListener listener : list ){
					mode.addLocationModeListener( listener );
				}
			}
		}
		
		public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			mode.setManager( null );
			
			List<LocationModeListener> list = listeners.get( mode.getUniqueIdentifier() );
			if( list != null ){
				for( LocationModeListener listener : list ){
					mode.removeLocationModeListener( listener );
				}
			}
		}
		
		public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			// ignore
		}

		public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
			// ignore	
		}		
	}
}
