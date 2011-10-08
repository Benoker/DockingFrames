package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm that allows to merge {@link ToolbarGroupDockStation}s together.
 * 
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationMerger extends ToolbarDockStationMerger{
	@Override
	public boolean canMerge( StationDropOperation operation,
			DockStation parent, DockStation child ){
		return (operation == null || !operation.isMove())
				&& parent instanceof ToolbarGroupDockStation
				&& child instanceof ToolbarGroupDockStation;
	}

}
