/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.SwingUtilities;

/**
 * A window provider which just returns the ancestor window of some {@link Component}
 * @author Benjamin Sigg
 */
public class ComponentWindowProvider extends AbstractWindowProvider{
    /** the child of the window to provide */
    private Component component;
    
    /** the current window of {@link #component} */
    private Window window;
    
    private HierarchyListener listener = new HierarchyListener(){
        public void hierarchyChanged( HierarchyEvent e ) {
            Window oldWindow = window;
            window = getWindowAncestor( component );
            if( oldWindow != window ){
                fireWindowChanged( window );
            }
        }
    };
    
    /**
     * Creates a new provider
     * @param component the component whose ancestor will be provided, <code>null</code>
     * is valid
     */
    public ComponentWindowProvider( Component component ){
        this.component = component;
    }
    
    @Override
    public void addWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        super.addWindowProviderListener( listener );
        if( previous == 0 && listeners.size() > 0 && component != null ){
            component.addHierarchyListener( this.listener );
            window = getWindowAncestor( component );
        }
    }
    
    @Override
    public void removeWindowProviderListener( WindowProviderListener listener ) {
        int previous = listeners.size();
        super.removeWindowProviderListener( listener );
        
        if( previous > 0 && listeners.size() == 0 && component != null ){
            component.removeHierarchyListener( this.listener );
        }
    }
    
    /**
     * Gets the {@link Component} whose ancestor window is provided.
     * @return the component or <code>null</code>
     */
    public Component getComponent() {
        return component;
    }
    
    /**
     * Sets the component whose ancestor window will be provided.
     * @param component the component or <code>null</code>
     */
    public void setComponent( Component component ) {
        if( listeners.size() == 0 ){
            this.component = component;
        }
        else{
            if( this.component != null )
                this.component.removeHierarchyListener( listener );
            
            this.component = component;
            
            if( this.component != null )
                this.component.addHierarchyListener( listener );
            
            Window oldWindow = window;
            window = component == null ? null : getWindowAncestor( component );
            if( oldWindow != window ){
                fireWindowChanged( window );
            }
        }
    }
    
    public Window searchWindow() {
        if( component == null )
            return null;
        
        if( listeners.size() == 0 )
            return getWindowAncestor( component );
        
        return window;
    }
    
    private Window getWindowAncestor( Component component ){
        if( component instanceof Window )
            return (Window)component;
        
        return SwingUtilities.getWindowAncestor( component );
    }
}
