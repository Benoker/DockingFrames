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
package bibliothek.gui.dock.themes.color;

import java.awt.Color;

import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.color.AbstractDockColor;

/**
 * A color used by a {@link DockTitle}.
 * @author Benjamin Sigg
 *
 */
public abstract class TitleColor extends AbstractDockColor{
    private DockTitle title;
    
    /**
     * Creates a new {@link TitleColor}.
     * @param id the id of the color
     * @param kind the kind of the color, can be <code>null</code>
     * @param title the title which uses this color
     * @param backup a backup, can be <code>null</code>
     */
    public TitleColor( String id, Class<? extends AbstractDockColor> kind, DockTitle title, Color backup ){
        super( id, kind, backup );
        if( title == null )
            throw new IllegalArgumentException( "title must not be null" );
        this.title = title;
    }
    
    /**
     * Gets the title which uses this color.
     * @return the title
     */
    public DockTitle getTitle() {
        return title;
    }
}
