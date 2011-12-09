package bibliothek.gui.dock.station.toolbar.layout;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A converter used by the {@link PlaceholderToolbarGrid} when reading a
 * {@link PlaceholderMap}.
 * 
 * @author Benjamin Sigg
 * @param <D>
 *            the type that represents a dockable
 * @param <P>
 *            the type that is actually stored in the grid
 */
public interface PlaceholderToolbarGridConverter<D, P extends PlaceholderListItem<D>> {
	/**
	 * Converts the {@link Dockable} <code>dockable</code> into an item that can
	 * be shown in the view. This method is also responsible for actually adding
	 * the item to the view.
	 * 
	 * @param dockable
	 *            the dockable to convert
	 * @param item
	 *            the item that is converted, may contain additional information
	 * @return the item that is shown in the view
	 */
	public P convert( D dockable, ConvertedPlaceholderListItem item );

	/**
	 * Called after the item <code>item</code> has been added to the grid.
	 * 
	 * @param item
	 *            the item that was added
	 */
	public void added( P item );
}
