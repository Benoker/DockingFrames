/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar.title;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * A {@link ColumnDockActionSource} is a list of {@link DockActionSource}s, all with some offset. The
 * list and the offsets may change at any time.
 * @author Benjamin Sigg
 */
public interface ColumnDockActionSource {
	/**
	 * Adds the observer <code>listener</code> to this source.
	 * @param listener the new observer
	 */
	public void addListener( ColumnDockActionSourceListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this source.
	 * @param listener the observer to remove
	 */
	public void removeListener( ColumnDockActionSourceListener listener );
	
	/**
	 * Gets the total number of {@link DockActionSource}s that are currently available.
	 * @return the total number of sources
	 */
	public int getSourceCount();
	
	/**
	 * Gets the <code>index</code>'th source of this {@link ColumnDockActionSource}.
	 * @param index the index of the source
	 * @return the {@link DockActionSource}, must not be <code>null</code> but may be empty
	 */
	public DockActionSource getSource( int index );
	
	/**
	 * Gets the orientation of this source. The orientation tells in which direction
	 * {@link #getSourceOffset(int)} points: either along the x, or along the y axis.
	 * @return the orientation, must not be <code>null</code>
	 */
	public Orientation getOrientation();
	
	/**
	 * Gets the offset of the <code>index</code>'th source in pixel. The offset is relative to
	 * the {@link Dockable} to which this {@link ColumnDockActionSource} belongs.
	 * @param index the index of the source
	 * @return the offset of the source in pixels
	 */
	public int getSourceOffset( int index );
	
	/**
	 * Gets the available length in pixel for the <code>index</code>'th source.
	 * @param index the index of the source
	 * @return the maximum amount of space in pixel
	 */
	public int getSourceLength( int index );
}
