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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
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
    
    @Override
    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        if( image != null ){
            g.drawImage( image, 0, 0, this );
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
    
    public void bind() {
        // ignore
    }
    
    public void unbind() {
        // ignore
    }
    
    public Component getComponent() {
        return this;
    }
}
