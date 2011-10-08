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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab4;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.GlobalMouseDispatcher;

/**
 * Tells whether the mouse is currently over a {@link TabComponent} or one of its children.
 * @author Benjamin Sigg
 */
public class HoverCondition extends TabComponentCondition{
	/** the last event that has been seen */
	private MouseEvent latestEvent;
	
	/** the controller in whose realm this condition is used */
	private DockController controller;
	
	/** the listener that was added to the {@link GlobalMouseDispatcher} */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new condition.
	 * @param component the component to observe
	 */
	public HoverCondition( TabComponent component ){
		super( component );
	}

	/**
	 * Sets the controller which is used to register global {@link MouseEvent}s.
	 * @param controller the controller to observe or <code>null</code> to remove all listeners
	 */
	public void setController( DockController controller ){
		if( this.controller != controller ){
			if( this.controller != null ){
				GlobalMouseDispatcher dispatcher = this.controller.getGlobalMouseDispatcher();
				dispatcher.removeMouseListener( listener );
				dispatcher.removeMouseMotionListener( listener );
			}
			this.controller = controller;
			if( this.controller != null ){
				GlobalMouseDispatcher dispatcher = this.controller.getGlobalMouseDispatcher();
				dispatcher.addMouseListener( listener );
				dispatcher.addMouseMotionListener( listener );
			}
			invalidate( null );
		}
	}
	
	@Override
	protected boolean checkCondition(){
		MouseEvent event = latestEvent;
		if( event == null ){
			return false;
		}
		Point location = event.getPoint();
		Component component = getComponent().getComponent();
		location = SwingUtilities.convertPoint( event.getComponent(), location, component );
		return component.contains( location );
	}
	
	private void invalidate( MouseEvent event ){
		latestEvent = event;
		invalidate();
	}
	
	private class Listener implements MouseListener, MouseMotionListener{
		public void mouseDragged( MouseEvent e ){
			// ignore	
		}

		public void mouseMoved( MouseEvent e ){
			invalidate( e );
		}

		public void mouseClicked( MouseEvent e ){
			invalidate( e );
		}

		public void mousePressed( MouseEvent e ){
			invalidate( e );
		}

		public void mouseReleased( MouseEvent e ){
			invalidate( e );
		}

		public void mouseEntered( MouseEvent e ){
			invalidate( e );
		}

		public void mouseExited( MouseEvent e ){
			invalidate( e );
		}		
	}
}
