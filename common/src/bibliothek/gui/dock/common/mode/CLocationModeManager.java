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
package bibliothek.gui.dock.common.mode;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.facile.state.MaximizeArea;
import bibliothek.gui.dock.facile.state.StateManager.AffectedSet;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * {@link LocationModeManager} providing additional methods specific for
 * the Common project.
 * @author Benjamin Sigg
 *
 */
public class CLocationModeManager extends LocationModeManager<CLocationMode>{
	private CControlAccess control;
	
	private CNormalMode normalMode;
	private CMaximizedMode maximizedMode;
	private CMinimizedMode minimizedMode;
	private CExternalizedMode externalizedMode;

	/**
	 * Creates a new manager.
	 * @param control the control in whose realm this manager works
	 */
	public CLocationModeManager( CControlAccess control ){
		super( control.getOwner().intern().getController() );
		this.control = control;		

		minimizedMode = new CMinimizedMode( control.getOwner() );
		maximizedMode = new CMaximizedMode( control.getOwner() );
		normalMode = new CNormalMode( control.getOwner() );
		externalizedMode = new CExternalizedMode( control.getOwner() );
		
		putMode( minimizedMode );
		putMode( normalMode );
		putMode( maximizedMode );
		putMode( externalizedMode );
	}

	
	/**
	 * Direct access to the mode handling "normal" {@link Dockable}s.
	 * @return the mode
	 */
	public CNormalMode getNormalMode(){
		return normalMode;
	}

	/**
	 * Direct access to the mode handling "maximized" {@link Dockable}s.
	 * @return the mode
	 */
	public CMaximizedMode getMaximizedMode(){
		return maximizedMode;
	}
	
	/**
	 * Direct access to the mode handling "minimized" {@link Dockable}s.
	 * @return the mode
	 */
	public CMinimizedMode getMinimizedMode(){
		return minimizedMode;
	}
	
	/**
	 * Direct access to the mode handling "externalized" {@link Dockable}s.
	 * @return the mode
	 */
	public CExternalizedMode getExternalizedMode(){
		return externalizedMode;
	}
	
	
	@Override
	protected boolean createEntryDuringRead( String key ){
		return control.shouldStore( key );
	}
	
    
    /**
     * Tries to set the location of <code>dockable</code>. Does nothing if
     * <code>location</code> is invalid or requires information that is
     * not available. If <code>dockable</code> is a {@link CommonDockable} and
     * the {@link CLocationMode mode} respects {@link LocationMode#respectWorkingAreas(DockStation) working-areas},
     * then the working-area is set or removed depending on the value of {@link CStation#isWorkingArea()}.
     * @param dockable the element to move
     * @param location the new location of <code>dockable</code>
     */
    public void setLocation( Dockable dockable, CLocation location ){
    	ExtendedMode mode = location.findMode();
    	if( mode == null )
    		return;
    	
    	CLocationMode newMode = getMode( mode.getModeIdentifier() );
    	if( newMode == null )
    		return;
    	
    	String root = location.findRoot();
    	
    	if( root != null ){
    		if( dockable instanceof CommonDockable ){
	    		CStation<?> station = control.getOwner().getStation( root );
	    		if( newMode.respectWorkingAreas( station.getStation() )){
	    			if( station.isWorkingArea() ){
	    				((CommonDockable)dockable).getDockable().setWorkingArea( station );
	    			}
	    			else{
	    				((CommonDockable)dockable).getDockable().setWorkingArea( null );
	    			}
	    		}
	    	}
    	}	    	
    	
    	// easy solution: set the location, then change the mode
	    setProperties( newMode, dockable, new Location( mode.getModeIdentifier(), root, location.findProperty() ) );
	    apply( dockable, newMode );
    	
//    	if( root != null && mode != null ){
//    		String root = location.findRoot();
//        	DockableProperty property = location.findProperty();
//        	
//    		
//    	    newMode = getMode( mode.getModeIdentifier() );
//	        
//	        // ensure the correct working area is set.
//            boolean set = false;
//            if( root != null ){
//            	CStation station = control.getOwner().getStation( root );
//            	if( station != null && station.isWorkingArea() ){
//            		dockable.getDockable().setWorkingArea( station );
//            		set = true;
//            		ExtendedMode stationMode = station.getStationLocation().findMode();
//            		if( stationMode != null ){
//            			newMode = getMode( stationMode.getModeIdentifier() );
//            		}
//            	}
//            }
//            if( !set && NORMALIZED.equals( newMode ) ){
//                dockable.getDockable().setWorkingArea( null );
//            }
//	    	
//	        if( mode == ExtendedMode.MAXIMIZED || property != null ){
//	            String current = currentMode( dockable );
//    		    store( current, dockable );
//    		    setProperties( newMode, dockable, new Location( root, property ) );
//    		    transition( null, newMode, dockable );
//	        }
//    	}
    }
    
