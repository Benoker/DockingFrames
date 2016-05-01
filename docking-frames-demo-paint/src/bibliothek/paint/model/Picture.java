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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bibliothek.util.xml.XElement;

/**
 * A set of {@link Shape}s forming some picture.
 * @author Benjamin Sigg
 *
 */
public class Picture {
    /** the shapes of this picture */
    private List<Shape> shapes = new ArrayList<Shape>();
    
    /** the observers of this picture */
    private List<PictureListener> listeners = new ArrayList<PictureListener>();
   
    /** the name of this picture */
    private String name;
    
    /**
     * Creates a new picture.
     * @param name the name of this picture
     */
    public Picture( String name ){
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    /**
     * Writes the contents of this picture.
     * @param out the stream to write into
     * @throws IOException if an I/O error occurs
     */
    public void write( DataOutputStream out ) throws IOException{
        out.writeUTF( name );
        out.writeInt( shapes.size() );
        for( Shape shape : shapes ){
            ShapeUtils.write( shape, out );
        }
    }
    
    /**
     * Writes the contents of this picture.
     * @param element the element to write into
     */
    public void writeXML( XElement element ){
        element.addElement( "name" ).setString( name );
        XElement xshapes = element.addElement( "shapes" );
        for( Shape shape : shapes ){
            ShapeUtils.writeXML( shape, xshapes.addElement( "shape" ) );
        }
    }
    
    /**
     * Reads the contents of this picture from <code>in</code>.
     * @param in the stream to read from
     * @throws IOException if an I/O error occurs
     */
    public void read( DataInputStream in ) throws IOException{
        name = in.readUTF();
        shapes.clear();
        for( int i = 0, n = in.readInt(); i<n; i++ ){
            shapes.add( ShapeUtils.read( in ) );
        }
        
        for( PictureListener listener : listeners.toArray( new PictureListener[ listeners.size() ] ))
            listener.pictureChanged();
    }
    
    public void readXML( XElement element ){
        name = element.getElement( "name" ).getString();
        XElement xshapes = element.getElement( "shapes" );
        shapes.clear();
        
        for( XElement xshape : xshapes ){
            if( xshape.getName().equals( "shape" )){
                shapes.add( ShapeUtils.readXML( xshape ));
            }
        }
        
        for( PictureListener listener : listeners.toArray( new PictureListener[ listeners.size() ] ))
            listener.pictureChanged();
    }
    
    /**
     * Adds a shape to the list of shapes.
     * @param shape the new shape
     */
    public void add( Shape shape ){
        shapes.add( shape );
        for( PictureListener listener : listeners.toArray( new PictureListener[ listeners.size() ] ))
            listener.pictureChanged();
    }
    
    /**
     * Tells whether this picture has at least one {@link Shape}
     * or not.
     * @return <code>true</code> if there are no shapes in this picture
     */
    public boolean isEmpty(){
        return shapes.isEmpty();
    }
    
    /**
     * Deletes the newest {@link Shape} of this picture.
     */
    public void removeLast(){
        shapes.remove( shapes.size()-1 );
        for( PictureListener listener : listeners.toArray( new PictureListener[ listeners.size() ] ))
            listener.pictureChanged();
    }
    
    /**
     * Adds an observer to this picture.
     * @param listener the new observer
     */
    public void addListener( PictureListener listener ){
        listeners.add( listener );
    }
    
    /**
     * Removes an observer from this picture.
     * @param listener the listener to remove
     */
    public void removeListener( PictureListener listener ){
        listeners.remove( listener );
    }
    
    /**
     * Gets the name of this picture.
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Paints all shapes of this picture.
     * @param g the graphics context
     * @param stretch a factor each coordinate will be multiplied with
     */
    public void paint( Graphics g, double stretch ){
        for( Shape shape : shapes )
            shape.paint( g, stretch );
    }
}
