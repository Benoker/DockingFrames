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

package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm allowing to merge two {@link AbstractToolbarDockStation}s
 * together.
 * 
 * @author Benjamin Sigg
 * @author Herve Guillaume
 */
public abstract class AbstractToolbarMerger implements Merger{
	/**
	 * Checks whether the type of <code>station</code> is valid for this merger.
	 * 
	 * @param station
	 *            can either be the parent of the child that is merged
	 * @return <code>true</code> if the type is accepted
	 */
	protected abstract boolean validType( AbstractToolbarDockStation station );

	@Override
	public boolean canMerge( StationDropOperation operation,
			DockStation parent, DockStation child ){
		return ((operation == null) || !operation.isMove())
				&& (parent instanceof AbstractToolbarDockStation)
				&& validType((AbstractToolbarDockStation) parent)
				&& (child instanceof AbstractToolbarDockStation)
				&& validType((AbstractToolbarDockStation) child);
	}

}
