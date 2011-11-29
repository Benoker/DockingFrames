package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarDockStation;

/**
 * An algorithm that allows to merge {@link ToolbarDockStation}s together.
 * 
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarDockStationMerger extends AbstractToolbarMerger {
	@Override
	protected boolean validType( AbstractToolbarDockStation station ){
		return station instanceof ToolbarDockStation;
	}
}
