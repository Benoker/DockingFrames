package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * An algorithm that allows to merge {@link ToolbarGroupDockStation}s together.
 * 
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationMerger extends AbstractToolbarMerger {
	@Override
	protected boolean validType( AbstractToolbarDockStation station ){
		return station instanceof ToolbarGroupDockStation;
	}
}
