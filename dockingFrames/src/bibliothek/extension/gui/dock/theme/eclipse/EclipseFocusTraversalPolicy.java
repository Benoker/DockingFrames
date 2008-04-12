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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.awt.Component;
import java.awt.Container;

import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabComponent;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.focus.SimplifiedFocusTraversalPolicy;

/**
 * A policy observing an {@link EclipseStackDockComponent}, ensures that
 * either a {@link Dockable}, a tab or its actions are selected.
 * @author Benjamin Sigg
 */
public class EclipseFocusTraversalPolicy implements SimplifiedFocusTraversalPolicy {

    private EclipseStackDockComponent stack;
    
    /**
     * Creates a new traversal policy.
     * @param stack the component for which this policy will be used.
     */
    public EclipseFocusTraversalPolicy( EclipseStackDockComponent stack ){
        this.stack = stack;
    }
    
    protected Component[] list(){
        Component[] result = new Component[3];
        
        int index = stack.getSelectedIndex();
        if( index >= 0 ){
            Dockable dockable = stack.getDockable( index );
            if( dockable != null )
                result[0] = dockable.getComponent();
            
            EclipseTabbedComponent tabs = stack.getTabs();
            if( tabs != null ){
                TabComponent tab = tabs.getTabComponent( index );
                if( tab != null ){
                    result[1] = tab.getComponent();
                }
                
                result[2] = tabs.getItemPanel();
            }
        }
        
        return result;
    }
    
    public Component getAfter( Container container, Component component ) {
        Component[] list = list();
        for( int i = 0; i < list.length; i++ ){
            if( list[i] == component ){
                for( int k = 0; k < list.length; k++ ){
                    Component next = list[ (i+k+1) % list.length ];
                    if( next != null )
                        return next;
                }
                return null;
            }
        }
        return null;
    }

    public Component getBefore( Container container, Component component ) {
        Component[] list = list();
        for( int i = list.length-1; i >= 0; i-- ){
            if( list[i] == component ){
                for( int k = list.length-1; k >= 0; k-- ){
                    Component next = list[ (i+k) % list.length ];
                    if( next != null )
                        return next;
                }
                return null;
            }
        }
        
        return null;
    }

    public Component getDefault( Container container ) {
        return getFirst( container );
    }

    public Component getFirst( Container container ) {
        Component[] list = list();
        for( Component c : list ){
            if( c != null )
                return c;
        }
        
        return null;
    }

    public Component getLast( Container container ) {
        Component[] list = list();
        for( int i = list.length-1; i >= 0; i-- ){
            if( list[i] != null )
                return list[i];
        }
        return null;
    }
}
