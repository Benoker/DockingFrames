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
package bibliothek.gui.dock.title;

import java.awt.Component;
import java.awt.Point;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.MovingImage;

/**
 * A moving image that uses a {@link DockTitle} to paint its content.
 * @author Benjamin Sigg
 *
 */
public class TitleMovingImage implements MovingImage {
    
    /** the element which is represented by this image */
    private Dockable dockable;
    /** the contents of this image */
    private DockTitle title;
    
    /**
     * Creates a new image.
     * @param dockable the element which is represented by this image
     * @param title the contents of this image
     */
    public TitleMovingImage( Dockable dockable, DockTitle title ){
        if( dockable == null )
            throw new IllegalArgumentException( "Dockable must not be null" );
        
        if( title == null )
            throw new IllegalArgumentException( "Title must not be null" );
        
        this.dockable = dockable;
        this.title = title;
    }
    
    public Point getOffset( Point pressPoint ){
    	return null;
    }
    
    public void bind( boolean transparency ) {
        dockable.bind( title );
    }

    public Component getComponent() {
        return title.getComponent();
    }

    public void unbind() {
        dockable.unbind( title );
    }
}
