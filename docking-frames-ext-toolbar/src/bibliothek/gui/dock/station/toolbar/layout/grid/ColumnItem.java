package bibliothek.gui.dock.station.toolbar.layout.grid;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * An class representing either a {@link Column} or a {@link DockElement}.
 * 
 * @author Benjamin Sigg
 */
public interface ColumnItem<D, S, P extends PlaceholderListItem<D>> {
	/**
	 * Converts this item into an item that represents a {@link DockStation}
	 * 
	 * @return the station, can be <code>null</code>
	 */
	public ColumnItem<D, S, P> asStation();

	/**
	 * Gets a placeholder for this item.
	 * 
	 * @return the placeholder or <code>null</code>
	 */
	public Path getPlaceholder();

	/**
	 * Gets the children of this station
	 * 
	 * @return the children
	 */
	public ColumnItem<D, S, P>[] getChildren();

	/**
	 * Gets all the placeholders of this item.
	 * 
	 * @return all the placeholders, can be <code>null</code>
	 */
	public PlaceholderMap getPlaceholders();

	/**
	 * Sets all the placeholders of this station.
	 * 
	 * @param map
	 *            the new placeholders
	 */
	public void setPlaceholders( PlaceholderMap map );
}