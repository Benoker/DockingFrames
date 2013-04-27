/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;

/**
 * This strategy is used by a {@link StackDockStation} to automatically change the selected
 * {@link Dockable} if the mouse hovers over a tab during a drag and drop operation.
 * @see StackDockStation#DND_AUTO_SELECT_STRATEGY
 * @author Benjamin Sigg
 */
public interface DndAutoSelectStrategy {
	/**
	 * The default implementation just focuses the {@link Dockable} under the mouse.
	 */
	public static final DndAutoSelectStrategy DEFAULT = new DndAutoSelectStrategy(){
		public void handleRequest( DndAutoSelectStrategyRequest request ){
			request.toFront();	
		}
	};
	
	/**
	 * Does not perform any actions.
	 */
	public static final DndAutoSelectStrategy IGNORE = new DndAutoSelectStrategy(){
		public void handleRequest( DndAutoSelectStrategyRequest request ){
			// ignore	
		}
	};

	/**
	 * Called whenever the framework detects a hovering mouse over a tab during a drag and drop operation.<br>
	 * Please do note:
	 * <ul>
	 * 	<li>The same request may be sent multiple times in fast succession</li>
	 *  <li>The framework does not generate any kind of event if the user releases the mouse</li>
	 * </ul>
	 * @param request information about the currently selected dockable
	 */
	public void handleRequest( DndAutoSelectStrategyRequest request );
}
