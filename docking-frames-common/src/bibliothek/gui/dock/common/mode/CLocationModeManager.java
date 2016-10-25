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
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockFrontend;
import bibliothek.gui.dock.common.intern.CDockFrontendListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CDockableAccess;
import bibliothek.gui.dock.common.intern.CSetting;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.facile.mode.CLocationModeSettings;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.LocationModeManager;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.AffectingRunnable;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;
import bibliothek.gui.dock.support.mode.ModeSettings;
import bibliothek.gui.dock.support.mode.ModeSettingsConverter;
import bibliothek.gui.dock.support.mode.UndoableModeSettings;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.util.Path;
import bibliothek.util.container.Single;

/**
 * {@link LocationModeManager} providing additional methods for working with
 * {@link CLocation}s, {@link CommonDockable}s and other items specific to the
 * <code>common</code> project. 
 * @author Benjamin Sigg
 *
 */
public class CLocationModeManager extends LocationModeManager<CLocationMode>{
	/** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "minimize"-action */
    public static final String ICON_MANAGER_KEY_MINIMIZE = "locationmanager.minimize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "maximize"-action */
    public static final String ICON_MANAGER_KEY_MAXIMIZE = "locationmanager.maximize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "normalize"-action */
    public static final String ICON_MANAGER_KEY_NORMALIZE = "locationmanager.normalize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "externalize"-action */
    public static final String ICON_MANAGER_KEY_EXTERNALIZE = "locationmanager.externalize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "unexternalize"-action */
    public static final String ICON_MANAGER_KEY_UNEXTERNALIZE = "locationmanager.unexternalize";
    /** the key used for the {@link IconManager} to read the {@link javax.swing.Icon} for the "unmaximize externalized"-action */
    public static final String ICON_MANAGER_KEY_UNMAXIMIZE_EXTERNALIZED = "locationmanager.unmaximize_externalized";
    
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

		setDoubleClickStrategy( new PreviousModeDoubleClickStrategy( this ) );
		
		minimizedMode = new CMinimizedMode( control.getOwner() );
		maximizedMode = new CMaximizedMode( control.getOwner() );
		normalMode = new CNormalMode( control.getOwner() );
		externalizedMode = new CExternalizedMode( control.getOwner() );
		
		putMode( minimizedMode );
		putMode( externalizedMode );
		putMode( normalMode );
		putMode( maximizedMode );
        
        addModeManagerListener(new ModeManagerListener<Location, LocationMode>(){
			public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
				// ignore
			}

			public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
				// ignore
			}

			public void modeAdded( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
				// ignore
			}

