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
package bibliothek.gui.dock.facile.intern;

import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.common.action.StateManager;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.event.KeyboardListener;
import bibliothek.gui.dock.facile.FContentArea;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FLocation;
import bibliothek.gui.dock.facile.FWorkingArea;
import bibliothek.gui.dock.facile.intern.FDockable.ExtendedMode;
import bibliothek.gui.dock.facile.location.*;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;
import bibliothek.gui.dock.station.stack.StackDockProperty;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;
import bibliothek.util.container.Single;

/**
 * A manager that can change the extended-state of {@link FDockable}s
 * @author Benjamin Sigg
 *
 */
public class FStateManager extends StateManager {
    /** access to the {@link FControl} that uses this manager */
    private FControlAccess control;
    
    /**
     * {@link KeyStroke} used to go into, or go out from the maximized state.
     */
    private PropertyValue<KeyStroke> keyStrokeMaximizeChange = new PropertyValue<KeyStroke>( FControl.KEY_MAXIMIZE_CHANGE ){
        @Override
        protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
            // nothing to do 
        }
    };
    
    /**
     * Creates a new manager
     * @param control internal access to the {@link FControl} that uses this manager
     */
    public FStateManager( FControlAccess control ){
        super( control.getOwner().intern().getController() );
        this.control = control;
        DockController controller = control.getOwner().intern().getController();
        
        // add hook to get key-events for all Dockables
        controller.getRegister().addDockRegisterListener( new DockControllerAdapter(){
            @Override
            public void dockableRegistered( DockController controller, Dockable dockable ) {
                new KeyHook( dockable );
            }
        });
        
        // using keystrokes
        keyStrokeMaximizeChange.setProperties( controller );
        
        new PropertyValue<KeyStroke>( FControl.KEY_GOTO_EXTERNALIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( EXTERNALIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( FControl.KEY_GOTO_MAXIMIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( MAXIMIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( FControl.KEY_GOTO_MINIMIZED, controller ){
            @Override
            protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ) {
                getIngoingAction( MINIMIZED ).setAccelerator( newValue );
            }
        };
        
        new PropertyValue<KeyStroke>( FControl.KEY_GOTO_NORMALIZED, controller ){
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
             * @param dockable the element to search for <code>FacileDockable</code>s
             * @return <code>true</code> if all elements are externalizable
             */
            private boolean externalizable( Dockable dockable ){
                final Single<Boolean> result = new Single<Boolean>( true );
                DockUtilities.visit( dockable, new DockUtilities.DockVisitor(){
                    @Override
                    public void handleDockable( Dockable dockable ) {
                        if( dockable instanceof FacileDockable ){
                            if( !((FacileDockable)dockable).getDockable().isExternalizable() ){
                                result.setA( false );
                            }
                        }
                    }
                });
                return result.getA();
            }
        });
        
        try {
            control.getOwner().getResources().put( "FStateManager", new ApplicationResource(){
                public void write( DataOutputStream out ) throws IOException {
                    FStateManager.this.write( new LocationStreamTransformer(), out );
                }
                public void read( DataInputStream in ) throws IOException {
                    FStateManager.this.read( new LocationStreamTransformer(), in );
                }
            });
        } catch( IOException e ) {
            System.err.println( "Non lethal IO-error:");
            e.printStackTrace();
        }
    }
    
    /**
     * Changes the mode of <code>dockable</code>.
     * @param dockable an element whose mode will be changed
     * @param mode the new mode
     */
    public void setMode( Dockable dockable, FDockable.ExtendedMode mode ) {
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
    public FDockable.ExtendedMode getMode( Dockable dockable ){
        String mode = currentMode( dockable );
        
        if( EXTERNALIZED.equals( mode ))
            return FDockable.ExtendedMode.EXTERNALIZED;
        else if( MINIMIZED.equals( mode ))
            return FDockable.ExtendedMode.MINIMIZED;
        else if( MAXIMIZED.equals( mode ))
            return FDockable.ExtendedMode.MAXIMIZED;
        else if( NORMALIZED.equals( mode ))
            return FDockable.ExtendedMode.NORMALIZED;
        
        return null;
    }
    
    /**
     * Tries to set the location of <code>dockable</code>.
     * @param dockable the element to move
     * @param location the new location of <code>dockable</code>
     */
    public void setLocation( FacileDockable dockable, FLocation location ){
    	String root = location.findRoot();
    	DockableProperty property = location.findProperty();
    	ExtendedMode mode = location.findMode();
    	String newMode = null;
    	
    	if( root != null && mode != null ){
	        switch( mode ){
		        case EXTERNALIZED:
		            newMode = EXTERNALIZED;
		            break;
		        case MAXIMIZED:
		        	newMode = MAXIMIZED;
		            break;
		        case MINIMIZED:
		        	newMode = MINIMIZED;
		            break;
		        case NORMALIZED:
		        	newMode = NORMALIZED;
		            break;
		    }
	        
	        // ensure the correct FWorkingArea is set.
            boolean set = false;
            if( root != null ){
                DockStation station = control.getOwner().intern().getRoot( root );
                if( station != null ){
                    Dockable stationDockable = station.asDockable();
                    if( stationDockable instanceof FacileDockable ){
                        FDockable fdockable = ((FacileDockable)stationDockable).getDockable();
                        if( fdockable instanceof FWorkingArea ){
                            dockable.getDockable().setWorkingArea( (FWorkingArea)fdockable );
                            set = true;
                            newMode = NORMALIZED;
                        }
                    }
                }
            }
            if( !set ){
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
     * Gets an element describing the location of <code>dockable</code> as
     * good as possible.
     * @param dockable the element whose location should be searched
     * @return the location or <code>null</code> if no location was found
     */
    public FLocation getLocation( Dockable dockable ){
    	if( getMode( dockable ) == ExtendedMode.MAXIMIZED )
    		return new FMaximizedLocation();
    	
    	DockableProperty property = DockUtilities.getPropertyChain( dockable );
    	return fill( null, property, dockable );
    }
    
    /**
     * Analyzes the contents of <code>property</code> and tries to create an
     * {@link FLocation} that matches <code>property</code> as good as possible. 
     * @param base the parent of the next location to create, can be <code>null</code>
     * @param property the property whose location should become a child of
     * <code>base</code>, can be <code>null</code>
     * @param dockable the element whose location is analyzed
     * @return either a location matching <code>property</code> and all its successors
     * as good as possible, <code>base</code> in case <code>property</code> can't
     * be analyzed or <code>null</code> if no analysis is possible at all
     */
    protected FLocation fill( FLocation base, DockableProperty property, Dockable dockable ){
    	if( property == null )
    		return base;
    	
    	if( base == null ){
	    	if( property instanceof ScreenDockProperty ){
	    		ScreenDockProperty screen = (ScreenDockProperty)property;
	    		FExternalizedLocation extern = new FExternalizedLocation(
    				screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight() );
	    		return fill( extern, screen.getSuccessor(), dockable );
	    	}
	    	else{
	    	    FWorkingArea workingArea = null;
	    	    if( dockable instanceof FacileDockable ){
	    	        workingArea = ((FacileDockable)dockable).getDockable().getWorkingArea();
	    	    }
	    	    if( workingArea == null ){
	    	        base = new FBaseLocation( getCenterOf( dockable ) );
	    	    }
	    	    else{
	    	        base = new FWorkingAreaLocation( workingArea );
	    	    }
	    	}
    	}
    	
    	if( property instanceof StackDockProperty ){
    		int index = ((StackDockProperty)property).getIndex();
    		return fill( new FStackLocation( base, index ), property.getSuccessor(), dockable );
    	}
    	
    	if( property instanceof FlapDockProperty ){
    		if( base instanceof FBaseLocation ){
    			FBaseLocation location = (FBaseLocation)base;
    			FlapDockProperty flap = (FlapDockProperty)property;
    			
    			FContentArea center = location.getContentArea();
    			String root = getRootName( dockable );
    			if( root == null )
    				return base;
    			
    			if( center == null ){
    				if( root.equals( FContentArea.getNorthIdentifier( FControl.CONTENT_AREA_STATIONS_ID ) )){
    					return fill( location.minimalNorth( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( FContentArea.getSouthIdentifier( FControl.CONTENT_AREA_STATIONS_ID ) )){
    					return fill( location.minimalSouth( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( FContentArea.getEastIdentifier( FControl.CONTENT_AREA_STATIONS_ID ) )){
    					return fill( location.minimalEast( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( FContentArea.getWestIdentifier( FControl.CONTENT_AREA_STATIONS_ID ) )){
    					return fill( location.minimalWest( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    			}
    			else{
    				if( root.equals( center.getNorthIdentifier() )){
    					return fill( location.minimalNorth( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( center.getSouthIdentifier() )){
    					return fill( location.minimalSouth( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( center.getEastIdentifier() )){
    					return fill( location.minimalEast( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}
    				if( root.equals( center.getWestIdentifier() )){
    					return fill( location.minimalWest( flap.getIndex() ), flap.getSuccessor(), dockable );
    				}    				
    			}
    		}
    		
    		return null;
    	}
    	
    	if( property instanceof SplitDockProperty ){
    		if( base instanceof FBaseLocation ){
    			SplitDockProperty split = (SplitDockProperty)property;
    			return fill( ((FBaseLocation)base).normalRectangle( split.getX(), split.getY(), split.getWidth(), split.getHeight() ), split.getSuccessor(), dockable );
    		}
    	}
    	
    	if( property instanceof SplitDockPathProperty ){
    		if( base instanceof FBaseLocation ){
    			SplitDockPathProperty path = (SplitDockPathProperty)property;
    			AbstractFCenterTreeLocation tree = null;
    			for( SplitDockPathProperty.Node node : path ){
    				Side side = null;
    				
    				switch( node.getLocation() ){
    					case TOP: 
    						side = Side.NORTH;
    						break;
    					case BOTTOM:
    						side = Side.SOUTH;
    						break;
    					case LEFT: 
    						side = Side.WEST;
    						break;
    					case RIGHT:
    						side = Side.EAST;
    						break;
    				}
    				
    				if( tree == null ){
    					tree = new FCenterTreeLocationRoot( (FBaseLocation)base, node.getSize(), side );
    				}
    				else{
    					tree = new FCenterTreeLocationNode( tree, node.getSize(), side );
    				}
    			}
    			
    			return fill( tree, path.getSuccessor(), dockable );
    		}
    	}
    	
    	return base;
    }
    
    /**
     * Searches the {@link FContentArea} on which <code>dockable</code> is shown.
     * @param dockable the element whose center is searched
     * @return the center or <code>null</code>
     */
    protected FContentArea getCenterOf( Dockable dockable ){
    	DockStation station = DockUtilities.getRoot( dockable );
    	if( station == null )
    		return null;
    	
    	for( FContentArea center : control.getOwner().getContentAreas() ){
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
    	if( !(dockable instanceof FacileDockable )){
    		return new String[0];
    	}
    	
    	FDockable facile = ((FacileDockable)dockable).getDockable();
    	if( facile.getWorkingArea() != null ){
    	    return new String[]{};
    	}
    	
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
     * is enabled by <code>dockable</code> and in the correct {@link FWorkingArea}),
     * perhaps changes the current location to ensure that.
     * @param dockable the element which might not be in a valid location
     */
    public void ensureValidLocation( FDockable dockable ){
        FWorkingArea preferredArea = dockable.getWorkingArea();
        FWorkingArea currentArea = findFirstParentWorkingArea( dockable.intern() );
        
        if( preferredArea != currentArea ){
            if( preferredArea == null ){
                dockable.setLocation( FLocation.base().normalRectangle( 0.25, 0.25, 0.5, 0.5 ) );
            }
            else{
                dockable.setLocation( FLocation.working( preferredArea ).rectangle( 0.25, 0.25, 0.5, 0.5 ) );
            }
        }
        
        ExtendedMode mode = getMode( dockable.intern() );
        boolean wrong = 
            (mode == ExtendedMode.EXTERNALIZED && !dockable.isExternalizable() ) ||
            (mode == ExtendedMode.MAXIMIZED && !dockable.isMaximizable() ) ||
            (mode == ExtendedMode.MINIMIZED && !dockable.isMinimizable() );
        
        if( wrong ){
            setMode( dockable.intern(), ExtendedMode.NORMALIZED );
        }
    }
    
    /**
     * Finds the first {@link FWorkingArea} in the path up to the root from
     * <code>dockable</code>.
     * @param dockable the element which might have a {@link FWorkingArea}
     * as parent.
     * @return the first found {@link FWorkingArea}.
     */
    private FWorkingArea findFirstParentWorkingArea( Dockable dockable ){
        DockStation station = dockable.getDockParent();
        dockable = station == null ? null : station.asDockable();
        
        while( dockable != null ){
            if( dockable instanceof FacileDockable ){
                FDockable fdock = ((FacileDockable)dockable).getDockable();
                if( fdock instanceof FWorkingArea )
                    return (FWorkingArea)fdock;
            }
            
            station = dockable.getDockParent();
            dockable = station == null ? null : station.asDockable();
        }
        
        return null;
    }
    
    @Override
    protected void transition( String oldMode, String newMode, Dockable dockable ) {
        super.transition( oldMode, newMode, dockable );
        if( dockable instanceof FacileDockable ){
            FDockable fdockable = ((FacileDockable)dockable).getDockable();
            FDockableAccess access = control.access( fdockable );
            if( access != null ){
                FDockable.ExtendedMode mode = getMode( dockable );
                access.informMode( mode );
            }
        }
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
        if( dockable instanceof FacileDockable ){
            FDockable fdockable = ((FacileDockable)dockable).getDockable();

            KeyStroke stroke = KeyStroke.getKeyStrokeForEvent( event );
            if( stroke.equals( keyStrokeMaximizeChange.getValue() )){
                if( fdockable.getExtendedMode() == FDockable.ExtendedMode.MAXIMIZED ){
                    goOut( MAXIMIZED, dockable );
                    return true;
                }
                else if( fdockable.isMaximizable() ){
                    fdockable.setExtendedMode( FDockable.ExtendedMode.MAXIMIZED );
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
    private class KeyHook extends DockControllerAdapter implements KeyboardListener{
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
