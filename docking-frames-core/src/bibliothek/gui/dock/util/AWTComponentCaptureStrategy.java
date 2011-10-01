/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.util;

import java.awt.AWTException;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * Used to capture an image of a {@link Component} which either is from AWT,
 * or has children from AWT. 
 * @author Benjamin Sigg
 */
public interface AWTComponentCaptureStrategy {
    /**
     * This strategy just takes a real screen capture of the area in which the
     * component is supposed to be.
     */
    public static final AWTComponentCaptureStrategy SCREEN_CAPTURE_STRATEGY = new AWTComponentCaptureStrategy(){
        public BufferedImage createCapture( DockController controller, Component component ){
            try {
                if( !component.isShowing() )
                    return null;

                GraphicsConfiguration configuration = component.getGraphicsConfiguration();
                if( configuration == null )
                    return null;

                GraphicsDevice device = configuration.getDevice();
                if( device == null )
                    return null;

                Robot deviceRobot = new Robot( device );

                Point location = new Point( 0, 0 );
                SwingUtilities.convertPointToScreen( location, component );

                return deviceRobot.createScreenCapture( new Rectangle( location, component.getSize() ) );
            }
            catch( AWTException e ) {
                e.printStackTrace();
                return null;
            }
            catch( SecurityException e ){
                // if in a secure environment...
                return null;
            }
        }
    };
    
    /**
     * This strategy calls {@link Component#paintAll(Graphics)} recursively
     * on all {@link Component}s.
     */
    public static final AWTComponentCaptureStrategy RECURSIVE_PAINT_STRATEGY = new AWTComponentCaptureStrategy(){
        public BufferedImage createCapture( DockController controller,
                Component component ) {
         
            BufferedImage image = new BufferedImage( component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics g = image.createGraphics();
            forcePaint( component, g );
            g.dispose();
            return image;

        }
        
        /**
         * Forces <code>component</code> and each of its children to be painted.
         * @param component the component to paint
         * @param g the graphics context to use
         */
        private void forcePaint( Component component, Graphics g ){
            component.paintAll( g );
            if( component instanceof Container ){
                Container container = (Container)component;
                for( int i = 0, n = container.getComponentCount(); i<n; i++ ){
                    Component next = container.getComponent( i );
                    int width = next.getWidth();
                    int height = next.getHeight();
                    if( width > 0 && height > 0 ){
                        Graphics sub = g.create( next.getX(), next.getY(), width, height );
                        forcePaint( next, sub );
                        sub.dispose();
                    }
                }
            }
        }
    };
    

    /**
     * This strategy calls {@link Component#paintAll(Graphics)} on the given component.
     */
    public static final AWTComponentCaptureStrategy PAINT_ALL_STRATEGY = new AWTComponentCaptureStrategy(){
        public BufferedImage createCapture( DockController controller,
                Component component ) {
         
            BufferedImage image = new BufferedImage( component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics g = image.createGraphics();
            component.paintAll( g );
            g.dispose();
            return image;

        }
    };
    
    /**
     * The {@link PropertyKey} for a {@link AWTComponentCaptureStrategy}.
     */
    public static final PropertyKey<AWTComponentCaptureStrategy> STRATEGY = 
        new PropertyKey<AWTComponentCaptureStrategy>( "dock.AWTComponentCaptureStrategy",
        		new ConstantPropertyFactory<AWTComponentCaptureStrategy>( PAINT_ALL_STRATEGY ), true );
    
    
    /**
     * Creates a new image that has the same size as <code>component</code> and
     * contains all the things painted on <code>component</code>.
     * @param controller the controller for which the image is needed
     * @param component the component to paint, its width and height must be
     * at least 1.
     * @return the new image or <code>null</code> if no image can be created
     */
    public BufferedImage createCapture( DockController controller, Component component );
}
