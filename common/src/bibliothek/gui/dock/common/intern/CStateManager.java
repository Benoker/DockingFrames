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
package bibliothek.gui.dock.common.intern;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.common.*;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.common.location.CMaximizedLocation;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DoubleClickListener;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.action.StateManager;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.util.container.Single;

/**
 * A manager that can change the extended-state of {@link CDockable}s
 * @author Benjamin Sigg
 *
 */
public class CStateManager extends StateManager {
    /** access to the {@link CControl} that uses this manager */
    private CControlAccess control;
    
    /** how to maximize the elements */
    private CMaximizeBehavior maximizeBehavior = CMaximizeBehavior.STACKED;
    
    /**
     * {@link KeyStroke} used to go into, or go out from the maximized state.
     */
    private PropertyValue<KeyStroke> keyStrokeMaximizeChange = new PropertyValue<KeyStroke>( CControl.KEY_MAXIMIZE_CHANGE ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            if( keyStrokeMaximized.getValue() == null )
                getIngoingAction( MAXIMIZED ).setAccelerator( newValue );
        }
    };
    
    /**
     * A listener to all {@link CDockable}s, calls {@link #rebuild(Dockable)}
     * when one of the actions of the dockables changes.
     */
    private CDockablePropertyListener actionChangeListener = new CDockableAdapter(){
        @Override
        public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ) {
            if( CDockable.ACTION_KEY_EXTERNALIZE.equals( key ) ||
                    CDockable.ACTION_KEY_MAXIMIZE.equals( key ) ||
                    CDockable.ACTION_KEY_MINIMIZE.equals( key ) ||
                    CDockable.ACTION_KEY_NORMALIZE.equals( key )){
                rebuild( dockable.intern() );
            }
        }
    };
    
    /**
     * {@link KeyStroke} used on the maximize-action.
     */
    private PropertyValue<KeyStroke> keyStrokeMaximized = new PropertyValue<KeyStroke>( CControl.KEY_GOTO_MAXIMIZED ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            if( newValue == null )
                getIngoingAction( MAXIMIZED ).setAccelerator( keyStrokeMaximizeChange.getValue() );
            else
                getIngoingAction( MAXIMIZED ).setAccelerator( newValue );
        }
    };
    
    /**
     * Creates a new manager
     * @param control internal access to the {@link CControl} that uses this manager
     */
    public CStateManager( CControlAccess control ){
        super( control.getOwner().intern().getController(), false );
        
        putIngoingAction( MAXIMIZED, new SimpleButtonAction(){
            @Override
            public void action( Dockable dockable ) {
                super.action( dockable );
                goIn( MAXIMIZED, dockable );
            }
            
            @Override
            protected boolean trigger( KeyEvent event, Dockable dockable ){
                if( !KeyStroke.getKeyStrokeForEvent( event ).equals( keyStrokeMaximizeChange.getValue())){
                    return super.trigger( event, dockable );
                }
                return false;
            }
        });
        
        // initializing method of parent class
        init();
        
        this.control = control;
        DockController controller = control.getOwner().intern().getController();
        
        // add hook to get key-events for all Dockables
        controller.getRegister().addDockRegisterListener( new DockRegisterAdapter(){
            @Override
            public void dockableRegistered( DockController controller, Dockable dockable ) {
                new KeyHook( dockable );
            }
        });
        
        // using keystrokes
        keyStrokeMaximizeChange.setProperties( controller );
        keyStrokeMaximized.setProperties( controller );
        
        new PropertyValue<KeyStroke>( CControl.KEY_GOTO_EXTERNALIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( EXTERNALIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( CControl.KEY_GOTO_MAXIMIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( MAXIMIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( CControl.KEY_GOTO_MINIMIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( MINIMIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( CControl.KEY_GOTO_NORMALIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( NORMALIZED ).setAccelerator( newValue );
            }
        };
        
        // ensure that non externalizable elements can't be dragged out
        controller.addAcceptance( new DockAcceptance(){
            public boolean accept( DockStation parent, Dockable child ) {
                if( parent instanceof ScreenDockStation )
                    return externalizable( child );
                return true;
            }
            public boolean accept( DockStation parent, Dockable child, Dockable next ) {
                if( parent instanceof ScreenDockStation )
                    return externalizable( next );
                return true;
            }
            
            /**
             * Tells whether all elements of <code>dockable</code> can be
             * externalized.
             * @param dockable the element to search for <code>CommonDockable</code>s
             * @return <code>true</code> if all elements are externalizable
             */
            private boolean externalizable( Dockable dockable ){
                final Single<Boolean> result = new Single<Boolean>( true );
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        if( dockable instanceof CommonDockable ){
                            if( !((CommonDockable)dockable).getDockable().isExternalizable() ){
                                result.setA( false );
                            }
                        }
                    }
                });
                return result.getA();
            }
        });
        
        // react on double click
        control.getOwner().intern().getController().getDoubleClickController().addListener( new DoubleClickListener(){
            public DockElement getTreeLocation() {
                return null;
            }
            public boolean process( Dockable dockable, MouseEvent event ) {
                if( event.isConsumed() )
                    return false;
                
                if( dockable instanceof CommonDockable ){
                    CDockable fdockable = ((CommonDockable)dockable).getDockable();
                    if( fdockable.getExtendedMode() != ExtendedMode.MAXIMIZED ){
                        if( fdockable.isMaximizable() ){
                            fdockable.setExtendedMode( CDockable.ExtendedMode.MAXIMIZED );
                            event.consume();
                            return true;
                        }
                    }
                    else {
                        goOut( MAXIMIZED, dockable );
                        ensureValidLocation( fdockable );
                        event.consume();
                        return true;
                    }
                }

                return false;
            }
        });
        
    }
    
    @Override
    protected Dockable getMaximizingElement( Dockable dockable ){
    	return maximizeBehavior.getMaximizingElement( dockable );
    }
    
    @Override
    protected Dockable getMaximizingElement( Dockable old, Dockable dockable ){
    	return maximizeBehavior.getMaximizingElement( old, dockable );
    }
    
    @Override
    protected void added( Dockable dockable ) {
        super.added( dockable );
        if( dockable instanceof CommonDockable ){
            ((CommonDockable)dockable).getDockable().addCDockablePropertyListener( actionChangeListener );
        }
    }
    
    @Override
    protected void removed( Dockable dockable ) {
        super.removed( dockable );
        if( dockable instanceof CommonDockable ){
            ((CommonDockable)dockable).getDockable().removeCDockablePropertyListener( actionChangeListener );
        }
    }
    
    
    /**
     * Sets a new {@link CMaximizeBehavior}. The behavior decides what happens
     * when the user maximizes or un-maximizes a {@link CDockable}.
     * @param maximizeBehavior the new behavior
     * @throws NullPointerException if <code>maximizeBehavior</code> is <code>null</code>
     */
    public void setMaximizeBehavior( CMaximizeBehavior maximizeBehavior ){
    	if( maximizeBehavior == null )
    		throw new NullPointerException( "maximizeBehavior must not be null" );
		this.maximizeBehavior = maximizeBehavior;
	}
    
    /**
     * Gets the currently used maximize-behavior.
     * @return the behavior
     * @see #setMaximizeBehavior(CMaximizeBehavior)
     */
    public CMaximizeBehavior getMaximizeBehavior(){
		return maximizeBehavior;
	}
    
    /**
     * Changes the mode of <code>dockable</code>.
     * @param dockable an element whose mode will be changed
     * @param mode the new mode
     */
    public void setMode( Dockable dockable, CDockable.ExtendedMode mode ) {
        switch( mode ){
            case EXTERNALIZED:
                setMode( dockable, EXTERNALIZED );
                break;
            case MAXIMIZED:
                setMode( dockable, MAXIMIZED );
                break;
            case MINIMIZED:
                setMode( dockable, MINIMIZED );
                break;
            case NORMALIZED:
                setMode( dockable, NORMALIZED );
                break;
        }
    }
    
    /**
     * Gets the mode <code>dockable</code> is currently into.
     * @param dockable the questioned element
     * @return the mode of <code>dockable</code>
     */
    public CDockable.ExtendedMode getMode( Dockable dockable ){
        String mode = currentMode( dockable );
        
        if( EXTERNALIZED.equals( mode ))
            return CDockable.ExtendedMode.EXTERNALIZED;
        else if( MINIMIZED.equals( mode ))
            return CDockable.ExtendedMode.MINIMIZED;
        else if( MAXIMIZED.equals( mode ))
            return CDockable.ExtendedMode.MAXIMIZED;
        else if( NORMALIZED.equals( mode ))
            return CDockable.ExtendedMode.NORMALIZED;
        
        return null;
    }
    
    /**
     * Finds out which mode a child of <code>station</code> would have.
     * @param station the station
     * @return the mode or <code>null</code> if the station is unknown
     */
    public CDockable.ExtendedMode childsExtendedMode( DockStation station){
        String mode = childsMode( station );

        if( EXTERNALIZED.equals( mode ))
            return CDockable.ExtendedMode.EXTERNALIZED;
        else if( MINIMIZED.equals( mode ))
            return CDockable.ExtendedMode.MINIMIZED;
        else if( NORMALIZED.equals( mode ))
            return CDockable.ExtendedMode.NORMALIZED;
        
        return null;
    }
    
    /**
     * Tries to set the location of <code>dockable</code>.
     * @param dockable the element to move
     * @param location the new location of <code>dockable</code>
     */
    public void setLocation( CommonDockable dockable, CLocation location ){
    	String root = location.findRoot();
    	DockableProperty property = location.findProperty();
    	ExtendedMode mode = location.findMode();
    	String newMode = null;
    	
    	if( root != null && mode != null ){
    	    newMode = convertMode( mode );
	        
	        // ensure the correct working area is set.
            boolean set = false;
            if( root != null ){
                DockStation station = getStation( root );
                if( station != null ){
                    CStation cstation = control.getOwner().getStation( station );
                    if( cstation != null && cstation.isWorkingArea() ){
                        dockable.getDockable().setWorkingArea( cstation );
                        set = true;
                        ExtendedMode stationMode = cstation.getStationLocation().findMode();
                        if( stationMode != null ){
                            newMode = convertMode( stationMode );
                        }
                    }
                }
            }
            if( !set && NORMALIZED.equals( newMode ) ){
                dockable.getDockable().setWorkingArea( null );
            }
	    	
	        if( mode == ExtendedMode.MAXIMIZED || property != null ){
	            String current = currentMode( dockable );
    		    store( current, dockable );
    		    setProperties( newMode, dockable, new Location( root, property ) );
    		    transition( null, newMode, dockable );
	        }
    	}
    }
    
    /**
     * Converts <code>mode</code> into one of the strings
     * {@link StateManager#EXTERNALIZED}, {@link StateManager#MAXIMIZED},
     * {@link StateManager#MINIMIZED} or {@link StateManager#NORMALIZED}.
     * @param mode the mode, not <code>null</code>
     * @return the mode represented as string
     */
    protected String convertMode( ExtendedMode mode ){
        switch( mode ){
            case EXTERNALIZED:
                return EXTERNALIZED;
            case MAXIMIZED:
                return MAXIMIZED;
            case MINIMIZED:
                return MINIMIZED;
            case NORMALIZED:
                return NORMALIZED;
        }
        
        return null;
    }

    /**
     * Converts <code>mode</code> into one of the {@link ExtendedMode}s.
     * 
     * @param mode {@link StateManager#EXTERNALIZED}, {@link StateManager#MAXIMIZED},
     * {@link StateManager#MINIMIZED} or {@link StateManager#NORMALIZED}.
     * @return the mode represented as {@link ExtendedMode}
     */
    protected ExtendedMode convertMode( String mode ){
        if( EXTERNALIZED.equals( mode ))
            return ExtendedMode.EXTERNALIZED;
        
        if( MAXIMIZED.equals( mode ))
            return ExtendedMode.MAXIMIZED;
        
        if( NORMALIZED.equals( mode ))
            return ExtendedMode.NORMALIZED;
        
        if( MINIMIZED.equals( mode ))
            return ExtendedMode.MINIMIZED;
        
        throw new IllegalArgumentException( "Not the name of a mode: " + mode );
    }
    
    /**
     * Gets an element describing the location of <code>dockable</code> as
     * good as possible.
     * @param dockable the element whose location should be searched
     * @return the location or <code>null</code> if no location was found
     */
    public CLocation getLocation( Dockable dockable ){
    	if( getMode( dockable ) == ExtendedMode.MAXIMIZED )
    		return new CMaximizedLocation();
    	
    	List<CStation> stations = control.getOwner().getStations();
    	CStation root = null;
    	
    	Dockable child = dockable;
    	DockStation search = dockable.asDockStation();
    	
    	loop:while( search != null ){
    	    for( CStation station : stations ){
    	        if( station.getStation() == search ){
    	            root = station;
    	            break loop;
    	        }
    	    }
    	    
    	    child = search.asDockable();
    	    if( child == null )
    	        search = null;
    	    else
    	        search = child.getDockParent();
    	}
    	
    	if( root == null )
    	    return null;
    	
    	DockableProperty property = DockUtilities.getPropertyChain( root.getStation(), dockable );
    	CLocation location = root.getStationLocation();
    	return location.expandProperty( property );
    }
    
    /**
     * Searches <code>dockable</code> and its parent for the first {@link CStation} 
     * that is a working area.
     * @param dockable the element whose working area is searched
     * @return the first working area or <code>null</code>
     */
    protected CStation getAreaOf( Dockable dockable ){
        Map<DockStation, CStation> stations = new HashMap<DockStation, CStation>();
        for( CStation station : control.getOwner().getStations() ){
            if( station.isWorkingArea() ){
                stations.put( station.getStation(), station );
            }
        }
        
        if( dockable.asDockStation() != null ){
            CStation station = stations.get( dockable.asDockStation() );
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
            
            CStation station = stations.get( parent );
            if( station != null )
                return station;
        }
        
        return null;
    }
    
    @Override
    protected boolean isValidNormalized( Dockable dockable ) {
        if( !super.isValidNormalized( dockable ) )
            return false;
        
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            
            CStation current = getAreaOf( dockable );
            CStation preferred = cdock.getWorkingArea();
            
            return current == preferred;
        }
        
        return true; 
    }
    
    /**
     * Searches the {@link CContentArea} on which <code>dockable</code> is shown.
     * @param dockable the element whose center is searched
     * @return the center or <code>null</code>
     */
    protected CContentArea getCenterOf( Dockable dockable ){
    	DockStation station = DockUtilities.getRoot( dockable );
    	if( station == null )
    		return null;
    	
    	for( CContentArea center : control.getOwner().getContentAreas() ){
    		if( center.getCenter() == station )
    			return center;
    		
    		if( center.getEast() == station )
    			return center;
    		if( center.getWest() == station )
    			return center;
    		if( center.getNorth() == station )
    			return center;
    		if( center.getSouth() == station )
    			return center;
    	}
    	
    	return null;
    }
    
    @Override
    protected String[] availableModes( String current, Dockable dockable ){
    	if( !(dockable instanceof CommonDockable )){
    		return new String[0];
    	}
    	
    	CDockable facile = ((CommonDockable)dockable).getDockable();
    	
    	List<String> modes = new ArrayList<String>( 4 );
    	
    	if( !MINIMIZED.equals( current ) && facile.isMinimizable() ){
    	    modes.add( MINIMIZED );
    	}
    	
    	if( !NORMALIZED.equals( current ) ){
    		modes.add( NORMALIZED );
    	}
    	
    	if( !MAXIMIZED.equals( current ) && facile.isMaximizable() ){
    		modes.add( MAXIMIZED );
    	}
    	
    	if( !EXTERNALIZED.equals( current ) && facile.isExternalizable() ){
    		modes.add( EXTERNALIZED );
    	}
    	
    	return modes.toArray( new String[ modes.size() ] );
    }
    
    /**
     * Ensures that <code>dockable</code> is in a valid location (a mode that
     * is enabled by <code>dockable</code> and in the correct working area,
     * perhaps changes the current location to ensure that.
     * @param dockable the element which might not be in a valid location
     */
    public void ensureValidLocation( CDockable dockable ){
        ExtendedMode mode = getMode( dockable.intern() );
        if( mode == ExtendedMode.NORMALIZED ){
            CStation preferredArea = dockable.getWorkingArea();
            CStation currentArea = findFirstParentWorkingArea( dockable.intern() );
            
            if( preferredArea != currentArea ){
                if( preferredArea == null ){
                    dockable.setLocation( CLocation.base().normalRectangle( 0.25, 0.25, 0.5, 0.5 ) );
                }
                else{
                    dockable.setLocation( preferredArea.getStationLocation() );
                }
            }
            
            mode = getMode( dockable.intern() );
        }
        
        boolean wrong = 
            (mode == ExtendedMode.EXTERNALIZED && !dockable.isExternalizable() ) ||
            (mode == ExtendedMode.MAXIMIZED && !dockable.isMaximizable() ) ||
            (mode == ExtendedMode.MINIMIZED && !dockable.isMinimizable() );
        
        if( wrong ){
            setMode( dockable.intern(), ExtendedMode.NORMALIZED );
        }
    }
    
    /**
     * Finds the first {@link CStation} in the path up to the root from
     * <code>dockable</code> wich is a working area.
     * @param dockable the element which might have a {@link CStation}
     * as parent.
     * @return the first found {@link CStation}.
     */
    private CStation findFirstParentWorkingArea( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        dockable = station == null ? null : station.asDockable();
        
        if( dockable != null )
            return getAreaOf( dockable );
        else
            return null;
    }
    
    /**
     * Ensures that all {@link CDockable}s which have a working area as
     * parent, are in their preferred mode.
     */
    public void normalizeAllWorkingAreaChildren(){
        for( Dockable dockable : control.getOwner().intern().getController().getRegister().listDockables() ){
            if( dockable instanceof CommonDockable ){
                CDockable fdockable = ((CommonDockable)dockable).getDockable();
                if( fdockable.getWorkingArea() != null ){
                    ExtendedMode mode = fdockable.getWorkingArea().getStationLocation().findMode();
                    if( mode == null )
                        mode = ExtendedMode.NORMALIZED;
                    
                    if( !mode.equals( fdockable.getExtendedMode() )){
                        fdockable.setExtendedMode( mode );
                    }
                }
            }
        }
    }
        
    @Override
    protected void modeChanged( Dockable dockable, String oldMode, String newMode ) {
        if( newMode != null && !newMode.equals( oldMode ) ){
            if( dockable instanceof CommonDockable ){
                CDockable fdockable = ((CommonDockable)dockable).getDockable();
                CDockableAccess access = control.access( fdockable );
                if( access != null ){
                    CDockable.ExtendedMode extMode = convertMode( newMode );
                    access.informMode( extMode );
                }
            }
        }
    }
    
    @Override
    protected DockStation getDefaultNormal( Dockable dockable ) {
        if( dockable instanceof CommonDockable ){
            CDockable cdockable = ((CommonDockable)dockable).getDockable();
            if( cdockable.getWorkingArea() != null ){
                ExtendedMode mode = cdockable.getWorkingArea().getStationLocation().findMode();
                if( mode != ExtendedMode.NORMALIZED )
                    return null;
                
                return cdockable.getWorkingArea().getStation();
            }
        }
        
        return super.getDefaultNormal( dockable );
    }
    
    @Override
    public DockAction getIngoingAction( String mode, Dockable dockable ) {
        if( dockable instanceof CommonDockable ){
            CDockable cdock = ((CommonDockable)dockable).getDockable();
            CAction action = null;
            if( MINIMIZED.equals( mode ))
                action = cdock.getAction( CDockable.ACTION_KEY_MINIMIZE );
            else if( MAXIMIZED.equals( mode ))
                action = cdock.getAction( CDockable.ACTION_KEY_MAXIMIZE );
            else if( EXTERNALIZED.equals( mode ))
                action = cdock.getAction( CDockable.ACTION_KEY_EXTERNALIZE );
            else if( NORMALIZED.equals( mode ))
                action = cdock.getAction( CDockable.ACTION_KEY_NORMALIZE );
            
            if( action != null )
                return action.intern();
        }
        
        return super.getIngoingAction( mode, dockable );
    }
    
    @Override
    public void rebuild( Dockable dockable ) {
        super.rebuild( dockable );
    }
    
    /**
     * Invoked whenever a key is pressed, released or typed.
     * @param dockable the element to which the event belongs
     * @param event the event
     * @return <code>true</code> if the event has been processed, <code>false</code>
     * if the event was not used up.
     */
    protected boolean process( Dockable dockable, KeyEvent event ){
        if( dockable instanceof CommonDockable ){
            CDockable cdockable = ((CommonDockable)dockable).getDockable();

            KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( event );
            if( stroke.equals( keyStrokeMaximizeChange.getValue() )){
                if( cdockable.getExtendedMode() == CDockable.ExtendedMode.MAXIMIZED ){
                    goOut( MAXIMIZED, dockable );
                    return true;
                }
                else if( cdockable.isMaximizable() ){
                    cdockable.setExtendedMode( CDockable.ExtendedMode.MAXIMIZED );
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * A hook recording key-events for a specific {@link Dockable}.
     * @author Benjamin Sigg
     *
     */
    private class KeyHook extends DockRegisterAdapter implements KeyboardListener{
        /** the Dockable which is observed by this hook */
        private Dockable dockable;
        
        /**
         * Creates a new hook
         * @param dockable the element which will be observed until it is removed
         * from the {@link DockController}.
         */
        public KeyHook( Dockable dockable ){
            this.dockable = dockable;
            DockController controller = control.getOwner().intern().getController();
            controller.getKeyboardController().addListener( this );
            controller.getRegister().addDockRegisterListener( this );
        }
        
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            if( this.dockable == dockable ){
                controller.getKeyboardController().removeListener( this );
                controller.getRegister().removeDockRegisterListener( this );
            }
        }
        
        public DockElement getTreeLocation() {
            return dockable;
        }
        
        public boolean keyPressed( DockElement element, KeyEvent event ) {
            return process( dockable, event );
        }
        
        public boolean keyReleased( DockElement element, KeyEvent event ) {
            return process( dockable, event );
        }
        
        public boolean keyTyped( DockElement element, KeyEvent event ) {
            return process( dockable, event );
        }
    }
}
