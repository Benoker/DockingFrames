package bibliothek.paint.view;

import java.awt.Color;
import java.awt.Graphics;
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
    private Picture picture;
    
    private ShapeFactory factory;
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
                    picture.add( current );
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
            picture.paint( g );
        if( current != null )
            current.paint( g );
    }
}
