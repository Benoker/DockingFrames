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

import bibliothek.gui.Dockable;

/**
 * A listener that is added to a {@link ToolbarColumnModel}, is informed
 * if columns are added or removed from the model.
 * @author Benjamin Sigg
 * @param <D> the dockable class itself
 * @param <P> the wrapper class used to describe a {@link Dockable}
 */
public interface ToolbarColumnModelListener<D,P> {
	/**
	 * Called if a column was added to <code>model</code>.
	 * @param model the source of the event
	 * @param column the column that was added
	 * @param index the index of the new column
	 */
	public void inserted( ToolbarColumnModel<D,P> model, ToolbarColumn<D,P> column, int index );
	
	/**
	 * Called if a column was removed from <code>model</code>.
	 * @param model the source of the event
	 * @param column the column that was removed
	 * @param index the index of the removed column
	 */
	public void removed( ToolbarColumnModel<D,P> model, ToolbarColumn<D,P> column, int index );
}
