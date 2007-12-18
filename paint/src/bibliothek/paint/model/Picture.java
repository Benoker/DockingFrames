package bibliothek.paint.model;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

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
     */
    public void paint( Graphics g ){
        for( Shape shape : shapes )
            shape.paint( g );
    }
}
