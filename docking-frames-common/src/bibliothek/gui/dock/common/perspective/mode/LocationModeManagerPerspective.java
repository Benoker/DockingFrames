/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.perspective.mode;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.mode.CLocationModeManager;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStationPerspective;
import bibliothek.gui.dock.common.perspective.CommonElementPerspective;
import bibliothek.gui.dock.common.perspective.LocationHistory;
import bibliothek.gui.dock.common.perspective.MultipleCDockablePerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.facile.mode.CLocationModeSettings;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationSettingConverter;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/**
 * Represents a {@link CLocationModeManager} and offers methods to find the current location and
 * {@link ExtendedMode} of a {@link CDockablePerspective}.
 * @author Benjamin Sigg
 */
public class LocationModeManagerPerspective {
	/** the owner of this manager */
	private CPerspective perspective;
	
	/** all the modes known to this manager */
	private Map<ExtendedMode, LocationModePerspective> modes = new HashMap<ExtendedMode, LocationModePerspective>();
	
	/** all the factories that creates {@link ModeSetting}s */
	private Map<Path, ModeSettingFactory<Location>> settingFactories = new HashMap<Path, ModeSettingFactory<Location>>();
	
	/**
	 * Creates a new manager
	 * @param perspective the owner of this manager, not <code>null</code>
	 * @param control the control which is wrapped by <code>perspective</code>
	 */
	public LocationModeManagerPerspective( CPerspective perspective, CControlAccess control ){
		this.perspective = perspective;
		
		for( ModeSettingFactory<Location> factory : control.getLocationManager().getFactories() ){
			settingFactories.put( factory.getModeId(), factory );
		}
	}
	
	/**
	 * Gets the {@link ExtendedMode} whose identifier is equal to
	 * <code>identifier</code>.
	 * @param identifier some identifier of a mode
	 * @return the mode or <code>null</code> if not found
	 */
	public ExtendedMode getMode( Path identifier ){
		for( ExtendedMode mode : modes.keySet() ){
			if( mode.getModeIdentifier().equals( identifier )){
				return mode;
			}
		}
		return null;
	}
	
	/**
	 * Adds <code>mode</code> to this manager.
	 * @param mode the additional mode
	 */
	public void addMode( LocationModePerspective mode ){
		modes.put( mode.getIdentifier(), mode );
		mode.setPerspective( perspective );
	}
	
	/**
	 * Gets the location-mode with the identifier <code>mode</code>.
	 * @param mode the identifier
	 * @return the mode or <code>null</code> if not found
	 */
	public LocationModePerspective getMode( ExtendedMode mode ){
		return modes.get( mode );
	}
	
	/**
	 * Gets an array containing all the modes known to this manager.
	 * @return all the modes
	 */
	public ExtendedMode[] getModes(){
		return modes.keySet().toArray( new ExtendedMode[ modes.size() ] );
	}
	
	/**
	 * Gets the current mode of <code>dockable</code>.
	 * @param dockable the element whose location is searched
	 * @return the mode of <code>dockable</code> or <code>null</code> if not found
	 */
	public ExtendedMode getMode( PerspectiveDockable dockable ){
		while( dockable != null ){
			for( LocationModePerspective mode : modes.values() ){
				if( mode.isCurrentMode( dockable )){
					return mode.getIdentifier();
				}
			}
			PerspectiveStation parent = dockable.getParent();
			dockable = parent == null ? null : parent.asDockable();
		}
		return null;
	}
	
	/**
	 * Gets the mode which could have a child in the given location.
	 * @param root the identifier of the root {@link DockStation}
	 * @param location the location of some dockable
	 * @return the matching mode or <code>null</code> if not found
	 */
	public ExtendedMode getMode( String root, DockableProperty location ){
		for( LocationModePerspective mode : modes.values() ){
			if( mode.isCurrentMode( root, location )){
				return mode.getIdentifier();
			}
		}
		return null;
	}
	
	/**
	 * Gets the first {@link CStationPerspective} that is a root station and
	 * that is a parent of <code>dockable</code>.
	 * @param dockable some element whose root-station is searched
	 * @return the root-station or <code>null</code> if not found
	 */
	public CStationPerspective getRoot( PerspectiveDockable dockable ){
		while( dockable != null ){
			PerspectiveStation parent = dockable.getParent();
			if( parent == null ){
				return null;
			}
			if( parent instanceof CommonElementPerspective ){
				CStationPerspective station = ((CommonElementPerspective)parent).getElement().asStation();
				if( perspective.getStation( station.getUniqueId() ) != null ){
					return station;
				}
			}
			dockable = parent.asDockable();
		}
		
		return null;
	}
	
	/**
	 * Gets the current location of <code>dockable</code>.
	 * @param dockable the element whose location is searched
	 * @return the location or <code>null</code> if not found
	 */
	public Location getLocation( CDockablePerspective dockable ){
		return getLocation( dockable.intern().asDockable() );
	}

