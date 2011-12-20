package bibliothek.gui.dock.station.toolbar.group;

/**
 * A listener that is added to a {@link ToolbarColumnModel}, is informed
 * if columns are added or removed from the model.
 * @author Benjamin Sigg
 */
public interface ToolbarColumnModelListener {
	/**
	 * Called if a column was added to <code>model</code>.
	 * @param model the source of the event
	 * @param column the column that was added
	 * @param index the index of the new column
	 */
	public void inserted( ToolbarColumnModel model, ToolbarColumn column, int index );
	
	/**
	 * Called if a column was removed from <code>model</code>.
	 * @param model the source of the event
	 * @param column the column that was removed
	 * @param index the index of the removed column
	 */
	public void removed( ToolbarColumnModel model, ToolbarColumn column, int index );
}