    /**
     * Sets the default location of <code>dockable</code> when going into
     * <code>mode</code>. The properties set here will be overridden
     * as soon as the user drags <code>dockable</code> to another location (within 
     * the same mode) or <code>dockable</code> is removed permanently.<br> 
     * This method has no effect if <code>dockable</code> is already in
     * <code>mode</code>. There is also no effect if <code>dockable</code>
     * has not been registered at the {@link CStateManager}.<br>
     * Note: it is the clients responsibility to ensure that <code>location</code>
     * and <code>mode</code> belong to each other.
     * @param dockable the element whose location will be set
     * @param mode the mode for which the location is to be set or <code>null</code>
     * @param location the new location
     * @throws IllegalArgumentException if either argument is <code>null</code> or
     * if {@link CLocation#findRoot()} or {@link CLocation#findProperty()} returns
     * <code>null</code>
     */
    public void setLocation( Dockable dockable, ExtendedMode mode, CLocation location ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        
        if( mode == null )
            throw new IllegalArgumentException( "mode must not be null" );
        
        if( location != null ){
	        String root = location.findRoot();
	        if( root == null )
	            throw new IllegalArgumentException( "the location is not sufficient to find the root station" );
	        
	        DockableProperty property = location.findProperty();
	        if( property == null )
	            throw new IllegalArgumentException( "the location does not carry enough information to find the location of dockable" );
	        
	        if( !mode.getModeIdentifier().equals( location.findMode() ))
	        	throw new IllegalArgumentException( "location and mode do not belong together, they do not have the same identifier" );
	        
	        setProperties( getMode( mode.getModeIdentifier() ), dockable, new Location( mode.getModeIdentifier(), root, property ) );
        }
        else{
        	setProperties( getMode( mode.getModeIdentifier() ), dockable, null );
        }
    }
	
    /**
     * Gets an element describing the location of <code>dockable</code> as
     * good as possible.
     * @param dockable the element whose location should be searched
     * @return the location or <code>null</code> if no location was found
     */
    public CLocation getLocation( Dockable dockable ){
    	CLocationMode mode = getCurrentMode( dockable );
    	if( mode == null )
    		return null;
    	
    	return mode.getCLocation( dockable );
    }

    /**
     * Assuming that <code>dockable</code> is currently not in mode <code>mode</code>,
     * then this method searches for the previously stored location of <code>dockable</code>.
     * Note that this method can't tell where <code>dockable</code> would be
     * shown if it never was in that mode and the client never specified the 
     * location.
     * @param dockable the dockable whose location is searched
     * @param mode the mode which might be taken by <code>dockable</code>
     * @return the location or <code>null</code>
     * @throws IllegalArgumentException if any argument is <code>null</code>
     */
    public CLocation getLocation( Dockable dockable, ExtendedMode mode ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        
        if( mode == null )
            throw new IllegalArgumentException( "mode must not be null" );
        
        CLocationMode cmode = getMode( mode.getModeIdentifier() );
        
        Location location = getProperties( cmode, dockable );
        if( location == null )
            return null;
        
        return cmode.getCLocation( dockable, location );
    }
    
    /**
     * This method compares the current mode of <code>dockable</code> with its
     * availability set. If the current mode is not available, then <code>dockable</code>
     * is put into another mode (usually the {@link #getNormalMode() normal mode}).<br>
     * This method also checks the working area, provided that the current mode respects
     * the working-area settings.
     * @param dockable the element whose mode is to be checked
     */
	public void ensureValidLocation( CDockable dockable ){
        ExtendedMode mode = getMode( dockable.intern() );
        if( mode == ExtendedMode.NORMALIZED ){
            CStation<?> preferredArea = dockable.getWorkingArea();
            CStation<?> currentArea = findFirstParentWorkingArea( dockable.intern() );
            
            if( preferredArea != currentArea ){
                if( preferredArea == null ){
                	// the dockable is on a working-area, but should not be there
                	CLocation defaultLocation = getNormalMode().getDefaultLocation();
                    dockable.setLocation( defaultLocation );
                }
                else{
                	// reset the location
                    dockable.setLocation( preferredArea.getStationLocation() );
                }
            }
            
            mode = getMode( dockable.intern() );
        }
        
        // normalize the element if its current mode is not valid
        if( !isModeAvailable( dockable.intern(), mode )){
        	dockable.setExtendedMode( ExtendedMode.NORMALIZED );
        }
	}

