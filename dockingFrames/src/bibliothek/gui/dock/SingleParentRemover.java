/**
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

package bibliothek.gui.dock;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockControllerAdapter;

/**
 * An observer of a {@link DockController}. The remover
 * ensures that there is no dockable {@link DockStation} with only one
 * or none child.
 * @author Benjamin Sigg
 */
public class SingleParentRemover{
	/** A listener added to a controller */
    private Listener listener = new Listener();
   
    /** state, ensures that no station is tested more than once in a run */
    private boolean onTest = false;
    
    /**
     * Commands this remover to observe <code>controller</code>.
     * @param controller a controller to observe
     */
    public void install( DockController controller ){
        controller.addDockControllerListener( listener );
    }
    
    /**
     * Commands this remover that it should no longer observe
     * <code>controller</code>.
     * @param controller a controller
     */
    public void uninstall( DockController controller ){
        controller.removeDockControllerListener( listener );
    }
    
    /**
     * Tests all stations of <code>controller</code> and removes
     * as many of them as possible
     * @param controller the controller to test
     */
    public void testAll( DockController controller ){
        if( onTest )
            return;
        
        try{
            onTest = true;
            
            int index = 0;
            while( index < controller.getStationCount() ){
                if( test( controller.getStation( index ))){
                    index = 0;
                }
                else
                    index++;
            }
        }
        finally{
            onTest = false;
        }
    }
    
    /**
     * Tells whether <code>station</code> should be automatically
     * removed or just be ignored.
     * @param station a station to test
     * @return <code>true</code> if the station may be removed
     * by this remover, <code>false</code> otherwise.
     */
    protected boolean shouldTest( DockStation station ){
        return true;
    }
    
    /**
     * Tries to replace <code>station</code> with its only child or 
     * remove <code>station</code> if it has no children at all. If the
     * parent of <code>station</code> refuses to accept the replacement
     * or <code>station</code> refuses to let its child go, nothing will
     * happen.
     * @param station the station to test
     * @return whether the station was replaced or removed
     */
    private boolean test( DockStation station ){
        if( !shouldTest( station ))
            return false;
        
        if( station.getDockableCount() > 1 )
            return false;
        
        Dockable transform = station.asDockable();
        if( transform == null )
            return false;
        
        DockStation parent = transform.getDockParent();
        
        if( parent == null )
            return false;
        
        if( parent.getController() == null )
            return false;
        
        if( station.getDockableCount() == 0 ){
            if( !parent.canDrag( transform ))
                return false;
            
            parent.drag( transform );
            return true;
        }
        else{
            Dockable dockable = station.getDockable( 0 );
            if( !station.canDrag( dockable ))
                return false;
            
            if( !parent.accept( dockable ) || !dockable.accept( parent ) )
                return false;
            
            if( !parent.canReplace( transform, dockable ))
                return false;
            
            DockAcceptance acceptance = station.getController().getAcceptance();
            
            if( acceptance != null ){
            	if( !acceptance.accept( parent, dockable ))
            		return false;
            }
            
            station.drag( dockable );
            parent.replace( transform, dockable );
            return true;
        }
    }
    
    /**
     * Listens to a {@link DockController} and call {@link SingleParentRemover#testAll(DockController)}
     * if the structure of the dock-tree changes.
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
        @Override
        public void dockablePut( DockController controller, Dockable dockable, DockStation station ) {
            testAll( controller );
        }
        
        @Override
        public void dockableUnregistered( DockController controller, Dockable dockable ) {
            if( !controller.isOnPut() ){
                testAll( controller );
            }
        }
    }
}
