package bibliothek.gui.dock.station.toolbar.layout;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;

/**
 * An implementation of {@link PlaceholderToolbarGrid} that uses {@link Dockable}s and {@link DockStation}s.
 * @author Benjamin Sigg
 * @param <P> the kind of object that represents a {@link Dockable}
 */
public class DockablePlaceholderToolbarGrid<P extends PlaceholderListItem<Dockable>> extends PlaceholderToolbarGrid<Dockable, DockStation, P> {

	@Override
	protected PlaceholderList<Dockable, DockStation, P> createColumn(){
		return new DockablePlaceholderList<P>();
	}

	@Override
	protected GridPlaceholderList<Dockable, DockStation, P> createGrid(){
		return new DockableGridPlaceholderList<P>();
	}
}