    /**
     * Ensures that <code>dockable</code> is not hidden behind another 
     * {@link Dockable}. That does not mean that <code>dockable</code> becomes
     * visible, just that it is easier reachable without the need to change
     * modes of any <code>Dockable</code>s.  
     * @param dockable the element which should not be hidden
     */
    public void ensureNotHidden( Dockable dockable ){
        AffectedSet set = new AffectedSet();

        DockStation parent = dockable.getDockParent();
        while( parent != null ){
        	
        	
        	
            MaximizeArea area = getMaximizeArea( parent );
            if( area != null ){
                if( area.getMaximizedDockable() != null && area.getMaximizedDockable() != dockable ){
                    unmaximize( area.getMaximizedDockable(), set );
                }
            }

            dockable = parent.asDockable();
            parent = dockable == null ? null : dockable.getDockParent();
        }

        set.finish();
    }
    
	public boolean ensureNothingMaximized(){
		// TODO Auto-generated method stub
		8
		return false;
	}

	public void normalizeAllWorkingAreaChildren(){
		// TODO Auto-generated method stub
		5
	}
	
	/**
	 * Guesses the result of {@link #getCurrentMode(Dockable)} once a {@link Dockable} is
	 * dropped onto {@link DockStation}. If more than one {@link LocationMode mode} is using
	 * <code>parent</code>, then the guess might not always be correct.
	 * @param parent some station
	 * @return the mode its children are in, or <code>null</code> if no guess can be made
	 */
	public ExtendedMode childsExtendedMode( DockStation parent ){
		while( parent != null ){
			CLocationMode mode = getRepresentingMode( parent );
			if( mode != null ){
				return mode.getExtendedMode();
			}
			Dockable dockable = parent.asDockable();
			if( dockable == null )
				return null;
			parent = dockable.getDockParent();
		}
		
		return null;
	}

	/**
     * Finds the first {@link CStation} in the path up to the root from
     * <code>dockable</code> wich is a working area.
     * @param dockable the element which might have a {@link CStation}
     * as parent.
     * @return the first found {@link CStation}.
     */
    private CStation<?> findFirstParentWorkingArea( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        dockable = station == null ? null : station.asDockable();
        
        if( dockable != null )
            return getAreaOf( dockable );
        else
            return null;
    }
    
    /**
     * Searches <code>dockable</code> and its parent for the first {@link CStation} 
     * that is a working area.
     * @param dockable the element whose working area is searched
     * @return the first working area or <code>null</code>
     */
    protected CStation<?> getAreaOf( Dockable dockable ){
        Map<DockStation, CStation<?>> stations = new HashMap<DockStation, CStation<?>>();
        for( CStation<?> station : control.getOwner().getStations() ){
            if( station.isWorkingArea() ){
                stations.put( station.getStation(), station );
            }
        }
        
        if( dockable.asDockStation() != null ){
            CStation<?> station = stations.get( dockable.asDockStation() );
            if( station != null )
                return station;
        }
        
        Dockable check = dockable;
        while( check != null ){
            DockStation parent = check.getDockParent();
            if( parent == null )
                check = null;
            else
                check = parent.asDockable();
            
            CStation<?> station = stations.get( parent );
            if( station != null )
                return station;
        }
        
        return null;
    }
    
    /**
     * Searches the {@link CLocationMode mode} which represents the mode of
     * the children of <code>station</code>. This method calls 
     * {@link LocationMode#isRepresenting(DockStation, boolean)} with <code>station</code>,
     * but does not check the parents of <code>station</code>.
     * @param station some station
     * @return the mode or <code>null</code> if nothing found
     */
    private CLocationMode getRepresentingMode( DockStation station ){
    	Iterable<CLocationMode> modes = modes();
    	
    	for( int i = 0; i < 2; i++ ){
    		boolean exceptions = i == 0;
        	
	    	for( CLocationMode mode : modes ){
	    		if( mode.isRepresenting( station, exceptions )){
	    			return mode;
	    		}
	    	}
    	}
    	
    	return null;
    }
}
