package bibliothek.gui.dock.station.toolbar.layout.grid;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;

/**
 * Represents one list of {@link Dockable}s.
 * 
 * @author Benjamin Sigg
 * 
 * @param <D>
 *            the kind of object that should be treated as {@link Dockable}
 * @param <S>
 *            the kind of object that should be treated as
 *            {@link DockStation}
 * @param <P>
 *            the type of item which represents a {@link Dockable}
 */

public interface Column<D, S, P extends PlaceholderListItem<D>> extends ColumnItem<D, S, P>, PlaceholderListItem<ColumnItem<D, S, P>> {
	/**
	 * Gets the list of dockables.
	 * 
	 * @return the list that is represented by this column
	 */
	public PlaceholderList<D, S, P> getList();
}