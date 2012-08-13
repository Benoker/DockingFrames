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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDropInfo;

/**
 * A {@link Merger} for merging two {@link ToolbarGroupDockStation}s.
 * 
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationMerger extends AbstractToolbarMerger{
	@Override
	protected boolean validType( AbstractToolbarDockStation station ){
		return station instanceof ToolbarGroupDockStation;
	}

	@Override
	public void merge( StationDropOperation operation, DockStation parent,
			DockStation child ){
		final ToolbarGroupDropInfo groupInfo = (ToolbarGroupDropInfo) operation;
		final ToolbarGroupDockStation station = (ToolbarGroupDockStation) parent;
		// WARNING: if I don't do a copy of dockables, problem occurs.
		// Perhaps due to concurrent access to the dockable (drop in
		// goal area ==> drag in origin area)?
		final int count = child.getDockableCount();
		final List<Dockable> insertDockables = new ArrayList<Dockable>();
		for (int i = 0; i < count; i++){
			insertDockables.add(child.getDockable(i));
		}

		int dropColumnIndex = groupInfo.getColumn();
		int dropLineIndex = groupInfo.getLine();
		if (dropLineIndex == -1){
			// a new column has to be created
			for (int i = 0; i < count; i++){
				station.drop(insertDockables.get(i), dropColumnIndex);
			}
		} else{
			for (int i = 0; i < count; i++){
				station.drop(insertDockables.get(i), dropColumnIndex,
						dropLineIndex);
			}
		}

	}
}
