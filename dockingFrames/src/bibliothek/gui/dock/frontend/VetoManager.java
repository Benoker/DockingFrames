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
package bibliothek.gui.dock.frontend;

import java.util.*;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.event.VetoableDockFrontendEvent;
import bibliothek.gui.dock.event.VetoableDockFrontendListener;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * Used by a {@link DockFrontend} to correctly handle all {@link VetoableDockFrontendEvent}s.
 * @author Benjamin Sigg
 */
public class VetoManager {
    /** the owner of this manager */
    private DockFrontend frontend;
    
    /** the set of elements which are not yet shown but are expected to show up soon */
    private Set<Dockable> expectedToShow = new HashSet<Dockable>();
    
    /** the set of elements which are not yet hidden but are expected to hide soon */
    private Set<Dockable> expectedToHide = new HashSet<Dockable>();
    
    /** the set of elements which are shown */
    private Set<Dockable> dockables = new HashSet<Dockable>();

    /** A list of observers to be notified if a {@link Dockable} gets closed or opened */
    private List<VetoableDockFrontendListener> vetoableListeners = new ArrayList<VetoableDockFrontendListener>();
    
    public VetoManager( DockFrontend frontend ){
        if( frontend == null )
            throw new IllegalArgumentException( "Frontend must not be null" );
        
        this.frontend = frontend;
        
        frontend.addFrontendListener( new DockFrontendAdapter(){
            @Override
            public void shown( DockFrontend frontend, Dockable dockable ) {
                boolean expected = expectedToShow.remove( dockable );
                if( dockables.add( dockable )){
                    fireShown( dockable, expected );
                }
            }
            
            @Override
            public void hidden( DockFrontend fronend, Dockable dockable ) {
                boolean expected = expectedToHide.remove( dockable );
                if( dockables.remove( dockable )){
                    fireHidden( dockable, expected );
                }
            }
        });
    }
    
    /**
     * Gets the owner of this manager.
     * @return the owner
     */
    public DockFrontend getFrontend() {
        return frontend;
    }
    
    /**
     * Marks all elements of the tree with root <code>dockable</code> to be expected to hide soon.
     * @param dockable the root of the elements which will be hidden
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation completed successful, <code>false</code>
     * if the operation was aborted
     */
    public boolean expectToHide( Dockable dockable, boolean cancelable ){
        return expectToHide( DockUtilities.listDockables( dockable, true ), cancelable );
    }
    
    /**
     * Marks all elements of <code>dockables</code> to be expected to hide soon.
     * @param dockables the elements which will be hidden
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation completed successful, <code>false</code>
     * if the operation was aborted
     */
    public boolean expectToHide( Collection<Dockable> dockables, boolean cancelable ){
        boolean cancel = fireAllHiding( dockables, cancelable );
        if( cancel ){
            return false;
        }
        
        expectedToHide.addAll( dockables );
        return true;
    }


    /**
     * Marks all elements of the tree with root <code>dockable</code> to be expected to show soon.
     * @param dockable the root of the elements
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation completed successful, <code>false</code>
     * if the operation was aborted
     */
    public boolean expectToShow( Dockable dockable, boolean cancelable ){
        return expectToShow( DockUtilities.listDockables( dockable, true ), cancelable );
    }
    
    /**
     * Marks all elements of <code>dockables</code> to be expected to show soon.
     * @param dockables the elements which will be shown
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation completed successful, <code>false</code>
     * if the operation was aborted
     */
    public boolean expectToShow( Collection<Dockable> dockables, boolean cancelable ){
        boolean cancel = fireAllShowing( dockables, cancelable );
        if( cancel ){
            return false;
        }
        
        expectedToShow.addAll( dockables );
        return true;
    }
    
    /**
     * Adds <code>listener</code> to this frontend. The listener will be notified
     * when a {@link Dockable} will be or is closed.
     * @param listener the new listener
     */
    public void addVetoableListener( VetoableDockFrontendListener listener ){
        vetoableListeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this frontend.
     * @param listener the listener to remove
     */
    public void removeVetoableListener( VetoableDockFrontendListener listener ){
        vetoableListeners.remove( listener );
    }
    
    /**
     * Gets an independent array containing all currently registered
     * {@link VetoableDockFrontendListener}s.
     * @return the array of listeners
     */
    protected VetoableDockFrontendListener[] vetoableListeners(){
        return vetoableListeners.toArray( new VetoableDockFrontendListener[ vetoableListeners.size() ] );
    }
 

    /**
     * Calls the method {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * for all elements in the tree beginning with <code>dockable</code>.
     * @param dockable the root of the tree of elements to remove
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if not.
     */
    protected boolean fireAllHiding( Dockable dockable, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;
        
        List<Dockable> list = DockUtilities.listDockables( dockable, true );
        return fireAllHiding( list, cancelable );
    }
    
    
    /**
     * Calls the method {@link VetoableDockFrontendListener#hiding(VetoableDockFrontendEvent)}
     * for all elements in <code>dockables</code>
     * @param dockables a list of element which will be closed
     * @param cancelable whether the operation can be aborted
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if not.
     */
    protected boolean fireAllHiding( Collection<Dockable> dockables, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;
        
        if( dockables.isEmpty() )
            return false;
        
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, cancelable, true, dockables.toArray( new Dockable[ dockables.size()] ));
        
        for( VetoableDockFrontendListener listener : vetoableListeners() ){
            listener.hiding( event );
        }
        return event.isCanceled();
    }
    

