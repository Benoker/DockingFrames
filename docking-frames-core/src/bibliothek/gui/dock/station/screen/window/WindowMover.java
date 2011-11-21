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
package bibliothek.gui.dock.station.screen.window;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.magnet.MagnetController;
import bibliothek.gui.dock.station.screen.magnet.MagnetizedOperation;

/**
 * the {@link WindowMover} can be used by any {@link ScreenDockWindow} showing a {@link DockElementRepresentative}. The
 * mover adds a listener to the element and moves the entire window if the element is dragged. The mover also
 * ensures that drag and drop still works.<br>
 * This class also supports {@link MagnetController magnetization}.
 * @author Benjamin Sigg
 */
public class WindowMover {
	/** the window which is to be moved */
	private ScreenDockWindow window;

	/** whether drag and drop operations are enabled */
	private boolean allowDragAndDrop;
	
	/**
	 * If {@link #allowDragAndDrop} and a target is found, then the window is set to its starting
	 * position. 
	 */
	private boolean resetOnDropable;
	
	/** the current title */
	private DockElementRepresentative element;
	
	/** a listener that is added to the current {@link #element} */
	private MouseInputAdapter listener = new MouseInputAdapter(){
		private Rectangle startBoundaries;
		private Point startPoint;
		private MagnetizedOperation magnet;
		private RemoteRelocator relocator;
		
		public void mousePressed( MouseEvent e ){
			if( !e.isConsumed() && !window.isFullscreen() ){
				if( allowDragAndDrop ){
					DockController controller = window.getStation().getController();
					if( controller != null ){
						relocator = controller.getRelocator().createRemote( window.getDockable() );
						Reaction reaction = relocator.init( startPoint.x, startPoint.y, 0, 0, e.getModifiersEx() );
						switch( reaction ){
							case BREAK:
							case BREAK_CONSUMED:
								relocator = null;
						}
					}
				}
				
				int buttons = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
				buttons &= e.getModifiersEx();
				
				if( Integer.bitCount( buttons ) == 1 && e.getButton() == MouseEvent.BUTTON1 ){
					e.consume();
					startBoundaries = window.getWindowBounds();
					startPoint = e.getPoint();
					convertPointToScreen( startPoint, e.getComponent() );
					
					magnet = window.getStation().getMagnetController().start( window );
				}
			}
		}
		
		public void mouseDragged( MouseEvent e ){
			if( !e.isConsumed() ){
				Point current = e.getPoint();
				convertPointToScreen( current, e.getComponent() );
				
				if( startPoint != null ){
					e.consume();
					
					int dx = current.x - startPoint.x;
					int dy = current.y - startPoint.y;
					
					Rectangle bounds = new Rectangle( startBoundaries.x + dx, startBoundaries.y + dy, startBoundaries.width, startBoundaries.height );
					bounds = magnet.attract( bounds );
					window.setWindowBounds( bounds, true );				
				}
				
				if( relocator != null ){
					Reaction reaction = relocator.drag( current.x, current.y, e.getModifiersEx() );
					switch( reaction ){
						case BREAK:
							relocator = null;
							break;
						case BREAK_CONSUMED:
							relocator = null;
							e.consume();
							break;
						case CONTINUE_CONSUMED:
							e.consume();
							break;
					}
				}
			}
		}
		
		public void mouseReleased( MouseEvent e ){
			if( !e.isConsumed() ){
				e.consume();
				
				Point current = e.getPoint();
				convertPointToScreen( current, e.getComponent() );
				
				if( relocator != null ){
					Reaction reaction = relocator.drop( current.x, current.y, e.getModifiersEx() );

					switch( reaction ){
						case BREAK:
							relocator = null;
							break;
						case BREAK_CONSUMED:
							relocator = null;
							e.consume();
							break;
						case CONTINUE_CONSUMED:
							e.consume();
							break;
					}
				}
				
				if( e.getButton() == MouseEvent.BUTTON1 ){
					if( magnet != null ){
						magnet.stop();
					}
					startPoint = null;
					startBoundaries = null;
				}
			}
		}
	};

	/**
	 * Creates a new mover
	 * @param window the window which is to be moved, must not be <code>null</code>
	 */
	public WindowMover( ScreenDockWindow window ){
		if( window == null ) {
			throw new IllegalArgumentException( "window must not be null" );
		}
		this.window = window;
	}

	/**
	 * Sets the element which is currently shown by the window.
	 * @param element the element that is shown, can be <code>null</code>
	 */
	public void setElement( DockElementRepresentative element ){
		if( this.element != null ){
			this.element.removeMouseInputListener( listener );
		}
		this.element = element;
		if( this.element != null ){
			this.element.addMouseInputListener( listener );
		}
	}
	
	/**
	 * Sets whether drag and drop operations are still allowed even if the window is moved around.
	 * @param allowDragAndDrop whether to allow drag and drop operations
	 */
	public void setAllowDragAndDrop( boolean allowDragAndDrop ){
		this.allowDragAndDrop = allowDragAndDrop;
	}
	
	/**
	 * Tells whether drag and drop operations are allowed even if the window is moved around.
	 * @return whether to allow drag and drop operations
	 */
	public boolean isAllowDragAndDrop(){
		return allowDragAndDrop;
	}
	
	/**
	 * 
	 * @param resetOnDropable
	 */
	public void setResetOnDropable( boolean resetOnDropable ){
		this.resetOnDropable = resetOnDropable;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isResetOnDropable(){
		return resetOnDropable;
	}
	
	/**
	 * Converst the point <code>point</code> on <code>component</code> to screen coordinates.
	 * @param point the point to convert
	 * @param component the current coordinate system of the point
	 */
	protected void convertPointToScreen( Point point, Component component ){
		SwingUtilities.convertPointToScreen( point, component );
	}
}
