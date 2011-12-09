package bibliothek.gui.dock.station.toolbar;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

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
		final ToolbarComplexDropInfo opreationToolbar = (ToolbarComplexDropInfo) operation;
		final ToolbarGroupDockStation station = (ToolbarGroupDockStation) parent;
		// WARNING: if I don't do a copy of dockables, problem occurs.
		// Perhaps due to concurrent access to the dockable (drop in
		// goal area ==> drag in origin area)?
		final int count = child.getDockableCount();
		final List<Dockable> insertDockables = new ArrayList<Dockable>();
		for (int i = 0; i < count; i++){
			insertDockables.add(child.getDockable(i));
		}
		int increment = 0;
		if ((opreationToolbar.getSideDockableBeneathMouse() == Position.SOUTH)
				|| (opreationToolbar.getSideDockableBeneathMouse() == Position.EAST)){
			increment++;
		}
		int dropIndex = station.indexOf(opreationToolbar
				.getDockableBeneathMouse()) + increment;
		for (int i = 0; i < count; i++){
			station.drop(insertDockables.get(i), dropIndex++);
		}
	}
}
