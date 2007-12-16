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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockAcceptance;
import bibliothek.gui.dock.common.action.StateManager;
import bibliothek.gui.dock.facile.FControl;
import bibliothek.gui.dock.facile.FDockable;
import bibliothek.gui.dock.facile.FDockable.ExtendedMode;
import bibliothek.gui.dock.station.ScreenDockStation;
import bibliothek.gui.dock.support.util.ApplicationResource;
import bibliothek.gui.dock.util.DockUtilities;
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
     * Creates a new manager
     * @param control internal access to the {@link FControl} that uses this manager
     */
    public FStateManager( FControlAccess control ){
        super( control.getOwner().intern().getController() );
        this.control = control;
        
        DockController controller = control.getOwner().intern().getController();
        
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
    
    @Override
    protected String[] availableModes( String current, Dockable dockable ){
    	if( !(dockable instanceof FacileDockable )){
    		return new String[0];
    	}
    	
    	FDockable facile = ((FacileDockable)dockable).getDockable();
    	
    	List<String> modes = new ArrayList<String>( 4 );
    	
    	if( !MINIMIZED.equals( current ) && facile.isMinimizable() )
    		modes.add( MINIMIZED );
    	
    	if( !NORMALIZED.equals( current ) )
    		modes.add( NORMALIZED );
    	
    	if( !MAXIMIZED.equals( current ) && facile.isMaximizable() )
    		modes.add( MAXIMIZED );
    	
    	if( !EXTERNALIZED.equals( current ) && facile.isExternalizable() )
    		modes.add( EXTERNALIZED );
    	
    	return modes.toArray( new String[ modes.size() ] );
    }
    
    /**
     * Ensures that <code>dockable</code> is in a valid mode (a mode that
     * is enabled by <code>dockable</code>), perhaps changes the current mode
     * to ensure that.
     * @param dockable the element which might not be in a valid mode
     */
    public void ensureValidMode( FDockable dockable ){
        ExtendedMode mode = getMode( dockable.intern() );
        boolean wrong = 
            (mode == ExtendedMode.EXTERNALIZED && !dockable.isExternalizable() ) ||
            (mode == ExtendedMode.MAXIMIZED && !dockable.isMaximizable() ) ||
            (mode == ExtendedMode.MINIMIZED && !dockable.isMinimizable() );
        
        if( wrong ){
            setMode( dockable.intern(), ExtendedMode.NORMALIZED );
        }
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
}
