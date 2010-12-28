package bibliothek.gui.dock.station.support;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderList} that works only with {@link PerspectiveElement}s.<br>
 * The {@link PlaceholderStrategy} of this list is automatically set during construction
 * and there is no need for clients to call {@link #bind()} or {@link #unbind()}. 
 * @author Benjamin Sigg
 *
 * @param <P> The kind of object that represents a {@link PerspectiveDockable}
 */
public class PerspectivePlaceholderList<P extends PlaceholderListItem<PerspectiveDockable>> extends PlaceholderList<PerspectiveDockable, PerspectiveStation, P>{

	/**
	 * Creates a new and empty list.
	 */
	public PerspectivePlaceholderList(){
		init();
	}
	
	/**
	 * Creates a new list using <code>map</code> to fill in the initial content.
	 * @param map the map to read
	 */
	public PerspectivePlaceholderList( PlaceholderMap map ){
		this( map, new PerspectivePlaceholderListItemConverter<P>() );
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
		init();
	}
	
	private void init(){
		setStrategy( new PlaceholderStrategy() {
			public void uninstall( DockStation station ){
				// ignore	
			}
			
			public void removeListener( PlaceholderStrategyListener listener ){
				// ignore
			}
			
			public boolean isValidPlaceholder( Path placeholder ){
				return true;
			}
			
			public void install( DockStation station ){
				// ignore	
			}
			
			public Path getPlaceholderFor( Dockable dockable ){
				// ignore, never called
				return null;
			}
			
			public void addListener( PlaceholderStrategyListener listener ){
				// ignore
			}
		});
		bind();
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
	
	/**
	 * Converts this list into a {@link PlaceholderMap}, any remaining {@link Dockable} or
	 * {@link DockStation} will be converted into its placeholder using the currently installed
	 * {@link PlaceholderStrategy}. 
	 * @return the new map, not <code>null</code>
	 */
	public PlaceholderMap toMap(){
		return toMap( new PerspectivePlaceholderListItemConverter<PlaceholderListItem<PerspectiveDockable>>() );
	}
	
	@Override
	protected Path getPlaceholder( PerspectiveDockable dockable ){
		return dockable.getPlaceholder();
	}

	@Override
	protected PlaceholderMap getPlaceholders( PerspectiveStation station ){
		return station.getPlaceholders();
	}

	@Override
	protected void setPlaceholders( PerspectiveStation station, PlaceholderMap map ){
		station.setPlaceholders( map );
	}

	@Override
	protected PerspectiveStation toStation( PerspectiveDockable dockable ){
		return dockable.asStation();
	}
	
	@Override
	protected PerspectiveDockable[] getChildren( PerspectiveStation station ){
		PerspectiveDockable[] children = new PerspectiveDockable[ station.getDockableCount() ];
		for( int i = 0; i < children.length; i++ ){
			children[i] = station.getDockable( i );
		}
		return children;
	}

	@Override
	protected String toString( PerspectiveDockable dockable ){
		return dockable.toString();
	}
}
