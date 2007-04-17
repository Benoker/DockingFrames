package bibliothek.gui.dock.util;

/**
 * A listener to an entry of {@link DockProperties}.
 * @author Benjamin Sigg
 *
 * @param <A> the type of observed value
 */
public interface DockPropertyListener<A> {
	/**
	 * Invoked if the observed value has been changed.
	 * @param properties the map in which the value is stored
	 * @param property the key of the value
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void propertyChanged( DockProperties properties, PropertyKey<A> property, A oldValue, A newValue );
}
