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
package bibliothek.gui.dock.displayer;

import java.awt.Component;
import java.awt.Container;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.focus.SimplifiedFocusTraversalPolicy;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A traversal policy for {@link DockableDisplayer}s. This policy changes between
 * the {@link DockTitle} and the {@link Dockable} that is on the displayer.
 * @author Benjamin Sigg
 */
public class DisplayerFocusTraversalPolicy implements SimplifiedFocusTraversalPolicy {
    /** the owner of this policy */
    private DockableDisplayer displayer;
    
    /**
     * Creates a new policy.
     * @param displayer the owner of this policy
     */
    public DisplayerFocusTraversalPolicy( DockableDisplayer displayer ){
        if( displayer == null )
            throw new IllegalArgumentException( "Displayer must not be null" );
        
        this.displayer = displayer;
    }
    
    public Component getAfter( Container container, Component component ) {
        DockTitle title = displayer.getTitle();
        Dockable dockable = displayer.getDockable();
        
        if( dockable != null && dockable.getComponent() == component ){
            if( title != null )
                return title.getComponent();
        }
        else if( title != null && title.getComponent() == component ){
            if( dockable != null )
                return dockable.getComponent();
        }
        
        return null;
    }

    public Component getBefore( Container container, Component component ) {
        DockTitle title = displayer.getTitle();
        Dockable dockable = displayer.getDockable();
 
        if( dockable != null && dockable.getComponent() == component ){
            if( title != null )
                return title.getComponent();
        }
        else if( title != null && title.getComponent() == component ){
            if( dockable != null )
                return dockable.getComponent();
        }
        
        return null;
    }

    public Component getDefault( Container container ) {
        return getFirst( container );
    }

    public Component getFirst( Container container ) {
        DockTitle title = displayer.getTitle();
        Dockable dockable = displayer.getDockable();
        
        if( dockable != null )
            return dockable.getComponent();
        
        if( title != null )
            return title.getComponent();
        
        return null;
    }

    public Component getLast( Container container ) {
        DockTitle title = displayer.getTitle();
        Dockable dockable = displayer.getDockable();
        
        if( title != null )
            return title.getComponent();
        
        if( dockable != null )
            return dockable.getComponent();
        
        return null;
    }
}
