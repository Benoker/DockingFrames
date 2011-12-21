package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.toolbar.layout.DockablePlaceholderToolbarGrid;
import bibliothek.util.FrameworkOnly;

/**
 * The {@link ToolbarColumnModel} provides a clearly defined way to access and monitor the columns of a 
 * {@link ToolbarGroupDockStation}. The model acts as facade for the real datastructures inside
 * {@link ToolbarGroupDockStation}, which are usually not accessible.<br>
 * The model does not offer any information that could not be retrieved through the methods of {@link DockablePlaceholderToolbarGrid},
 * but it offers an API to register observers and be notified about changes within the columns.<br>
 * Clients should not implement this interface.
 * @author Benjamin Sigg
 * @param <P> the wrapper used to describe a {@link Dockable}
 */
@FrameworkOnly
public interface ToolbarColumnModel<P> {
	/**
	 * Gets the total number of columns that are currently available.
	 * @return the total number of columns
	 */
	public int getColumnCount();
	
	/**
	 * Gets the <code>index</code>'th column of this model.
	 * @param index the index of the column
	 * @return the column, not <code>null</code>
	 * @throws IllegalArgumentException if <code>index</code> is not within the boundaries
	 */
	public ToolbarColumn<P> getColumn( int index );
	
	/**
	 * Searches the column which contains <code>dockable</code>.
	 * @param dockable the item to search
	 * @return the column containing <code>dockable</code> or <code>null</code> if not found
	 */
	public ToolbarColumn<P> getColumn( Dockable dockable );
	
	/**
	 * Adds the observer <code>listener</code> to this model.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addListener( ToolbarColumnModelListener<P> listener );
	
	/**
	 * Removes the observer <code>listener</code> from this model.
	 * @param listener the observer to remove
	 */
	public void removeListener( ToolbarColumnModelListener<P> listener );
}
