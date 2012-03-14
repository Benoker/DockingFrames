/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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

import java.awt.Dimension;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * A {@link ScreenDropSizeStrategy} is used by a {@link ScreenDockStation} to decide some details
 * on how to drop a {@link Dockable}.
 * @author Benjamin Sigg
 */
public interface ScreenDropSizeStrategy {
	/**
	 * The {@link #getDropSize(ScreenDockStation, Dockable)} of a {@link Dockable} is always its
	 * current size when using this strategy. This is the default strategy.
	 */
	public static final ScreenDropSizeStrategy CURRENT_SIZE = new ScreenDropSizeStrategy(){
		public void install( ScreenDockStation station ){
			// ignore
		}

		public void uninstall( ScreenDockStation station ){
			// ignore
		}

		public Dimension getDropSize( ScreenDockStation station, Dockable dockable ){
			return dockable.getComponent().getSize();
		}
		
		public Dimension getAddSize( ScreenDockStation station, Dockable dockable ){
			return dockable.getComponent().getPreferredSize();
		}
	};
	
	/**
	 * This strategy always returns the preferred size of a {@link Dockable}.
	 */
	public static final ScreenDropSizeStrategy PREFERRED_SIZE = new ScreenDropSizeStrategy(){
		public void install( ScreenDockStation station ){
			// ignore
		}

		public void uninstall( ScreenDockStation station ){
			// ignore
		}

		public Dimension getDropSize( ScreenDockStation station, Dockable dockable ){
			return dockable.getComponent().getPreferredSize();
		}
		
		public Dimension getAddSize( ScreenDockStation station, Dockable dockable ){
			return dockable.getComponent().getPreferredSize();
		}
	};
	
	/**
	 * Informs this strategy that is is used by <code>station</code>.
	 * @param station the station using this strategy
	 */
	public void install( ScreenDockStation station );
	
	/**
	 * Informs this strategy that it is no longer used by <code>station</code>.
	 * @param station the station no longer using this strategy
	 */
	public void uninstall( ScreenDockStation station );
	
	/**
	 * Called when <code>dockable</code> is about to be dropped into <code>station</code> and the
	 * new size of <code>dockable</code> must be found.
	 * @param station the new parent of <code>dockable</code>
	 * @param dockable the item that is about to be dropped
	 * @return the new size of <code>dockable</code>. This size will be validated by the
	 * current {@link BoundaryRestriction}.
	 */
	public Dimension getDropSize( ScreenDockStation station, Dockable dockable );
	
	/**
	 * Called when <code>dockable</code> is added to <code>station</code>, but not by a drag and drop
	 * operation.
	 * @param station the new parent of <code>dockable</code>
	 * @param dockable the item that is about to be dropped
	 * @return the new size of <code>dockable</code>, usually this is the preferred size
	 */
	public Dimension getAddSize( ScreenDockStation station, Dockable dockable );
}
