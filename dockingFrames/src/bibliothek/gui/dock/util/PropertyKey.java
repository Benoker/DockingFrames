package bibliothek.gui.dock.util;

import javax.swing.Icon;

import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;

/**
 * The key for an entry of {@link DockProperties}.
 * @author Benjamin Sigg
 *
 * @param <A> the type of entry
 */
public class PropertyKey<A> {
	/**
	 * The Icon used for a {@link Dockable} if it has no icon.
	 */
	public static final PropertyKey<Icon> DOCKABLE_ICON = new PropertyKey<Icon>( "javax.swing.Icon_dockable_icon" );
	/**
	 * The Icon used for a {@link DockStation} if it has no icon.
	 */
	public static final PropertyKey<Icon> DOCK_STATION_ICON = new PropertyKey<Icon>( "javax.swing.Icon_dock_station_icon" );
	
	/**
	 * The title of a {@link Dockable} if it has no title.
	 */
	public static final PropertyKey<String> DOCKABLE_TITLE = new PropertyKey<String>( "java.lang.String_dockable_title" );
	/**
	 * The title of a {@link DockStation} if it has no title.
	 */
	public static final PropertyKey<String> DOCK_STATION_TITLE = new PropertyKey<String>( "java.lang.String_dock_station_title" );
	
	
	/** a unique identifier */
	private String id;

	/**
	 * Creates a new key.
	 * @param id a unique identifier, should contain the name of the
	 * type of property, represented by this key.
	 */
	public PropertyKey( String id ){
		if( id == null )
			throw new IllegalArgumentException( "id must not be null" );
		
		this.id = id;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PropertyKey other = (PropertyKey) obj;
		return other.id.equals( id );
	}
}
