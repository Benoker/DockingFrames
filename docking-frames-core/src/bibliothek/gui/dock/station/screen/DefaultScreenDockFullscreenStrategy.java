/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.window.ScreenDockDialog;
import bibliothek.gui.dock.station.screen.window.ScreenDockFrame;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * This default implementation of a {@link ScreenDockFullscreenStrategy} just works with
 * the boundaries of a {@link ScreenDockWindow}. It assumes that either {@link ScreenDockDialog}
 * or {@link ScreenDockFrame} is used. Subclasses may extend this list, the algorithms of this
 * strategy should work with most implementations that are based on a {@link Window}.
 * @author Benjamin Sigg
 */
@Todo( compatibility=Compatibility.COMPATIBLE, priority=Priority.BUG, target=Version.VERSION_1_1_1,
	description="handle taskbar, check multi-screen (does not work properly)" )
public class DefaultScreenDockFullscreenStrategy implements ScreenDockFullscreenStrategy {
	public void install( ScreenDockStation station ) {
		// ignore
	}
	
	public void uninstall( ScreenDockStation station ) {
		// ignore
	}

	public boolean isFullscreen( ScreenDockWindow window ) {
		if( window instanceof ScreenDockDialog ){
			return isFullscreen( window, ((ScreenDockDialog)window).getDialog() );
		}
		if( window instanceof ScreenDockFrame ){
			return isFullscreen( window, ((ScreenDockFrame)window).getFrame() );
		}
		
		return false;
	}


	/**
	 * Tells whether the frame or dialog <code>window</code> is in fullscreen mode.
	 * @param wrapper the wrapper around <code>window</code>
	 * @param window the window to check
	 * @return the current mode
	 */
	public boolean isFullscreen( ScreenDockWindow wrapper, Window window ){
		if( wrapper.getNormalBounds() == null ){
			return false;
		}
		
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		Rectangle current = window.getBounds();
		current = new Rectangle( current.x - 1, current.y - 1, current.width + 2, current.height + 2 );
		
		for( GraphicsDevice device : environment.getScreenDevices() ){
			Rectangle fullscreen = getAvailableBounds( device.getDefaultConfiguration() );
			if( current.contains( fullscreen ) ){
				return true;
			}
		}
		
		return false;
	}
	
	public void setFullscreen( ScreenDockWindow window, boolean fullscreen ) {
            if (window instanceof ScreenDockDialog) {
                // aqui para maximizar
                System.out.println("ScreenDockDialog");
			setFullscreen( window, ((ScreenDockDialog)window).getDialog(), fullscreen );
		}
            if (window instanceof ScreenDockFrame) {
                System.out.println("ScreenDockFrame");
			setFullscreen( window, ((ScreenDockFrame)window).getFrame(), fullscreen );
		}
	}
	
	/**
	 * Sets the fullscreen mode of <code>window</code>.
	 * @param wrapper the wrapper around <code>window</code>
	 * @param window the window whose state is to be changed
	 * @param fullscreen the new state
	 */
	public void setFullscreen( ScreenDockWindow wrapper, Window window, boolean fullscreen ){
            System.out.println("settttFullscreen");
            if (isFullscreen(wrapper, window) != fullscreen) {
			if( fullscreen ){
                            Rectangle bounds = findBestFullscreenBounds(window);
                            System.out.println("best full screen bounds: " + bounds);
                            if (bounds != null) {
                                wrapper.setWindowBounds(getNormalBounds(window)); //  AQUIIIII !!!!!

                                wrapper.setNormalBounds(wrapper.getWindowBounds());
                                window.setBounds(bounds.x - 1, bounds.y - 1, bounds.width + 2, bounds.height + 2);
				}
			}
                        else {
                            System.out.println("normal bounds");
				Rectangle bounds = wrapper.getNormalBounds();
				if( bounds != null ){
					window.setBounds( bounds );
					wrapper.setNormalBounds( null );
				}
			}
		}
    }

