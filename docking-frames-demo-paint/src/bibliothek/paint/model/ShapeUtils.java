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

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import bibliothek.util.xml.XElement;

/**
 * Some methods useful to work with {@link Shape}s.
 * @author Benjamin Sigg
 *
 */
public class ShapeUtils {
    /** factories for different types of shapes */
    private static final Map<String, ShapeFactory> FACTORIES;
    
    static{
        FACTORIES = new HashMap<String, ShapeFactory>();
        FACTORIES.put( RectangleShape.class.getCanonicalName(), RectangleShape.FACTORY );
        FACTORIES.put( LineShape.class.getCanonicalName(), LineShape.FACTORY );
        FACTORIES.put( OvalShape.class.getCanonicalName(), OvalShape.FACTORY );
    }
    
    /**
     * Gets a list of all {@link ShapeFactory}s which are used in this application.
     * @return the collection of factories
     */
    public static Collection<ShapeFactory> getFactories(){
        return Collections.unmodifiableCollection( FACTORIES.values() );
    }
    
    /**
     * Writes the contents of <code>shape</code> into <code>out</code>.
     * @param shape the shape to write
     * @param out the stream to write into
     * @throws IOException if an I/O error occurs
     */
    public static void write( Shape shape, DataOutputStream out ) throws IOException{
        String key = shape.getClass().getCanonicalName();
        out.writeUTF( key );
        out.writeInt( shape.getColor().getRGB() );
        out.writeInt( shape.getPointA().x );
        out.writeInt( shape.getPointA().y );
        out.writeInt( shape.getPointB().x );
        out.writeInt( shape.getPointB().y );
    }
    
    /**
     * Writes the contents of <code>shape</code> in xml format.
     * @param shape the shape to write
     * @param element the element to write into
     */
    public static void writeXML( Shape shape, XElement element ){
        element.addElement( "class" ).setString( shape.getClass().getCanonicalName() );
        element.addElement( "color" ).setInt( shape.getColor().getRGB() );
        element.addElement( "point" ).addInt( "x", shape.getPointA().x ).addInt( "y", shape.getPointA().y );
        element.addElement( "point" ).addInt( "x", shape.getPointB().x ).addInt( "y", shape.getPointB().y );
    }
    
    /**
     * Reads a {@link Shape} from the stream <code>in</code>.
     * @param in the stream to read from
     * @return the newly read <code>Shape</code>
     * @throws IOException if an I/O error occurs
     */
    public static Shape read( DataInputStream in ) throws IOException{
        String key = in.readUTF();
        Shape shape = FACTORIES.get( key ).create();
        shape.setColor( new Color( in.readInt() ) );
        shape.setPointA( new Point( in.readInt(), in.readInt() ) );
        shape.setPointB( new Point( in.readInt(), in.readInt() ) );
        return shape;
    }
    
    /**
     * Reads a {@link Shape} from <code>element</code>.
     * @param element the element to read from
     * @return the newly created <code>Shape</code>
     */
    public static Shape readXML( XElement element ){
        Shape shape = FACTORIES.get( element.getElement( "class" ).getString() ).create();
        shape.setColor( new Color( element.getElement( "color" ).getInt() ) );
        XElement[] points = element.getElements( "point" );
        shape.setPointA( new Point( points[0].getInt( "x" ), points[0].getInt( "y" ) ));
        shape.setPointB( new Point( points[1].getInt( "x" ), points[1].getInt( "y" ) ));
        return shape;
    }
}
