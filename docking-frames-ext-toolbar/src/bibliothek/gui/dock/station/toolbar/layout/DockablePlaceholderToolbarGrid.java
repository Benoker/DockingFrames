package bibliothek.gui.dock.station.toolbar.layout;

import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.util.Path;

/**
 * An implementation of {@link PlaceholderToolbarGrid} that uses {@link Dockable}s and {@link DockStation}s.
 * @author Benjamin Sigg
 * @param <P> the kind of object that represents a {@link Dockable}
 */
public class DockablePlaceholderToolbarGrid<P extends PlaceholderListItem<Dockable>> extends PlaceholderToolbarGrid<Dockable, DockStation, P> {
	/**
	 * Creates and initializes a new grid
	 */
	public DockablePlaceholderToolbarGrid(){
		init();
	}
	
	@Override
	protected PlaceholderList<Dockable, DockStation, P> createColumn(){
		return new DockablePlaceholderList<P>();
	}

	@Override
	protected GridPlaceholderList<Dockable, DockStation, P> createGrid(){
		return new DockableGridPlaceholderList<P>();
	}

	@Override
	protected Set<Path> getPlaceholders( Dockable dockable ){
		
	}

	@Override
	protected void fill( Dockable dockable, ConvertedPlaceholderListItem item ){
	fdsa	
	}
}