    protected Rectangle getNormalBounds(final Window window) {
        System.out.println("getNormalBounds");
        Rectangle current = window.getBounds();
        GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

        // try easy hit
        for (GraphicsDevice device : devices) {
            Rectangle bounds = device.getDefaultConfiguration().getBounds();

            if (current.x >= bounds.x) {
                System.out.println("okay, esta no monitor: " + device + " bounds: " + bounds);
                return bounds;
            }

            if (bounds.contains(current)) {
                System.out.println("easy hittt! both in same display");
//                        return getAvailableBounds(device);
                return getAvailableBounds(device.getDefaultConfiguration());
            }
        }
        return null;
    }

	
	/**
	 * Tries to find the boundaries that match the fullscreen-criterium and that match the current
	 * location of <code>window</code>.
	 * @param window some window which is not yet in fullscreen mode
	 * @return boundaries for fullscreen mode or <code>null</code> if not boundaries could be found
	 */
    protected Rectangle findBestFullscreenBounds(Window window) {
        System.out.println("findBestFullscreenBounds!!!!");
		Rectangle current = window.getBounds();
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		
		// try easy hit
		for( GraphicsDevice device : devices ){
			Rectangle bounds = device.getDefaultConfiguration().getBounds();

                    if (current.x >= bounds.x) {
                        System.out.println("okay, esta no monitor: " + device + " bounds: " + bounds);
                        Rectangle b = getAvailableBounds(device.getDefaultConfiguration());

                        System.out.println("recebido: " + b);
//                b.width = (int) b.getWidth();
//                b.height = (int) b.getHeight();

                        System.out.println("retorna: " + b);
                        return b;
                    }

                    if (bounds.contains(current)) {
                        System.out.println("easy hittt! both in same display");
//                        return getAvailableBounds(device);
                        return getAvailableBounds(device.getDefaultConfiguration());
			}
		}
		
		// check center of window
		Point center = new Point( current.x + current.width/2, current.y + current.height/2 );
		GraphicsDevice best = null;
		int bestDist = 0;
		
		for( GraphicsDevice device : devices ){
			Rectangle bounds = device.getDefaultConfiguration().getBounds();

//                        bounds.width = device.getDisplayMode().getWidth();
//                        bounds.height = device.getDisplayMode().getHeight();

			int dist = dist( bounds.x, bounds.width, center.x ) + dist( bounds.y, bounds.height, center.y );
			if( best == null || dist < bestDist ){
				best = device;
				bestDist = dist;
			}
		}
		
            if (best != null) {
                System.out.println("using best device");
                return getAvailableBounds(best);
//                return getAvailableBounds(best.getDefaultConfiguration());
		}
		return null;
        }

        /**
         * Gets the boundaries of a {@link GraphicsDevice} that can actually be used.
         *
         * @param device Device to get bounds from.
         * @return the boundaries that can be used
         */
    protected Rectangle getAvailableBounds(GraphicsDevice device) {
        GraphicsConfiguration configuration = device.getDefaultConfiguration();
        Rectangle bounds = configuration.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(configuration);
        bounds.x += insets.left;
        bounds.y += insets.top;

        System.out.println("tira da w: " + (insets.left + insets.right));
        System.out.println("tira da h: " + (insets.top + insets.bottom));

//        bounds.width -= insets.left + insets.right;
//        bounds.height -= insets.top + insets.bottom;
        return bounds;
        }
	
	/**
	 * Gets the boundaries of a {@link GraphicsDevice} that can actually be used.
	 * @param configuration some device
	 * @return the boundaries that can be used
	 */
	protected Rectangle getAvailableBounds( GraphicsConfiguration configuration ){
		Rectangle bounds = configuration.getBounds();
		Insets insets = Toolkit.getDefaultToolkit().getScreenInsets( configuration );
		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= insets.left + insets.right;
		bounds.height -= insets.top + insets.bottom;
		return bounds;
	}
	
	private int dist( int x, int width, int pos ){
		if( pos < x ){
			return x - pos;
		}
		if( pos > x + width ){
			return pos - x - width;
		}
		return 0;
	}
}
