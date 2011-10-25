package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.control.relocator.Merger;

/**
 * A {@link Merger} for merging two {@link ToolbarDockStation}s.
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
