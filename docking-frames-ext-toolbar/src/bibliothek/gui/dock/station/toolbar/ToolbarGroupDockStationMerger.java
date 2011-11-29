package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.relocator.Merger;

/**
 * A {@link Merger} for merging two {@link ToolbarGroupDockStation}s.
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
