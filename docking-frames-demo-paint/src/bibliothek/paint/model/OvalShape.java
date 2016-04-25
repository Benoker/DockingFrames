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
package bibliothek.paint.model;

import java.awt.Graphics;
import java.awt.Point;

/**
 * An oval which fits into the rectangle that is created by two points.
 * @author Benjamin Sigg
 */
public class OvalShape extends Shape {
    /** a factory which creates {@link OvalShape}s */
    public static final ShapeFactory FACTORY = 
        new ShapeFactory(){
            public Shape create() {
                return new OvalShape();
            }
            public String getName() {
                return "Oval";
            }
        };   
    
    @Override
    public void paint( Graphics g, double stretch ) {
        g.setColor( getColor() );
        Point a = getPointA();
        Point b = getPointB();
        g.drawOval(
        		stretch( Math.min( a.x, b.x ), stretch ),
                stretch( Math.min( a.y, b.y ), stretch ),
                stretch( Math.abs( a.x - b.x ), stretch ),
                stretch( Math.abs( a.y - b.y ), stretch ));
    }
}
