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
package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

/**
 * A {@link ToolbarGroupDividerStrategy} is responsible for painting dividers between
 * {@link Dockable}s. It can reserve some empty space between {@link Dockable}s to have enough
 * space for painting.
 * @author Benjamin Sigg
 */
public interface ToolbarGroupDividerStrategy {
	/**
	 * Represents the strategy that does not exist.
	 */
	public static final ToolbarGroupDividerStrategy NULL = new ToolbarGroupDividerStrategy(){
		@Override
		public int getLine( int column, int index ){
			return 0;
		}
		
		@Override
		public int getColumn( int index ){
			return 0;
		}
		
		@Override
		public void paint( Component parent, Graphics g, ToolbarGridLayoutManager<StationChildHandle> layoutManager ){
			// nothing
		}
	};
	
	/**
	 * Gets the empty space left of column <code>index</code>. If there are <code>n</code>
	 * columns, then <code>index=n</code> will return the empty space on the right side of the
	 * last column.
	 * @param index the index of a column
	 * @return the empty space left of it
	 */
	public int getColumn( int index );
	
	/**
	 * Gets the size of the gap top of the cell <code>index</code> of <code>column</code>. If there
	 * are <code>n</code> cells, then <code>index=n</code> will return the size of the bottom most gap.
	 * @param column the column in which to search
	 * @param index the index of the cell
	 * @return the size of the gap
	 */
	public int getLine( int column, int index );

	/**
	 * Allows this strategy to paint on the container that shows the {@link Dockable}s.
	 * @param parent the parent container
	 * @param g the graphics context to use
	 * @param layoutManager detailed information about the children to paint
	 */
	public void paint( Component parent, Graphics g, ToolbarGridLayoutManager<StationChildHandle> layoutManager );
}
