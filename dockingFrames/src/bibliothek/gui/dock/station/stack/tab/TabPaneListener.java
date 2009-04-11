package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.Dockable;

/**
 * An observer added to a {@link TabPane}. This listener is informed when
 * elements are added and removed from the {@link TabPane}, and also
 * when the selection changes.
 * @author Benjamin Sigg
 */
public interface TabPaneListener {
	/**
	 * Called after <code>dockable</code> has been added to <code>pane</code>.
	 * @param pane the parent
	 * @param dockable the new child
	 */
	public void added( TabPane pane, Dockable dockable );
	
	/**
	 * Called after <code>dockable</code> has been removed from <code>pane</code>.
	 * @param pane the parent
	 * @param dockable the removed child
	 */
	public void removed( TabPane pane, Dockable dockable );
	
	/**
	 * Called when the selection on <code>pane</code> has changed.
	 * @param pane the {@link TabPane} whose selection changed
	 */
	public void selectionChanged( TabPane pane );
}
