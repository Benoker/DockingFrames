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
 * A page is a panel that paints a {@link Picture}.
 * @author Benjamin Sigg
 *
 */
public class Page extends JPanel implements PictureListener {
    /** the size a page has when its zoomfactor is set to 1.0 */
    private static final Dimension ORIGINAL_SIZE = new Dimension( 800, 600 ); 
	
    /** the picture which is painted on this panel */
	private Picture picture;
    
	/** the factory which is used to create a new {@link Shape} when the user wants to insert a new one */
    private ShapeFactory factory;
    /** the <code>Shape</code> the user is currently inserting */
    private Shape current;
    /** the <code>Color</code> each new <code>Shape</code> will have */
    private Color color = Color.BLACK;
    
    /** the zoomfactor, each coordinate will be multiplied with this factor */
    private double zoom = 1.0;
    
    /**
     * Creates a new page.
     */
    public Page(){
        setFocusable( true );
        
        addMouseListener( new MouseAdapter(){
            @Override
            public void mousePressed( MouseEvent e ) {
                requestFocusInWindow();
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
    
    /**
     * Sets the <code>Color</code> which is used to paint any new {@link Shape}
     * the user wants to insert.
     * @param color the new color
     */
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
    
    /**
     * Divides each coordinate of <code>point</code> with the zoomfactor
     * of this <code>Page</code>.
     * @param point the point whose coordinates will be divided
     * @return a point with the divided coordinates
     */
    private Point unstretch( Point point ){
    	return new Point( (int)(point.x / zoom), (int)(point.y / zoom) );
    }
    
    /**
     * Gets the factory which is used to fetch new {@link Shape}s when the
     * user wants to insert a new <code>Shape</code>.
     * @return the factory
     */
    public ShapeFactory getFactory() {
        return factory;
    }
    
    /**
     * Sets the factory which is used to fetch new {@link Shape}s when the
     * user wants to insert new ones.
     * @param factory the factory, should not be <code>null</code>
     */
    public void setFactory( ShapeFactory factory ) {
        this.factory = factory;
    }

    /**
     * Gets the picture which is painted on this page.
     * @return the picture, can be <code>null</code>
     */
    public Picture getPicture() {
        return picture;
    }
    
    /**
     * Sets the <code>Picture</code> which is painted on this page.
     * @param picture the picture to paint, can be <code>null</code>
     */
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
