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
package bibliothek.extension.gui.dock.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A class that can observe a {@link Component} and the children of the same component
 * to find out whether the mouse is over the base component. This also works if
 * the children of the base component do not let {@link MouseEvent}s through.
 * @author Benjamin Sigg
 */
public abstract class MouseOverListener extends MouseAdapter implements ContainerListener{
    /** whether the mouse is currently over the component */
    private boolean mouseover = false;
    
    /**
     * Creates a new listener
     * @param parent the component to observe
     */
    public MouseOverListener( Component parent ){
        added( parent );
    }
    
    @Override
    public void mouseEntered( MouseEvent e ) {
        mouseover = true;
        changed();
    }
    @Override
    public void mouseExited( MouseEvent e ) {
        mouseover = false;
        changed();
    }
    
    /**
     * Called whenever the mouse-over state changed.
     */
    protected abstract void changed();
    
    /**
     * Tells whether the mouse is currently over the base component.
     * @return <code>true</code> if the mouse is within the borders of the
     * base component
     */
    public boolean isMouseOver() {
        return mouseover;
    }
    
    public void componentAdded( ContainerEvent e ){
        added( e.getChild() );
    }
    
    /**
     * Adds listeners to <code>component</code> and its children.
     * @param component the new component to observe
     */
    protected void added( Component component ){
        component.addMouseListener( this );
        if( component instanceof Container ){
            Container container = (Container)component;
            container.addContainerListener( this );
            for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                added( container.getComponent( i ));
            }
        }
    }
    
    public void componentRemoved( ContainerEvent e ){
        removed( e.getChild() );
    }
    
    /**
     * Removes listeners from <code>component</code> and its children.
     * @param component the component which should no longer be observed
     */
    protected void removed( Component component ){
        component.removeMouseListener( this );
        if( component instanceof Container ){
            Container container = (Container)component;
            container.removeContainerListener( this );
            for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                removed( container.getComponent( i ));
            }
        }
    }
}