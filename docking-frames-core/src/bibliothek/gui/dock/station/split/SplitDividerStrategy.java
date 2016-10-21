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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import bibliothek.gui.dock.SplitDockStation;

/**
 * The {@link SplitDividerStrategy} is responsible for resizing the children of a {@link SplitDockStation}. How exactly
 * a {@link SplitDividerStrategy} accomplishes that goal is not defined, but usually that involves:
 * <ul><li>Adding a {@link MouseListener} and a {@link MouseMotionListener} to the {@link Component} that is given during the
 * {@link #install(SplitDockStation, Component) installation}.</li>
 * <li>If the user presses the mouse the strategy will use {@link SplitDockStation#getRoot()} and {@link SplitNode#getDividerNode(int, int)} to find the {@link Node} that
 * is below the mouse.</li>
 * <li>By calling {@link Node#getDividerAt(int, int)} and {@link Node#setDivider(double)} the strategy can resize
 * the children.</li>
 * <li>Through {@link SplitDockStation#getCurrentSplitLayoutManager()} and {@link SplitLayoutManager#validateDivider(SplitDockStation, double, Node)} a strategy
 * can make sure that a valid value for the divider property is chosen.</li>
 * </ul>
 * A strategy may offer additional services like changing the {@link Cursor}, or a strategy
 * may not do anything at all.<br>
 * Implementations should (but are not enforced to) respect some properties:
 * <ul> 
 * 	<li>{@link SplitDockStation#isResizingEnabled()}: whether the user is allowed to resize the children.</li>
 * 	<li>{@link SplitDockStation#isContinousDisplay()}: whether resizing should happen immediately.</li>
 * </ul>
 * <br>
 * Clients usually do not need to implement this interface, and the framework offers only one default
 * implementation. The interface will however remain, ready for clients with unforseen needs. 
 * @author Benjamin Sigg
 */
public interface SplitDividerStrategy {
	/**
	 * Informs this strategy that <code>station</code> is going to use it and that <code>container</code> must
	 * be monitored in order to receive {@link MouseEvent}s.
	 * @param station the station whose children are resized by this strategy
	 * @param container the component to monitor
	 */
	public void install( SplitDockStation station, Component container );

	/**
	 * Informs this strategy that it will no longer be used by <code>station</code>.
	 * @param station the station that is no longer using <code>this</code>
	 */
	public void uninstall( SplitDockStation station );
	
	/**
	 * Allows this strategy to paint onto the {@link SplitDockStation}.
	 * @param station the station which is painted
	 * @param g the graphics context to use
	 */
	public void paint( SplitDockStation station, Graphics g );
}
