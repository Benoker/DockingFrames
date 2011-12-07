package bibliothek.gui.dock.station.toolbar.layout;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMap.Key;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;
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
		final PlaceholderStrategy strategy = getStrategy();
		if( strategy == null ) {
			return Collections.emptySet();
		}
		final Set<Path> placeholders = new HashSet<Path>();
		DockUtilities.visit( dockable, new DockVisitor(){
			@Override
			public void handleDockable( Dockable dockable ){
				Path placeholder = strategy.getPlaceholderFor( dockable );
				if( placeholder != null ) {
					placeholders.add( placeholder );
				}
			}

			@Override
			public void handleDockStation( DockStation station ){
				PlaceholderMap map = station.getPlaceholders();
				if( map != null ) {
					for( Key key : map.getPlaceholders() ) {
						for( Path placeholder : key.getPlaceholders() ) {
							placeholders.add( placeholder );
						}
					}
				}
			}
		} );
		return placeholders;
	}

	@Override
	protected void fill( Dockable dockable, ConvertedPlaceholderListItem item ){
		PlaceholderStrategy strategy = getStrategy();
		if( strategy != null ) {
			Path placeholder = strategy.getPlaceholderFor( dockable );
			if( placeholder != null ) {
				item.putString( "placeholder", placeholder.toString() );
				item.setPlaceholder( placeholder );
			}
		}
	}
}
