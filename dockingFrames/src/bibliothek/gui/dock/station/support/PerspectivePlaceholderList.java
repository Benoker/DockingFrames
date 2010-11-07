package bibliothek.gui.dock.station.support;

import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderList} that works only with {@link PerspectiveElement}s.
 * @author Benjamin Sigg
 *
 * @param <P> The kind of object that represents a {@link PerspectiveDockable}
 */
public class PerspectivePlaceholderList<P extends PlaceholderListItem<PerspectiveDockable>> extends PlaceholderList<PerspectiveDockable, PerspectiveStation, P>{

	/**
	 * Creates a new and empty list.
	 */
	public PerspectivePlaceholderList(){
		// nothing
	}
	
	/**
	 * Creates a new list reading all the data that is stored in <code>map</code>. This
	 * constructor stores all placeholders that are described in <code>map</code>, obsolete
	 * placeholders may be deleted as soon as a {@link PlaceholderStrategy} is set.
	 * @param map the map to read, not <code>null</code>
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @throws IllegalArgumentException if <code>map</code> was not written by a {@link PlaceholderList}
	 */
	public PerspectivePlaceholderList( PlaceholderMap map, PlaceholderListItemConverter<PerspectiveDockable,P> converter ){
		super( map, converter );
	}

	/**
	 * Simulates a call to {@link #read(PlaceholderMap, PlaceholderListItemConverter)} and makes all calls to <code>converter</code>
	 * that would be made in a real read as well. 
	 * @param map the map to read
	 * @param converter used to convert items back to dockables, not <code>null</code>
	 * @param <P> the kind of data <code>converter</code> handles
	 */
	public static <P extends PlaceholderListItem<PerspectiveDockable>> void simulatedRead( PlaceholderMap map, PlaceholderListItemConverter<PerspectiveDockable,P> converter ){
		PerspectivePlaceholderList<P> list = new PerspectivePlaceholderList<P>();
		list.read( map, converter, true );
	}
	
	@Override
	protected Path getPlaceholder( PerspectiveDockable dockable ){
		return null;
	}

	@Override
	protected PlaceholderMap getPlaceholders( PerspectiveStation station ){
		return null;
	}

	@Override
	protected void setPlaceholders( PerspectiveStation station, PlaceholderMap map ){
		
	}

	@Override
	protected PerspectiveStation toStation( PerspectiveDockable dockable ){
		return dockable.asStation();
	}

	@Override
	protected String toString( PerspectiveDockable dockable ){
		return dockable.toString();
	}
}
