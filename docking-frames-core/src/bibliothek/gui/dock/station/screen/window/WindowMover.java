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
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.accept.DockAcceptance;
import bibliothek.gui.dock.control.DockRelocator;
import bibliothek.gui.dock.control.RemoteRelocator;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;
import bibliothek.gui.dock.control.relocator.DockRelocatorEvent;
import bibliothek.gui.dock.control.relocator.VetoableDockRelocatorAdapter;
import bibliothek.gui.dock.control.relocator.VetoableDockRelocatorListener;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.magnet.MagnetController;
import bibliothek.gui.dock.station.screen.magnet.MagnetizedOperation;

/**
 * The {@link WindowMover} can be used by any {@link ScreenDockWindow} showing a {@link DockElementRepresentative}. The
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
	private MouseInputAdapter listener = new Listener();
	
	/** the currently applied {@link DragAcceptance} */
	private DragAcceptance acceptance;
	
	/** prevents the current {@link DockRelocator} from moving around the {@link Dockable} on its own */
	private VetoListener veto;
	
	/** whether this {@link WindowMover} is performing an operation based on a {@link MouseEvent} right now */
	private boolean onMouseEvent = false;
	
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
		uninstallDragAcceptance();
		uninstallVeto();
		if( this.element != null ){
			this.element.addMouseInputListener( listener );
		}
	}
	
	private void installDragAcceptance(){
		uninstallDragAcceptance();
		acceptance = new DragAcceptance( window.getStation().getController() );
	}
	
	
	private void uninstallDragAcceptance(){
		if( acceptance != null ){
			acceptance.destroy();
			acceptance = null;
		}
	}
	
	private void installVeto(){
		uninstallVeto();
		veto = new VetoListener( window.getStation().getController() );
	}
	
	private void uninstallVeto(){
		if( veto != null ){
			veto.destroy();
			veto = null;
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
	 * Sets whether the window jumps back to its starting position if a droppable stop is found.
	 * @param resetOnDropable whether the window can jump back to its starting position
	 */
	public void setResetOnDropable( boolean resetOnDropable ){
		this.resetOnDropable = resetOnDropable;
	}
	
	/**
	 * Tells whether the window jumps back to its starting position if a droppable stop is found.
	 * @return whether the window can jump back
	 */
	public boolean isResetOnDropable(){
		return resetOnDropable;
	}
	
	/**
	 * Converts the point <code>point</code> on <code>component</code> to screen coordinates.
	 * @param point the point to convert
	 * @param component the current coordinate system of the point
	 */
	protected void convertPointToScreen( Point point, Component component ){
		SwingUtilities.convertPointToScreen( point, component );
	}
	
	/**
	 * A {@link DragAcceptance} is a {@link DockAcceptance} that prevents the {@link ScreenDockStation} from acting
	 * as potential parent of the moved {@link ScreenDockWindow}. 
	 * @author Benjamin Sigg
	 */
	private class DragAcceptance implements DockAcceptance{
		private DockController controller;
		
		/**
		 * Creates the new acceptance
		 * @param controller the controller in whose realm this {@link DockAcceptance} is required
		 */
		public DragAcceptance( DockController controller ){
			this.controller = controller;
			controller.addAcceptance( this );
		}
		
		public boolean accept( DockStation parent, Dockable child ){
			return parent != window.getStation();
		}
		
		public boolean accept( DockStation parent, Dockable child, Dockable next ){
			return true;
		}
		
		/**
		 * Uninstalls this {@link DockAcceptance}
		 */
		public void destroy(){
			controller.removeAcceptance( this );
		}
	}
	
	/**
	 * This {@link VetoableDockRelocatorListener} prevents the {@link DockRelocator} from moving around the
	 * {@link Dockable} on its own
	 * @author Benjamin Sigg
	 *
	 */
	private class VetoListener extends VetoableDockRelocatorAdapter{
		private DockRelocator relocator;
		
		/**
		 * Creates a new veto listener
		 * @param controller the controller in whose {@link DockRelocator} this listener should add itself.
		 */
		public VetoListener( DockController controller ){
			relocator = controller.getRelocator();
			relocator.addVetoableDockRelocatorListener( this );
		}
		
		/**
		 * Removes this listener from its {@link DockRelocator}.
		 */
		public void destroy(){
			relocator.removeVetoableDockRelocatorListener( this );
		}

		public void grabbing( DockRelocatorEvent event ){
			if( !onMouseEvent ){
				event.ignore();
			}
		}
		
		@Override
		public void dragged( DockRelocatorEvent event ){
			if( !onMouseEvent ){
				event.ignore();
			}
		}
		
		@Override
		public void canceled( DockRelocatorEvent event ){
			if( !onMouseEvent ){
				event.ignore();
			}
		}
	}
	
	/**
	 * This listener is added to the {@link DockElementRepresentative} which is monitored by this {@link WindowMover}
	 * @author Benjamin Sigg
	 */
	private class Listener extends MouseInputAdapter{
		private Rectangle startBoundaries;
		private Point startPoint;
		private MagnetizedOperation magnet;
		private RemoteRelocator relocator;
		
		public void mousePressed( MouseEvent e ){
			try{
				onMouseEvent = true;
				handleMousePressed( e );
			}
			finally{
				onMouseEvent = false;
			}
		}
		
		private void handleMousePressed( MouseEvent e ){
			if( !e.isConsumed() && !window.isFullscreen() ){
				int buttons = MouseEvent.BUTTON1_DOWN_MASK | MouseEvent.BUTTON2_DOWN_MASK | MouseEvent.BUTTON3_DOWN_MASK;
				buttons &= e.getModifiersEx();
				
				if( Integer.bitCount( buttons ) == 1 && e.getButton() == MouseEvent.BUTTON1 ){
					e.consume();
					startBoundaries = window.getWindowBounds();
					startPoint = e.getPoint();
					convertPointToScreen( startPoint, e.getComponent() );
					
					magnet = window.getStation().getMagnetController().start( window );
				}
				
				installVeto();
				
				if( allowDragAndDrop ){
					DockController controller = window.getStation().getController();
					if( controller != null ){
						relocator = controller.getRelocator().createRemote( window.getDockable() );
						relocator.setShowImageWindow( false );
						
						DockableDisplayer displayer = window.getDockableDisplayer();
						if( displayer != null ){
							relocator.setTitle( displayer.getTitle() );
						}
						
						Reaction reaction = relocator.init( startPoint.x, startPoint.y, 0, 0, e.getModifiersEx() );
						switch( reaction ){
							case BREAK:
							case BREAK_CONSUMED:
								relocator = null;
								break;
							case CONTINUE:
							case CONTINUE_CONSUMED:
								installDragAcceptance();
								break;
						}
					}
				}
			}
		}
		
		public void mouseDragged( MouseEvent e ){
			try{
				onMouseEvent = true;
				handleMouseDragged( e );
			}
			finally{
				onMouseEvent = false;
			}
		}
		
		private void handleMouseDragged( MouseEvent e ){
			if( !e.isConsumed() ){
 				Point current = e.getPoint();
				convertPointToScreen( current, e.getComponent() );
				
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
				

				if( startPoint != null ){
					e.consume();
					
					int dx = current.x - startPoint.x;
					int dy = current.y - startPoint.y;
					
					Rectangle bounds = null;
					
					if( relocator != null && resetOnDropable ){
						DockController controller = window.getStation().getController();
						if( controller != null && controller.getRelocator().hasTarget()){
							bounds = startBoundaries;
						}
					}
					if( bounds == null ){
						bounds = new Rectangle( startBoundaries.x + dx, startBoundaries.y + dy, startBoundaries.width, startBoundaries.height );
					}
					bounds = magnet.attract( bounds );
					window.setWindowBounds( bounds );				
				}
			}
		}
		
		public void mouseReleased( MouseEvent e ){
			try{
				onMouseEvent = true;
				handleMouseReleased( e );
			}
			finally{
				onMouseEvent = false;
			}
		}
		
		private void handleMouseReleased( MouseEvent e ){
			Point current = e.getPoint();
			convertPointToScreen( current, e.getComponent() );
			
			if( relocator != null ){
				Reaction reaction = relocator.drop( current.x, current.y, e.getModifiersEx() );

				switch( reaction ){
					case BREAK:
						relocator = null;
						uninstallDragAcceptance();
						uninstallVeto();
						break;
					case BREAK_CONSUMED:
						relocator = null;
						uninstallDragAcceptance();
						uninstallVeto();
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
				uninstallDragAcceptance();
				uninstallVeto();
			}
		}
	}
}
