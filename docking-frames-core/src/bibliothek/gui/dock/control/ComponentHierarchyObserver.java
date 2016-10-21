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
package bibliothek.gui.dock.control;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.util.*;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.event.ComponentHierarchyObserverEvent;
import bibliothek.gui.dock.event.ComponentHierarchyObserverListener;
import bibliothek.gui.dock.event.DockControllerRepresentativeListener;

/**
 * A class collecting all {@link Component}s which are somehow used on or with
 * the {@link Dockable}s  in the realm of one {@link DockController}.<br>
 * A global instance of {@link ComponentHierarchyObserver} can be obtained
 * through {@link DockController#getComponentHierarchyObserver()}.<br>
 * Note that a hierarchy observer may also know {@link Component}s which are
 * not directly associated with {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class ComponentHierarchyObserver {
    /**
     * The set of components which were explicitly added to this observer and
     * will not be removed implicitly.
     */
    private Set<Component> roots = new HashSet<Component>();
    
    /** the currently known components */
    private Set<Component> components = new HashSet<Component>();
    
    /** a listener to all {@link Container}s */
    private Listener listener = new Listener();
    
    /** the controller in whose realm this observer works */
    private DockController controller;
    
    /** the observers of this {@link ComponentHierarchyObserver} */
    private List<ComponentHierarchyObserverListener> listeners =
        new ArrayList<ComponentHierarchyObserverListener>();
    
    /**
     * Creates a new observer.
     * @param controller the controller whose {@link Dockable}s will be observed.
     */
    public ComponentHierarchyObserver( DockController controller ){
        this.controller = controller;
        
        controller.addRepresentativeListener( new DockControllerRepresentativeListener(){
            public void representativeAdded( DockController controller, DockElementRepresentative representative ) {
                add( representative.getComponent() );
            }
            public void representativeRemoved( DockController controller, DockElementRepresentative representative ) {
                remove( representative.getComponent() );
            }
        });
    }
    
    /**
     * Gets a {@link Set} containing all {@link Component}s which are 
     * used on {@link Dockable}s known in the realm of the {@link DockController}
     * of this observer.
     * @return the set of <code>Component</code>s.
     */
    public Set<Component> getComponents() {
        return Collections.unmodifiableSet( components );
    }
    
    /**
     * Gets the controller in whose realm this observer searches for
     * {@link Component}s.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Adds a listener to this observer.
     * @param listener the new listener, not <code>null</code>
     */
    public void addListener( ComponentHierarchyObserverListener listener ){
        if( listener == null )
            throw new NullPointerException( "listener must not be null" );
        listeners.add( listener );
    }
    
    /**
     * Removes a listener from this observer.
     * @param listener the listener to remove
     */
    public void removeListener( ComponentHierarchyObserverListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets an array containing all listeners that are registered at this
     * observer.
     * @return the list of listeners
     */
    protected ComponentHierarchyObserverListener[] listeners(){
        return listeners.toArray( new ComponentHierarchyObserverListener[ listeners.size() ] );
    }
    
    /**
     * Adds <code>component</code> and all its children to the set of
     * known {@link Component}s. Components that are already known will
     * not be registered twice. This observer will notice when a child of
     * <code>component</code> changes and update itself accordingly.
     * @param component the new component
     */
    public void add( Component component ){
        roots.add( component );
        add( component, null );
    }
    
    /**
     * Adds <code>component</code> and all children of it to the set of
     * known {@link Component}s. This will add <code>component</code> as a
     * root, which prevents <code>component</code> from being removed
     * implicitly because its parent gets removed.
     * @param component the new component
     * @param list a list to be filled with the affected {@link Component}s,
     * can be <code>null</code> to indicate that this method has to fire
     * an event.
     */
    private void add( Component component, List<Component> list ){
        boolean fire = list == null;
        if( fire )
            list = new LinkedList<Component>();
        
        if( components.add( component )){
            list.add( component );
            if( component instanceof Container ){
                Container container = (Container)component;
                container.addContainerListener( listener );
                for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                    add( container.getComponent( i ), list );
                }
            }
        }
        
        if( fire && !list.isEmpty() ){
            list = Collections.unmodifiableList( list );
            ComponentHierarchyObserverEvent event = new ComponentHierarchyObserverEvent( controller, list );
            for( ComponentHierarchyObserverListener listener : listeners() )
                listener.added( event );
        }
    }
    
    /**
     * Removes <code>component</code> and all its children from the set
     * of known {@link Component}s. If a child was added as a root, then
     * the recursion stops there because roots can't be removed implicitly.
     * @param component the component to remove
     */
    public void remove( Component component ){
        roots.remove( component );
        remove( component, null );
    }
    
    /**
     * Removes <code>component</code> and all its children from the set
     * of known {@link Component}s.
     * @param component the removed component.
     * @param list a list to be filled with the affected {@link Component}s,
     * can be <code>null</code> to indicate that this method has to fire
     * an event.
     */
    private void remove( Component component, List<Component> list ){
        if( !roots.contains( component )){
            boolean fire = list == null;
            if( fire )
                list = new LinkedList<Component>();
            
            if( components.remove( component )){
                list.add( component );
                if( component instanceof Container ){
                    Container container = (Container)component;
                    container.removeContainerListener( listener );
                    for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                        remove( container.getComponent( i ), list );
                    }
                }
            }
            
            if( fire && !list.isEmpty() ){
                list = Collections.unmodifiableList( list );
                ComponentHierarchyObserverEvent event = new ComponentHierarchyObserverEvent( controller, list );
                for( ComponentHierarchyObserverListener listener : listeners() )
                    listener.removed( event );
            }
        }
    }
    
    /**
     * A listener to {@link Container}s, triggered when {@link Component}s
     * are added or removed.
     * @author Benjamin Sigg
     */
    private class Listener implements ContainerListener{
        public void componentAdded( ContainerEvent e ) {
            add( e.getChild(), null );
        }

        public void componentRemoved( ContainerEvent e ) {
            remove( e.getChild(), null );
        }
    }
}