    /**
     * Invokes the method {@link VetoableDockFrontendListener#hidden(VetoableDockFrontendEvent)}
     * for all listeners for all elements in <code>dockables</code>.
     * @param dockables the elements that were hidden
     * @param expected whether this event was expected or unexpected
     */
    protected void fireAllHidden( Collection<Dockable> dockables, final boolean expected ){
        if( !dockables.isEmpty() ){
            VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, false, expected, dockables.toArray( new Dockable[ dockables.size()] ));
            
            for( VetoableDockFrontendListener listener : vetoableListeners() ){
                listener.hidden( event );
            }
        }
    }

    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * on all listeners.
     * @param dockable the element that was shown
     * @param expected whether the event was expected or not
     */
    protected void fireHidden( Dockable dockable, boolean expected ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, false, expected, dockable );
        for( VetoableDockFrontendListener listener : vetoableListeners() )
            listener.hidden( event );
    }


    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#hidden(VetoableDockFrontendEvent)}
     * for all listeners for all elements of the tree with root <code>dockable</code>.
     * @param dockable the element that was hidden
     * @param expected whether this event was expected or unexpected
     */
    protected void fireAllHidden( Dockable dockable, final boolean expected ){
        fireAllHidden( DockUtilities.listDockables( dockable, true ), expected );
    }

    /**
     * Calls {@link VetoableDockFrontendListener#showing(VetoableDockFrontendEvent)}
     * for all elements in the tree with root <code>dockable</code>.
     * @param dockable the root of the tree that will become visible
     * @param cancelable whether the operation can be canceled
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if the operation can continue
     */
    protected boolean fireAllShowing( Dockable dockable, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;
        
        return fireAllShowing( DockUtilities.listDockables( dockable, true ), cancelable );
    }
    

    /**
     * Calls {@link VetoableDockFrontendListener#showing(VetoableDockFrontendEvent)}
     * for all elements in <code>dockables</code>.
     * @param dockables the element to show
     * @param cancelable whether the operation can be canceled
     * @return <code>true</code> if the operation was aborted, <code>false</code>
     * if the operation can continue
     */
    protected boolean fireAllShowing( Collection<Dockable> dockables, final boolean cancelable ){
        if( vetoableListeners.size() == 0 )
            return false;

        if( dockables.isEmpty() )
            return false;
        
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, cancelable, true, dockables.toArray( new Dockable[ dockables.size()] ));
        
        for( VetoableDockFrontendListener listener : vetoableListeners() ){
            listener.showing( event );
        }
        
        return event.isCanceled();
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * for all elements in the tree with root <code>dockable</code>.
     * @param dockable the root of the tree that is shown
     * @param expected whether the event is expected or not
     */
    protected void fireAllShown( Dockable dockable, final boolean expected ){
        if( vetoableListeners.size() == 0 )
            return;

        List<Dockable> list = DockUtilities.listDockables( dockable, true );
        fireAllShown( list, expected );
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * for all elements in <code>dockables</code>.
     * @param dockables the set of newly shown elements
     * @param expected whether the event is expected or not
     */
    protected void fireAllShown( Collection<Dockable> dockables, final boolean expected ){
        if( vetoableListeners.size() == 0 )
            return;

        if( !dockables.isEmpty() ){
            VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, false, expected, dockables.toArray( new Dockable[ dockables.size()] ));

            for( VetoableDockFrontendListener listener : vetoableListeners() ){
                listener.shown( event );
            }
        }
    }
    
    /**
     * Invokes the method {@link VetoableDockFrontendListener#shown(VetoableDockFrontendEvent)}
     * on all listeners.
     * @param dockable the element that was shown
     * @param expected whether the event was expected or not
     */
    protected void fireShown( Dockable dockable, boolean expected ){
        VetoableDockFrontendEvent event = new VetoableDockFrontendEvent( frontend, false, expected, dockable );
        for( VetoableDockFrontendListener listener : vetoableListeners() )
            listener.shown( event );
    }
}