			public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
				CDockableAccess access = CLocationModeManager.this.control.access( ((CommonDockable)dockable).getDockable() );
				if( access != null ){
					ExtendedMode mode = getMode(dockable);
					access.informMode( mode );
				}
			}

			public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
				// ignore
			}
		});
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
	
	@Override
	public void remove( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			CDockable cdockable = ((CommonDockable)dockable).getDockable();
			String key = control.shouldStore( cdockable );
			if( key != null ){
				addEmpty( key );
			}
		}
		super.remove( dockable );
	}

    @Override
    public <B> ModeSettings<Location, B> createModeSettings( ModeSettingsConverter<Location, B> converter ){
    	return new CLocationModeSettings<B>( converter );
    }
    
    @Override
    public void readSettings( ModeSettings<Location, ?> settings ){
        UndoableModeSettings undoable = new UndoableModeSettings(){
        	public boolean createTemporaryDuringRead( String key ){
        		return control.getRegister().isMultiId( key );
        	}
        };
    	
    	final Runnable temporary = readSettings( settings, undoable );
    	control.getOwner().intern().addListener( new CDockFrontendListener(){
			public void loading( CDockFrontend frontend, CSetting setting ){
				// ignore
			}
			
			public void loaded( CDockFrontend frontend, CSetting setting ){
				temporary.run();
				frontend.removeListener( this );
			}
		});
    }
    
    @Override
    public Runnable readSettings( ModeSettings<Location, ?> settings, UndoableModeSettings pending ){
    	Runnable result = super.readSettings( settings, pending );
    	if( settings instanceof CLocationModeSettings<?> ){
    		CLocationModeSettings<?> locationSettings = (CLocationModeSettings<?>)settings;
    		locationSettings.rescue( getMaximizedMode() );
    	}
    	return result;
    }
    
    /**
     * Tries to set the location of <code>dockable</code>. Does nothing if
     * <code>location</code> is invalid or requires information that is
     * not available. If <code>dockable</code> is a {@link CommonDockable} and
     * the {@link CLocationMode mode} respects {@link CLocationMode#respectWorkingAreas(DockStation) working-areas},
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
	    		if( station != null ){
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
    	    setProperties( newMode, dockable, new Location( mode.getModeIdentifier(), root, location.findProperty(), true ) );
    	    apply( dockable, newMode, true );
    	}
    	else{
    		apply( dockable, newMode, false );
    	}
    }
    
    /**
     * Sets the default location of <code>dockable</code> when going into
     * <code>mode</code>. The properties set here will be overridden
     * as soon as the user drags <code>dockable</code> to another location (within 
     * the same mode) or <code>dockable</code> is removed permanently.<br> 
     * This method has no effect if <code>dockable</code> is already in
     * <code>mode</code>. There is also no effect if <code>dockable</code>
     * has not been registered at the {@link CLocationModeManager}.<br>
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
	        
	        ExtendedMode locationMode = location.findMode();
	        if( locationMode == null ){
	        	throw new IllegalArgumentException( "the location does not carry enough information to find the mode of dockable" );
	        }
	        
	        if( !mode.getModeIdentifier().equals( locationMode.getModeIdentifier() ))
	        	throw new IllegalArgumentException( "location and mode do not belong together, they do not have the same identifier" );
	        
	        setProperties( getMode( mode.getModeIdentifier() ), dockable, new Location( mode.getModeIdentifier(), root, property, true ) );
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
     * Tries to find the "optimal spot" where to put a new child onto <code>station</code>. In this
     * case the optimal spot is {@link CLocation#aside()} the latest focused child of <code>station</code>.
     * @param station the station where a {@link CDockable} is about to be dropped onto
     * @return the preferred location of the new child
     */
    public CLocation getDropLocation( CStation<?> station ){
    	Dockable[] history = control.getOwner().getController().getFocusHistory().getHistory();
    	for( int i = history.length-1; i >= 0; i-- ){
    		Dockable next = history[i];
    		if( next instanceof CommonDockable && next.asDockStation() != station.getStation() ){
    			CDockable cnext = ((CommonDockable)next).getDockable();
    			
    			if( DockUtilities.isAncestor( station.getStation(), next )){
	    			boolean valid;
	    			if( station.isWorkingArea() ){
	    				valid = cnext.getWorkingArea() == station;
	    			}
	    			else{
	    				valid = cnext.getWorkingArea() == null;
	    			}
	    			if( valid ){
	    				CLocation location = cnext.getBaseLocation();
	    				if( location != null ){
	    					return location.aside();
	    				}
	        		}
    			}
    			if( cnext.getWorkingArea() == station ){
    				CLocation location = cnext.getBaseLocation();
    				if( location != null ){
    					return location.aside();
    				}
    			}
    		}
    	}
    	return station.getStationLocation();
    }
    
    @Override
    public void ensureValidLocation( Dockable dockable ){
	    if( dockable instanceof CommonDockable ){
	    	ensureValidLocation( ((CommonDockable)dockable).getDockable() );
	    }
    }
    
    /**
     * This method compares the current mode of <code>dockable</code> with its
     * availability set. If the current mode is not available, then <code>dockable</code>
     * is put into another mode (usually the {@link #getNormalMode() normal mode}).<br>
     * This method also checks the working area, provided that the current mode respects
     * the working-area settings.<br>
     * This method returns immediately if in {@link #isLayouting() layouting mode}
     * @param dockable the element whose mode is to be checked
     */
	public void ensureValidLocation( CDockable dockable ){
		if( isLayouting() )
			return;
		
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
     * Ensures that all dockables are in a basic mode.<br>
     * This method returns immediately if in {@link #isLayouting() layouting mode}
     * @return <code>true</code> if at least one element was affected by changes,
     * <code>false</code> if nothing happened.
     */
	public boolean ensureBasicModes(){
		if( isLayouting() )
			return false;
		
		final Single<Boolean> result = new Single<Boolean>( false );
		
		runTransaction( new AffectingRunnable() {
			public void run( AffectedSet set ){
				for( Dockable dockable : listDockables() ){
					CLocationMode current = getCurrentMode( dockable );
					if( current != null && !current.isBasicMode() ){
						List<CLocationMode> modes = getModeHistory( dockable );
						CLocationMode next = null;
						for( int i = modes.size()-1; i >= 0 && next == null; i-- ){
							CLocationMode mode = modes.get( i );
							if( mode.isBasicMode() && isModeAvailable( dockable, mode.getExtendedMode() )){
								next = mode;
							}
						}
						if( next == null ){
							next = getNormalMode();
						}
						
						result.setA( true );
						setMode( dockable, next.getExtendedMode() );
					}
				}		
			}
		});
		return result.getA();
	}
	
	/**
	 * Updates the location of all dockables that should be on a working-area
	 * and that are currently in a mode that does not support working-areas. The history
	 * of the elements is searched for the first mode which supports working-areas. If no
	 * such mode is found, then the normal-mode is applied.
	 */
	public void resetWorkingAreaChildren(){
		runTransaction( new AffectingRunnable() {
			public void run( AffectedSet set ){
				for( Dockable dockable : listDockables() ){
					if( dockable instanceof CommonDockable ){
						CDockable cdockable = ((CommonDockable)dockable).getDockable();
						resetWorkingArea( cdockable, set );
					}
				}
			}
		});
	}
	
	private void resetWorkingArea( CDockable dockable, AffectedSet set ){
		if( dockable.getWorkingArea() == null )
			return;
		
		DockStation parent = dockable.intern().getDockParent();
		if( parent == null )
			return;
		
		CLocationMode current = getCurrentMode( dockable.intern() );
		if( current == null )
			return;
		
		if( current.respectWorkingAreas( parent ))
			return;
		
		// need to reset
		List<Location> history = getPropertyHistory( dockable.intern() );
		CLocationMode next = null;
		for( int i = history.size()-1; i >= 0 && next == null; i-- ){
			Location check = history.get( i );
			Path path = check.getMode();
			String root = check.getRoot();
			if( path != null && root != null ){
				CLocationMode mode = getMode( path );
				if( mode != null ){
					CStation<?> station = control.getOwner().getStation( root );
					if( station != null ){
						if( mode.respectWorkingAreas( station.getStation() ) && mode.isRepresenting( station.getStation() )){
							if( isModeAvailable( dockable.intern(), mode.getExtendedMode() )){
								next = mode;
							}
						}
					}
				}
			}
		}
		if( next == null ){
			next = getNormalMode();
		}

		apply( dockable.intern(), next, set, false );
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
     * {@link LocationMode#isRepresenting(DockStation)} with <code>station</code>,
     * but does not check the parents of <code>station</code>. Basic modes are preferred
     * over non-basic modes by this method.
     * @param station some station
     * @return the mode or <code>null</code> if nothing found
     */
    private CLocationMode getRepresentingMode( DockStation station ){
    	Iterable<CLocationMode> modes = modes();
    	CLocationMode first = null;
    	
	    for( CLocationMode mode : modes ){
	    	if( mode.isRepresenting( station )){
	    		if( mode.isBasicMode() )
	    			return mode;
	    		if( first == null )
	    			first = mode;
	    	}
	    }
	    
	    return first;
    }
}
