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

package bibliothek.gui.dock.control;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockControllerListener;
import bibliothek.gui.dock.event.FocusVetoListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A FocusController listens to all AWT-events. As soon as the mouse is pressed
 * over a {@link Dockable}, the FocusController will inform the {@link DockController}
 * about a new {@link DockController#setFocusedDockable(Dockable, boolean) front-dockable}.
 * @author Benjamin Sigg
 */
public abstract class MouseFocusObserver implements DockControllerListener {
    /** a list of all Dockables and their base-component */
    private Map<Component, Dockable> dockables = new HashMap<Component, Dockable>();
    /** a list of all DockTitles and their base-component */
    private Map<Component, DockTitle> titles = new HashMap<Component, DockTitle>();
    
    /** A list of listeners which can cancel a call to the controller */
    private List<FocusVetoListener> vetos = new ArrayList<FocusVetoListener>();
    
    /** The controller to be informed about changes */
    private DockController controller;
    
    /**
     * Creates a new FocusController.
     * @param controller the controller which will be informed about
     * focus-changes
     */
    public MouseFocusObserver( DockController controller ){
        this.controller = controller;
        controller.addDockControllerListener( this );
    }
    
    /**
     * Stops this FocusController. This controller will remove all
     * its listeners and become ready for the garbage collector. 
     */
    public void kill(){
    	// nothing to do
    }
    
    /**
     * Gets the affected controller.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Adds a listener to this controller which can cancel a call to
     * the {@link DockController}.
     * @param listener the new listener
     */
    public void addVetoListener( FocusVetoListener listener ){
        vetos.add( listener );
    }
    
    /**
     * Removes a listener from this controller
     * @param listener the listener to remove
     */
    public void removeVetoListener( FocusVetoListener listener ){
        vetos.remove( listener );
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(MouseFocusObserver, DockTitle)}
     * whether they want cancel a call to the {@link DockController}.
     * @param title the title which was hit by the mouse
     * @return <code>true</code> if at least one veto was made,
     * <code>false</code> otherwise
     */
    protected boolean fireVetoTitle( DockTitle title ){
        for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] ))
            if( listener.vetoFocus( this, title ))
                return true;
        
        return false;
    }
    
    /**
     * Asks all {@link FocusVetoListener} through their method
     * {@link FocusVetoListener#vetoFocus(MouseFocusObserver, Dockable)}
     * whether they want cancel a call to the {@link DockController}.
     * @param dockable the Dockable which was hit by the mouse
     * @return <code>true</code> if at least one veto was made,
     * <code>false</code> otherwise
     */    
    protected boolean fireVetoDockable( Dockable dockable ){
        for( FocusVetoListener listener : vetos.toArray( new FocusVetoListener[ vetos.size() ] ))
            if( listener.vetoFocus( this, dockable ))
                return true;
        
        return false;
    }
    
    /**
     * Lets check this controller whether the focus should change, or not. Clients
     * invoking this method should first check whether <code>event</code> is
     * allowed to change the focus or not. This check can be done through the
     * method {@link #interact(AWTEvent)}.
     * @param event The event to react on.
     * @see #interact(AWTEvent)
     */
    protected void check( AWTEvent event ){
        if( controller.getRelocator().isOnPut() || controller.isOnFocusing() )
            return;
        
        Object source = event.getSource();
        if( source instanceof Component ){
            Component component = (Component)source;
            if( event.getID() == MouseEvent.MOUSE_PRESSED )
                check( component, !component.isFocusable() );
            else
                check( component );
        }
    }
    
    /**
     * Tells whether this event should change the focus.
     * @param event the event
     * @return <code>true</code> if the focus could be changed
     */
    protected boolean interact( AWTEvent event ){
        int id = event.getID();
        
        return id == MouseEvent.MOUSE_PRESSED ||
            id == MouseEvent.MOUSE_WHEEL;
    }
    
    /**
     * Tries to find the Dockable which owns <code>component</code>
     * and sets this Dockable to the focusedDockable. The method
     * only succeeds if no veto-listener reacts.
     * @param component the component whose dockable parent is to set
     * focused
     */
    protected void check( Component component ){
        check( component, true );
    }
    
    /**
     * Tries to find the Dockable which owns <code>component</code>
     * and sets this Dockable to the focusedDockable. The method
     * only succeeds if no veto-listener reacts.
     * @param component the component whose dockable parent is to set
     * focused
     * @param ensureFocus whether the DockController should ensure
     * that the focus is set correctly or not.
     */
    protected void check( Component component, boolean ensureFocus ){
        Dockable dock = getDockable( component );
        if( dock != null ){
            Dockable focused = controller.getFocusedDockable();
            boolean change = true;
            if( focused != null )
                change = !DockUtilities.isAnchestor( dock, focused );
            
            if( change )
                controller.setFocusedDockable( dock, false, ensureFocus );
        }
    }
    
    public void dockableRegistered( DockController controller, Dockable dockable ) {
        dockables.put( dockable.getComponent(), dockable );
    }
    
    public void dockablePut( final DockController controller, final Dockable dockable, DockStation station ) {
        EventQueue.invokeLater( new Runnable(){
            public void run(){
                //controller.setAtLeastFocusedDockable( dockable );
                controller.setFocusedDockable( dockable, true );
            }
        });
    }
    
    public void dockableDrag( DockController controller, Dockable dockable, DockStation station ) {
        // do nothing
    }
    
    public void dockableUnregistered( DockController controller, Dockable dockable ) {
        dockables.remove( dockable.getComponent() );
    }
    
    public void titleBinded( DockController controller, DockTitle title, Dockable dockable ) {
        titles.put( title.getComponent(), title );
    }
    public void titleUnbinded( DockController controller, DockTitle title, Dockable dockable ) {
        titles.remove( title.getComponent() );
    }
    
    /**
     * Gets the top-dockable which has <code>component</code> or 
     * parent of <code>component</code> as base Component.
     * @param component a Component
     * @return a Dockable or <code>null</code> if nothing was found or
     * a {@link FocusVetoListener} doesn't want to inform the controller
     */
    protected Dockable getDockable( Component component ){
        Dockable dock = null;
        
        while( component != null && dock == null ){
            dock = dockables.get( component );
            
            if( dock == null ){
                DockTitle title = titles.get( component );
                
                if( title != null ){
                    if( fireVetoTitle( title ))
                        return null;
                    
                    dock = title.getDockable();
                }
            }
            else{
                if( fireVetoDockable( dock ))
                    return null;
            }
            
            component = component.getParent();
        }
        
        return dock;
    }
    
    public void dockStationUnregistered( DockController controller, DockStation station ) {
        // do nothing
    }
    public void dockStationRegistered( DockController controller, DockStation station ) {
        // do nothing
    }
    
    public void dockableRegistering( DockController controller, Dockable dockable ) {
        // do nothing
    }
    public void dockStationRegistering( DockController controller, DockStation station ) {
        // do nothing
    }
    public void dockableFocused( DockController controller, Dockable dockable ) {
        // do nothing
    }
}
