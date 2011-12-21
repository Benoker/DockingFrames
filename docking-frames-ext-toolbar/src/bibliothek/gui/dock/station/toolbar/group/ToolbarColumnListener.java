package bibliothek.gui.dock.station.toolbar.group;

import bibliothek.gui.Dockable;


/**
 * This observer can be added to a {@link ToolbarColumn} and receives events if the content
 * of the column changes.
 * @author Benjamin Sigg
 * @param <P> the wrapper class used to describe dockables
 */
public interface ToolbarColumnListener<P> {
	/**
	 * Called if an item was added to <code>column</code> at index <code>index</code>.
	 * @param column the source of the event
	 * @param item the item that was added
	 * @param dockable the dockable that was added
	 * @param index the index of the item that was added
	 */
	public void inserted( ToolbarColumn<P> column, P item, Dockable dockable, int index );
	
	/**
	 * Called if an item was removed from <code>column</code>.
	 * @param column the source of the event
	 * @param item the item that was removed
	 * @param dockable the dockable that was removed
	 * @param index the index of the item that was removed
	 */
	public void removed( ToolbarColumn<P> column, P item, Dockable dockable, int index );
}
