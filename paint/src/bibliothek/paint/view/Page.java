package bibliothek.paint.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JPanel;

import bibliothek.paint.model.Picture;
import bibliothek.paint.model.PictureListener;
import bibliothek.paint.model.Shape;
import bibliothek.paint.model.ShapeFactory;

/**
 * A page paints a {@link Picture}.
 * @author Benjamin Sigg
 *
 */
public class Page extends JPanel implements PictureListener {
    private static final Dimension ORIGINAL_SIZE = new Dimension( 800, 600 ); 
	
	private Picture picture;
    
    private ShapeFactory factory;
    private Shape current;
    private Color color = Color.BLACK;
    
    private double zoom = 1.0;
    
    public Page(){
        addMouseListener( new MouseAdapter(){
            @Override
            public void mousePressed( MouseEvent e ) {
                if( current == null && factory != null ){
                    current = factory.create();
                    current.setColor( color );
                    current.setPointA( unstretch( e.getPoint() ));
                    current.setPointB( unstretch( e.getPoint() ));
                    repaint();
                }
            }
            
            @Override
            public void mouseReleased( MouseEvent e ) {
                if( current != null ){
                    picture.add( current );
                    current = null;
                }
            }
        });
        
        addMouseMotionListener( new MouseMotionAdapter(){
            @Override
            public void mouseDragged( MouseEvent e ) {
                if( current != null ){
                    current.setPointB( unstretch( e.getPoint() ));
                    repaint();
                }
            }
        });
        
        setZoom( 1.0 );
    }
    
    public void setColor( Color color ){
		this.color = color;
	}
    
    /**
     * Sets the zoom-factory. Each coordinate will be multiplied by this factor.
     * @param zoom a value greater than 0
     */
    public void setZoom( double zoom ){
		this.zoom = zoom;
		setPreferredSize( new Dimension( (int)(ORIGINAL_SIZE.width * zoom), (int)(ORIGINAL_SIZE.height * zoom) ) );
		revalidate();
		repaint();
	}
    
    /**
     * Gets the current zoom-factor.
     * @return the factor
     */
    public double getZoom(){
		return zoom;
	}
    
    private Point unstretch( Point point ){
    	return new Point( (int)(point.x / zoom), (int)(point.y / zoom) );
    }
    
    public ShapeFactory getFactory() {
        return factory;
    }
    
    public void setFactory( ShapeFactory factory ) {
        this.factory = factory;
    }

    public Picture getPicture() {
        return picture;
    }
    
    public void setPicture( Picture picture ) {
        if( this.picture != null )
            this.picture.removeListener( this );
        
        this.picture = picture;
        
        if( this.picture != null ){
            this.picture.addListener( this );
        }
        
        repaint();
    }
    
    public void pictureChanged() {
        repaint();
    }
    
    @Override
    protected void paintComponent( Graphics g ) {
        g.setColor( Color.WHITE );
        g.fillRect( 0, 0, getWidth(), getHeight() );
        if( picture != null )
            picture.paint( g, zoom );
        if( current != null )
            current.paint( g, zoom );
    }
}
