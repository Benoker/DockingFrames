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
package bibliothek.gui.dock.dockable;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * A {@link MovingImage} that truly uses an {@link Image} to paint its content.
 * @author Benjamin Sigg
 *
 */
public class TrueMovingImage extends JPanel implements MovingImage{
    /** the content of this component */
    private BufferedImage image;
    
    /** the transparency with which to paint the image */
    private float alpha = 1.0f;
    
    /** whether transparency is supported */
    private boolean transparent = true;
    
    /**
     * Sets the transparency, 0 means the image is invisible, 1 means the image is opaque.
     * @param alpha the new transparency
     */
    public void setAlpha( float alpha ){
    	if( alpha < 0 || alpha > 1 || Float.isNaN( alpha )){
    		throw new IllegalArgumentException( "alpha must be between 0 and 1" );
    	}
    	
		this.alpha = alpha;
	}
    
    /**
     * Gets the transparency with which the image is painted.
     * @return the transparency
     */
    public float getAlpha(){
		return alpha;
	}
    
    @Override
    protected void paintComponent( Graphics g ) {
    	if( image != null ){
        	if( alpha == 1.0f || !transparent ){
        		g.drawImage( image, 0, 0, this );
        	}
        	else{
        		Graphics2D g2 = (Graphics2D)g;
        		Composite old = g2.getComposite();
        		g2.setComposite( AlphaComposite.getInstance( AlphaComposite.DST_ATOP, alpha ) );
        		g.drawImage( image, 0, 0, this );
        		g2.setComposite( old );
        	}
        }
    }
    
    /**
     * Sets the image that this component will paint.
     * @param image the new image or <code>null</code>
     */
    public void setImage( BufferedImage image ) {
        this.image = image;
        if( image != null ){
            setPreferredSize( new Dimension( image.getWidth(), image.getHeight() ) );
        }
        repaint();
    }
    
    public Point getOffset( Point pressPoint ){
    	return null;
    }
    
    public void bind( boolean transparency ) {
    	this.transparent = transparency;
    	setOpaque( !transparency );
    }
    
    public void unbind() {
        // ignore
    }
    
    public Component getComponent() {
        return this;
    }
}
