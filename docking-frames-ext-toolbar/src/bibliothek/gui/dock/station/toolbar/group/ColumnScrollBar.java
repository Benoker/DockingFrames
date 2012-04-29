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
import java.awt.event.AdjustmentListener;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * A scrollbar used by the {@link ToolbarGroupDockStation} to slide around entire columns of buttons.
 * @author Benjamin Sigg
 */
public interface ColumnScrollBar {
	/**
	 * Tells this scrollbar how much space is required, and how much space is available.
	 * @param required the required pixels to show a column
	 * @param available the pixels actually available
	 */
	public void setValues( int required, int available );
	
	/**
	 * Gets the offset of the scrollbar. This value is between <code>0</code> and
	 * <code>available - required</code>.
	 * @return the offset
	 */
	public int getValue();
	
	/**
	 * Gets a {@link Component} which is the graphical representation of this scrollbar
	 * @return the graphical representation, not <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Sets the orientation of the scrollbar
	 * @param orientation the orientation, not <code>null</code>
	 */
	public void setOrientation( Orientation orientation );
	
	/**
	 * Adds a listener to this scrollbar, the listener is to be called whenever the {@link #getValue() value}
	 * changes.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addAdjustmentListener( AdjustmentListener listener );
	
	/**
	 * Removes <code>listener</code> from this scrollbar.
	 * @param listener the listener to remove
	 */
	public void removeAdjustmentListener( AdjustmentListener listener );
}
