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
package bibliothek.gui.dock.control;

import java.awt.Component;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;

/**
 * The {@link GlobalMouseDispatcher} is used to keep track of the location of mouse on the screen. In a 
 * normal desktop application this is equivalent of installing an {@link AWTEventListener}. In a secure
 * environment the {@link GlobalMouseDispatcher} is restricted to register events that are forwarded by {@link Component}s
 * to it. Any {@link DockStation} forwards its events to this class.
 * @author Benjamin Sigg
 */
public interface GlobalMouseDispatcher {
	/**
	 * Called by various {@link Component}s when they register a {@link MouseEvent}. Calls to this method are only
	 * necessary when in a {@link DockController#isRestrictedEnvironment() restricted environment}. The  
	 * {@link GlobalMouseDispatcher} may filter the events, but usually the event is forwarded to all
	 * registered {@link MouseListener}s. 
	 * @param event the event to forward
	 */
	public void dispatch( MouseEvent event );
	
	/**
	 * Adds the observer <code>listener</code> to this dispatcher.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addMouseListener( MouseListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this dispatcher.
	 * @param listener the listener to remove
	 */
	public void removeMouseListener( MouseListener listener );
	
	/**
	 * Adds the observer <code>listener</code> to this dispatcher.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addMouseMotionListener( MouseMotionListener listener );

	/**
	 * Removes the observer <code>listener</code> from this dispatcher.
	 * @param listener the listener to remove
	 */
	public void removeMouseMotionListener( MouseMotionListener listener );

	/**
	 * Adds the observer <code>listener</code> to this dispatcher.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addMouseWheelListener( MouseWheelListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this dispatcher.
	 * @param listener the listener to remove
	 */
	public void removeMouseWheelListener( MouseWheelListener listener );
	
	/**
	 * Releases any resources this dispatcher holds.
	 */
	public void kill();
}
