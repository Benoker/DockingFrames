package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.util.FrameworkOnly;

/**
 * Represents one column of a {@link ToolbarGroupDockStation}. This interface
 * is not intended for subclassing.
 * @author Benjamin Sigg
 * @param <P> the kind of object used to describe a {@link Dockable}
 */
@FrameworkOnly
public interface ToolbarColumn<P> {
	/**
	 * Tells how many {@link Dockable}s are shown in this column.
	 * @return the total number of {@link Dockable}s, at least <code>0</code>.
	 */
	public int getDockableCount();
	
	/**
	 * Gets the <code>index</code>'th {@link Dockable} of this column.
	 * @param index the index of the {@link Dockable}
	 * @return the element at <code>index</code>, never <code>null</code>
	 * @throws IllegalArgumentException if <code>index</code> is out of bounds
	 */
	public Dockable getDockable( int index );
	
	/**
	 * Gets a wrapper item that represents the {@link Dockable} at <code>index</code>.
	 * @param index the index of the item
	 * @return the element at <code>index</code>, never <code>null</code>
	 */
	public P getItem( int index );
	
	/**
	 * Gets the location of this column in its parent {@link ToolbarColumnModel}.
	 * @return the location of this column
	 */
	public int getColumnIndex();
	
	/**
	 * Adds the new observer <code>listener</code> to this column.
	 * @param listener the new observer
	 */
	public void addListener( ToolbarColumnListener<P> listener );
	
	/**
	 * Removes the observer <code>listener</code> from this column.
	 * @param listener the listener to remove
	 */
	public void removeListener( ToolbarColumnListener<P> listener );
}
