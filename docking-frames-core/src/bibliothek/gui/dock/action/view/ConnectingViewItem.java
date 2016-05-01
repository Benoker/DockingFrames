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
package bibliothek.gui.dock.action.view;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;

/**
 * A connecting view item is a wrapper around another {@link ViewItem}. This 
 * item registers when a {@link DockController} is available and needed. It
 * does this by observing a {@link Dockable}.
 * @author Benjamin Sigg
 *
 * @param <A> the kind of item this represents
 */
public abstract class ConnectingViewItem<A> implements ViewItem<A>{
    /** the item that does most of the work */
    private ViewItem<A> delegate;
    /** the observed {@link DockElement} */
    private Dockable dockable;
    
    /** the currently available controller */
    private DockController controller;
    
    /** the current bind state */
    private boolean bound = false;
    
    /** a listener added to {@link #dockable} */
    private DockHierarchyListener listener = new DockHierarchyListener(){
        public void controllerChanged( DockHierarchyEvent event ) {
            check();
        }
        public void hierarchyChanged( DockHierarchyEvent event ) {
            // ignore
        }
    };
    
    /**
     * Creates a new item.
     * @param dockable the element to observe for a {@link DockController}.
     * @param delegate the delegate that carries out most of the work
     */
    public ConnectingViewItem( Dockable dockable, ViewItem<A> delegate ){
        if( dockable == null )
            throw new IllegalArgumentException( "dockable must not be null" );
        if( delegate == null )
            throw new IllegalArgumentException( "delegate must not be null" );
            
        this.dockable = dockable;
        this.delegate = delegate;
    }

    /**
     * Checks the current {@link DockController} and may replace the controller
     * when a new one is available.
     */
    public void check(){
        DockController current = bound ? dockable.getController() : null;
        if( current != controller ){
            changed( controller, current );
            controller = current;
        }
    }
    
    /**
     * Called when the {@link DockController} changed.
     * @param oldController the old controller, can be <code>null</code>
     * @param newController the new controller, can be <code>null</code>
     */
    protected abstract void changed( DockController oldController, DockController newController );
    
    public void bind(){
        if( !bound ){
            // if we would live in a perfect world, this check wouldn't be necessary
            bound = true;
            dockable.addDockHierarchyListener( listener );
            check();
        }
        
        delegate.bind();
    }

    public DockAction getAction() {
        return delegate.getAction();
    }

    public A getItem() {
        return delegate.getItem();
    }

    public void unbind() {
        if( bound ){
            bound = false;
            dockable.removeDockHierarchyListener( listener );
            check();
        }
        
        delegate.unbind();
    }
}
