package bibliothek.paint.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import bibliothek.paint.model.Shape;
import bibliothek.paint.model.ShapeFactory;

public class Page extends JPanel {
    private List<Shape> shapes = new ArrayList<Shape>();
    
    private ShapeFactory<? extends Shape> factory;
    private Shape current;
    
    public Page(){
        addMouseListener( new MouseAdapter(){
            @Override
            public void mousePressed( MouseEvent e ) {
                if( current == null && factory != null ){
                    current = factory.create();
                    current.setPointA( e.getPoint() );
                    current.setPointB( e.getPoint() );
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( current != null ){
                    shapes.add( current );
                    current = null;
                }
            }
        });
        
        addMouseMotionListener( new MouseMotionAdapter(){
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( current != null ){
                    current.setPointB( e.getPoint() );
                    repaint();
                }
            }
        });
    }
    
    public ShapeFactory<? extends Shape> getFactory() {
        return factory;
    }
    
    public void setFactory( ShapeFactory<? extends Shape> factory ) {
        this.factory = factory;
    }

    public List<Shape> getShapes() {
        return shapes;
    }
    
    public void setShapes( List<Shape> shapes ) {
        this.shapes = shapes;
        repaint();
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        g.setColor( Color.BLACK );

        for( Shape shape : shapes ){
            shape.paint( g );
        }
        
        if( current != null )
            current.paint( g );
    }
}
