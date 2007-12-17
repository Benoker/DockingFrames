package bibliothek.paint.model;

import java.awt.Color;
import java.awt.Point;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Some methods useful to work with {@link Shape}s.
 * @author Benjamin Sigg
 *
 */
public class ShapeUtils {
    /** factories for different types of shapes */
    private static final Map<String, ShapeFactory<?>> FACTORIES;
    
    static{
        FACTORIES = new HashMap<String, ShapeFactory<?>>();
        FACTORIES.put( RectangleShape.class.getCanonicalName(), RectangleShape.FACTORY );
        FACTORIES.put( LineShape.class.getCanonicalName(), LineShape.FACTORY );
        FACTORIES.put( OvalShape.class.getCanonicalName(), OvalShape.FACTORY );
    }
    
    public static Collection<ShapeFactory<?>> getFactories(){
        return Collections.unmodifiableCollection( FACTORIES.values() );
    }
    
    /**
     * Writes all shapes in <code>shapes</code> to <code>out</code>.
     * @param shapes a list of shapes
     * @param out the stream to write into
     * @throws IOException if an I/O error occurs
     */
    @SuppressWarnings("unchecked")
    public static void write( List<Shape> shapes, DataOutputStream out ) throws IOException{
        out.writeInt( shapes.size() );
        for( Shape shape : shapes ){
            String key = shape.getClass().getCanonicalName();
            out.writeUTF( key );
            out.writeInt( shape.getColor().getRGB() );
            out.writeInt( shape.getPointA().x );
            out.writeInt( shape.getPointA().y );
            out.writeInt( shape.getPointB().x );
            out.writeInt( shape.getPointB().y );
        }
    }
    
    /**
     * Reads some {@link Shape}s from the stream <code>in</code>.
     * @param in the stream to read from
     * @return the list of {@link Shape}s
     * @throws IOException if an I/O error occurs
     */
    public static List<Shape> read( DataInputStream in ) throws IOException{
        List<Shape> shapes = new ArrayList<Shape>();
        for( int i = 0, n = in.readInt(); i<n; i++ ){
            String key = in.readUTF();
            Shape shape = FACTORIES.get( key ).create();
            shape.setColor( new Color( in.readInt() ) );
            shape.setPointA( new Point( in.readInt(), in.readInt() ) );
            shape.setPointB( new Point( in.readInt(), in.readInt() ) );
            shapes.add( shape );
        }
        return shapes;
    }
}
