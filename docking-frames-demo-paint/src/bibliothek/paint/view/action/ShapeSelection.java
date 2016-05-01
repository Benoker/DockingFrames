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
package bibliothek.paint.view.action;

import java.awt.*;

import javax.swing.Icon;

import bibliothek.gui.dock.common.action.CRadioButton;
import bibliothek.paint.model.Shape;
import bibliothek.paint.model.ShapeFactory;
import bibliothek.paint.view.Page;

/**
 * A button allowing to select one {@link ShapeFactory} which will forwarded
 * to a {@link Page}.
 * @author Benjamin Sigg
 *
 */
public class ShapeSelection extends CRadioButton {
    /** the page whose factory might be changed by this button */
    private Page page;
    /** the factory this button represents */
    private ShapeFactory factory;
    
    /**
     * Creates a new button
     * @param page the page whose factory will be replaced by <code>factory</code>
     * when this button is clicked by the user.
     * @param factory the factory which is represented by this button
     */
    public ShapeSelection( Page page, ShapeFactory factory ){
        this.page = page;
        this.factory = factory;
        setText( factory.getName() );
        setIcon( new ShapeIcon() );
    }
    
    @Override
    protected void changed() {
        if( isSelected() ){
            page.setFactory( factory );
        }
    }
    
    /**
     * An icon that uses a {@link Shape} to be painted.
     * @author Benjamin Sigg
     */
    private class ShapeIcon implements Icon{
        /** the shape which represents this icon */
        private Shape shape;
        
        /**
         * Creates a new icon
         */
        public ShapeIcon(){
            shape = factory.create();
            shape.setColor( Color.BLACK );
        }
        
        public int getIconHeight() {
            return 16;
        }
        
        public int getIconWidth() {
            return 16;
        }
        
        public void paintIcon( Component c, Graphics g, int x, int y ) {
            shape.setPointA( new Point( x+3, y+3 ) );
            shape.setPointB( new Point( x+13, y+13 ) );
            shape.paint( g, 1.0 );
        }
    }
}
