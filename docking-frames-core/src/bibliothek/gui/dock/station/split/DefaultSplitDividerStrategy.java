/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.split;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;

/**
 * The default implementation of {@link SplitDividerStrategy} 
 * @author Benjamin Sigg
 */
public class DefaultSplitDividerStrategy implements SplitDividerStrategy {
	private Map<SplitDockStation, Handler> handlers = new HashMap<SplitDockStation, Handler>();
	
	public void install( SplitDockStation station, Component container ){
		Handler handler = createHandlerFor( station );
		handler.install( container );
		handlers.put( station, handler );
	}
	
	public void uninstall( SplitDockStation station ){
		Handler handler = handlers.remove( station );
		if( handler != null ){
			handler.destroy();
		}
	}
	
	public void paint( SplitDockStation station, Graphics g ){
		Handler handler = handlers.get( station );
		if( handler != null ){
			handler.paint( g );
		}
	}
	
	/**
	 * Creates a new {@link Handler} for <code>station</code>.
	 * @param station the station which is to be monitored
	 * @return the new handler, not <code>null</code>
	 */
	protected Handler createHandlerFor( SplitDockStation station ){
		return new Handler( station );
	}
	
	/**
	 * A {@link Handler} is responsible for handling the needs of one {@link SplitDockStation}.
	 * @author Benjamin Sigg
	 */
	public static class Handler extends MouseAdapter{
		/** the node of the currently selected divider */
		private Node current;
	
		/** the current location of the divider */
		private double divider;
	
		/** the current state of the mouse: pressed or not pressed */
		private boolean pressed = false;
	
		/** the current bounds of the divider */
		private Rectangle bounds = new Rectangle();
		
		/** 
		 * A small modification of the position of the mouse. The modification
		 * is the distance to the center of the divider.
		 */
		private int deltaX;
	
		/** 
		 * A small modification of the position of the mouse. The modification
		 * is the distance to the center of the divider.
		 */
		private int deltaY;
	
		/** The station which is monitored by this strategy */
		private SplitDockStation station;
		
		/** The component to which this strategy added a {@link MouseListener} */
		private Component container;
		
		/**
		 * Creates a new strategy that will monitor <code>station</code>.
		 * @param station the station to monitor
		 */
		public Handler( SplitDockStation station ){
			this.station = station;
		}
		
		/**
		 * Gets the station which is monitored by this strategy
		 * @return the owner of this strategy
		 */
		public SplitDockStation getStation(){
			return station;
		}
		
		public void install( Component container ){
			if( this.container != null ){
				throw new IllegalStateException( "already initialized" );
			}
			this.container = container;
			container.addMouseListener( this );
			container.addMouseMotionListener( this );
		}
		
		/**
		 * Gets the {@link Component} with which this strategy was {@link #install(Component) initialized}.
		 * @return the argument from {@link #install(Component)}
		 */
		public Component getContainer(){
			return container;
		}
		
		/**
		 * Disposes all resources that are used by this handler.
		 */
		public void destroy(){
			if( container != null ){
				setCursor( null );
				current = null;
				container.removeMouseListener( this );
				container.removeMouseMotionListener( this );
				container = null;
			}
		}
		
		/**
		 * Changes the cursor of {@link #getContainer() the base component}. Subclasses may override this
		 * method to use custom cursors.
		 * @param cursor the cursor to set, may be <code>null</code>
		 */
		protected void setCursor( Cursor cursor ){
			container.setCursor( cursor );
		}
		
		/**
		 * Repaints parts of the {@link #getContainer() base component}.
		 * @param x x coordinate
		 * @param y y coordinate
		 * @param width the width of the are to repaint
		 * @param height the height of the are to repaint
		 */
		protected void repaint( int x, int y, int width, int height ){
			container.repaint( x, y, width, height );
		}
	
		/**
		 * Asynchronously checks the current position of the mouse and updates the cursor
		 * if necessary.
		 */
		protected void checkMousePositionAsync(){
			DockController controller = station.getController();
			if( controller != null && !controller.isRestrictedEnvironment() ){
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						if( container != null ){
							PointerInfo p = MouseInfo.getPointerInfo();
							Point e = p.getLocation();
							SwingUtilities.convertPointFromScreen(e, container);
							current = station.getRoot().getDividerNode(e.x, e.y);
			
							if( current == null ) {
								setCursor(null);
							}
						}
					}
				});
			}
		}
		
		@Override
		public void mousePressed( MouseEvent e ){
			if( station.isResizingEnabled() ) {
				if( !pressed ) {
					pressed = true;
					mouseMoved( e );
					if( current != null ) {
						divider = current.getDividerAt( e.getX() + deltaX, e.getY() + deltaY );
						repaint( bounds.x, bounds.y, bounds.width, bounds.height );
						bounds = current.getDividerBounds( divider, bounds );
						repaint( bounds.x, bounds.y, bounds.width, bounds.height );
					}
				}
			}
		}
	
		@Override
		public void mouseDragged( MouseEvent e ){
			if( station.isResizingEnabled() ) {
				if( pressed && current != null ) {
					divider = current.getDividerAt( e.getX() + deltaX, e.getY() + deltaY );
					divider = station.getCurrentSplitLayoutManager().validateDivider( station, divider, current );
					repaint( bounds.x, bounds.y, bounds.width, bounds.height );
					bounds = current.getDividerBounds( divider, bounds );
					repaint( bounds.x, bounds.y, bounds.width, bounds.height );
	
					if( station.isContinousDisplay() && current != null ) {
						current.setDivider( divider );
						station.updateBounds();
					}
				}
			}
		}
	
		@Override
		public void mouseReleased( MouseEvent e ){
			if( pressed ) {
				pressed = false;
				if( current != null ) {
					current.setDivider( divider );
					repaint( bounds.x, bounds.y, bounds.width, bounds.height );
					station.updateBounds();
				}
				setCursor( null );
				mouseMoved( e );
				checkMousePositionAsync();
			}
		}
	
		@Override
		public void mouseMoved( MouseEvent e ){
			if( station.isResizingEnabled() ) {
				current = station.getRoot().getDividerNode( e.getX(), e.getY() );
	
				if( current == null )
					setCursor( null );
				else if( current.getOrientation() == Orientation.HORIZONTAL )
					setCursor( Cursor.getPredefinedCursor( Cursor.W_RESIZE_CURSOR ) );
				else
					setCursor( Cursor.getPredefinedCursor( Cursor.N_RESIZE_CURSOR ) );
	
				if( current != null ) {
					bounds = current.getDividerBounds( current.getDivider(), bounds );
					deltaX = bounds.width / 2 + bounds.x - e.getX();
					deltaY = bounds.height / 2 + bounds.y - e.getY();
				}
			}
		}
	
		@Override
		public void mouseExited( MouseEvent e ){
			if( station.isResizingEnabled() ) {
				if( !pressed ) {
					current = null;
					setCursor( null );
				}
			}
		}
	
		/**
		 * Paints a line at the current location of the divider.
		 * @param g the Graphics used to paint
		 */
		public void paint( Graphics g ){
			if( station.isResizingEnabled() ) {
				if( current != null && pressed ) {
					station.getPaint().drawDivider( g, bounds );
				}
			}
		}
	}
}