	/**
	 * Gets the current location of <code>dockable</code>.
	 * @param dockable the element whose location is searched
	 * @return the location or <code>null</code> if not found
	 */
	public Location getLocation( PerspectiveDockable dockable ){
		CStationPerspective root = getRoot( dockable );
		if( root == null ){
			return null;
		}
		ExtendedMode mode = getMode( dockable );
		if( mode == null ){
			return null;
		}
		DockableProperty location = DockUtilities.getPropertyChain( root.intern().asStation(), dockable );
		if( location == null ){
			return null;
		}
		return new Location( mode.getModeIdentifier(), root.getUniqueId(), location, false );
	}
	
	/**
	 * Writes the contents of this {@link LocationModeManagerPerspective} into a new {@link ModeSettings}.
	 * @param control access to factories that may be used for writing the contents
	 * @return the contents of this object
	 */
	public ModeSettings<Location, Location> writeModes( CControlAccess control ){
		ModeSettings<Location, Location> modes = new CLocationModeSettings<Location>( new LocationSettingConverter( control.getOwner().getController() ) );
    	
    	LocationModeManagerPerspective manager = perspective.getLocationManager();
    	
    	Iterator<PerspectiveElement> elements = perspective.elements();
    	while( elements.hasNext() ){
    		PerspectiveElement next = elements.next();
    		if( next instanceof CommonElementPerspective ){
    			CDockablePerspective dockable = ((CommonElementPerspective)next).getElement().asDockable();
    			if( dockable != null ){
	    			String id = null;
	    			
	    			if( dockable instanceof SingleCDockablePerspective ){
	    				id = control.getRegister().toSingleId( ((SingleCDockablePerspective)dockable).getUniqueId() );
	    			}
	    			else if( dockable instanceof MultipleCDockablePerspective){
	    				id = ((MultipleCDockablePerspective)dockable).getUniqueId();
	    				if( id != null ){
	    					// id == null should never happen
	    					id = control.getRegister().toMultiId( id );
	    				}
	    				else{
	    					throw new IllegalStateException( "detected dockable with no identifier" );
	    				}
	    			}
	    			
	    			if( id != null ){
	    				 ExtendedMode current = manager.getMode( dockable.intern().asDockable() );
	    				 LocationHistory history = dockable.getLocationHistory();
	    				 List<Path> order = history.getOrder();
	    				 Map<Path, Location> locations = history.getLocations();
	    				 modes.add( id, current == null ? null : current.getModeIdentifier(), locations, order );
	    			}
    			}
    		}
    	}
    	
    	// mode settings
    	for( ExtendedMode mode : manager.getModes() ){
    		ModeSettingFactory<Location> factory = settingFactories.get( mode.getModeIdentifier() );
    		if( factory != null ){
    			ModeSetting<Location> setting = factory.create();
    			getMode( mode ).writeSetting( setting );
    			modes.add( setting );
    		}
    	}
    	
    	return modes;
	}
	
	/**
	 * Reads the contents of <code>modes</code> and applies it to the dockables of <code>cperspective</code>.
	 * @param modes the settings to read
	 * @param cperspective the perspective for which to read the setting
	 * @param control the control associated with <code>cperspective</code>
	 */
	public void readModes( ModeSettings<Location, ?> modes, CPerspective cperspective, CControlAccess control ){
		Iterator<PerspectiveElement> elements = cperspective.elements();
    	while( elements.hasNext() ){
    		PerspectiveElement next = elements.next();
    		if( next instanceof CommonElementPerspective ){
    			CDockablePerspective dockable = ((CommonElementPerspective)next).getElement().asDockable();
    			if( dockable != null ){
    				String id = null;
    				if( dockable instanceof SingleCDockablePerspective ){
    					id = ((SingleCDockablePerspective)dockable).getUniqueId();
    					id = control.getRegister().toSingleId( id );
    				}
    				else if( dockable instanceof MultipleCDockablePerspective ){
    					id = ((MultipleCDockablePerspective)dockable).getUniqueId();
    					id = control.getRegister().toMultiId( id );
    				}
    				if( id != null ){
    					int index = modes.indexOf( id );
    					if( index != -1 ){
    						
    						Path[] order = modes.getHistory( index );
    						Map<Path, Location> locations = modes.getProperties( index );
    						LocationHistory history = dockable.getLocationHistory();
    						for( Path item : order ){
    							ExtendedMode mode = getMode( item );
    							if( mode == null ){
    								throw new IllegalStateException( "unknown ExtendedMode with id='" + item + "'" );
    							}
    							Location location = locations.get( item );
    							if( location != null ){
    								history.add( mode, location );
    							}
    						}
    					}
    				}
    			}
    		}
    	}
    	
    	// mode settings
    	for( ExtendedMode mode : getModes() ){
    		ModeSetting<Location> modeSetting = modes.getSettings( mode.getModeIdentifier() );
    		if( modeSetting != null ){
    			getMode( mode ).readSetting( modeSetting );
    		}
    	}
	}
}
