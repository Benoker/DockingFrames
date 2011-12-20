package bibliothek.gui.dock.station.toolbar.layout;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;

/**
 * Represents a list of lists of {@link Dockable}s and placeholders.<br>
 * Note: this class does not validate its content in any way, it is the clients
 * responsibility to keep the list clean. A good wrapper is
 * {@link PlaceholderToolbarGrid}, which adds several convenient methods and
 * ensures that the list of lists always is cleaned up correctly.
 * 
 * @author Benjamin Sigg
 * @param <P>
 *            the kind of object that represents a {@link Dockable}
 */
public class DockableGridPlaceholderList<P extends PlaceholderListItem<Dockable>> extends GridPlaceholderList<Dockable, DockStation, P> {
	@Override
	protected DockStation itemToStation( Dockable dockable ){
		return dockable.asDockStation();
	}

	@Override
	protected Dockable[] getItemChildren( DockStation station ){
		final Dockable[] result = new Dockable[station.getDockableCount()];
		for( int i = 0; i < result.length; i++ ) {
			result[i] = station.getDockable( i );
		}
		return result;
	}

	@Override
	protected Path getItemPlaceholder( Dockable dockable ){
		final PlaceholderStrategy strategy = getStrategy();
		if( strategy == null ) {
			return null;
		}
		return strategy.getPlaceholderFor( dockable );
	}

	@Override
	protected PlaceholderMap getItemPlaceholders( DockStation station ){
		return station.getPlaceholders();
	}

	@Override
	protected void setItemPlaceholders( DockStation station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}
}
