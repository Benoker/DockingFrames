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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.common.intern.DefaultFDockable;

/**
 * A <code>DefaultFSingleDockable</code> is an element which has a 
 * {@link #getContentPane() content-pane} where clients can add or remove as many
 * {@link java.awt.Component}s as they whish.
 * @author Benjamin Sigg
 * @see FSingleDockable
 */
public class DefaultFSingleDockable extends DefaultFDockable implements FSingleDockable{
    /** a unique id */
    private String id;
    
    /**
     * Creates a new dockable
     * @param id a unique id, not <code>null</code>
     */
    public DefaultFSingleDockable( String id ){
        if( id == null )
            throw new NullPointerException( "id must not be null" );
        
        this.id = id;
    }
    
    /**
     * Gets the id of this dockable. The id is unique if among all dockables
     * which are added to the same {@link FControl}.
     * @return the unique id
     */
    public String getUniqueId(){
        return id;
    }
}
