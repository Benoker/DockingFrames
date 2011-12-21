package bibliothek.gui.dock.station.toolbar.title;

import bibliothek.gui.dock.action.DockActionSource;

/**
 * An observer that can be added to a {@link ColumnDockActionSource}.
 * @author Benjamin Sigg
 */
public interface ColumnDockActionSourceListener {
	/**
	 * Called if a new column was inserted into <code>source</code>
	 * @param source the source of the event
	 * @param item the new column
	 * @param index the location of the new column
	 */
	public void inserted( ColumnDockActionSource source, DockActionSource item, int index );
	
	/**
	 * Called if a column was removed from <code>source</code>
	 * @param source the source of the event
	 * @param item the column that was removed
	 * @param index the location of the removed column
	 */
	public void removed( ColumnDockActionSource source, DockActionSource item, int index );
	
	/**
	 * Called if the offset or length of at least one column changed
	 * @param source the source of the event
	 */
	public void reshaped( ColumnDockActionSource source );
}
