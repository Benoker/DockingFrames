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
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.DockSwingUtilities;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * A factory that makes a snapshot of the {@link Dockable} which will be represented
 * by its {@link MovingImage}.
 * @author Benjamin Sigg
 *
 */
public class ScreencaptureMovingImageFactory implements DockableMovingImageFactory {
    /** the maximal size of the images created by this factory */
    private Dimension max;
    
    /**
     * Creates a new factory.
     * @param max the maximal size of the images created by this factory
     */
    public ScreencaptureMovingImageFactory( Dimension max ){
        this.max = max;
    }
    
    public MovingImage create( DockController controller, DockTitle snatched ) {
        return create( controller, snatched.getDockable() );
    }
    
    /**
     * This method creates a new image that contains the contents of <code>dockable</code>.
     * @param dockable the element whose image should be taken
     * @return an image of <code>dockable</code> which is not larger than the
     * maximum {@link Dimension} that was given to this factory in the 
     * constructor.
     */
    public BufferedImage createImageFrom( Dockable dockable ){
        Component c = dockable.getComponent();

        Dimension size = new Dimension( 
                Math.max( 1, c.getWidth() ),
                Math.max( 1, c.getHeight() ));
        
        if( size.width < 10 || size.height < 10 ){
            Icon icon = dockable.getTitleIcon();
            if( icon == null || icon.getIconHeight() < 1 || icon.getIconWidth() < 1 )
                return null;
            
            BufferedImage image = new BufferedImage( icon.getIconWidth()+2, icon.getIconHeight()+2, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g = image.createGraphics();
            g.setColor( c.getBackground() );
            g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
            icon.paintIcon( c, g, 1, 1 );
            g.dispose();
            return image;
        }
        else{
            BufferedImage image = new BufferedImage(
                    size.width, size.height,
                    BufferedImage.TYPE_INT_ARGB );
            
            Graphics2D g = image.createGraphics();
            c.paint( g );
            g.dispose();
            
            double factor = Math.min( 
                    max.getWidth() / size.getWidth(), 
                    max.getHeight() / size.getHeight() );
            
            if( factor < 1.0 ){
                int w = (int)( factor * size.getWidth() );
                int h = (int)( factor * size.getHeight() );
                
                w = Math.max( w, 1 );
                h = Math.max( h, 1 );
                
                if( w != size.width || h != size.height ){
                    BufferedImage small = new BufferedImage( w, h, image.getType() );
                    g = small.createGraphics();
                    g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                    g.drawImage( image, 0, 0, w, h, 0, 0, size.width, size.height, null );
                    g.dispose();
                    image = small;
                }
            }
            
            return image;
        }
    }

    public MovingImage create( DockController controller, Dockable dockable ) {
        BufferedImage image = createImageFrom( dockable );
        
        TrueMovingImage moving = new TrueMovingImage();
        moving.setImage( image );
        return moving;
    }
}
