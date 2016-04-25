package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.DockController;
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
	
	/**
	 * Called if the {@link TabPane#getInfoComponent()} was replaced.
	 * @param pane the source of this event
	 * @param oldInfo the old info component, may be <code>null</code>
	 * @param newInfo the new info component, may be <code>null</code>
	 */
	public void infoComponentChanged( TabPane pane, LonelyTabPaneComponent oldInfo, LonelyTabPaneComponent newInfo );
	
	/**
	 * Called if the {@link DockController} of <code>pane</code> has changed.
	 * @param controller the new controller, can be <code>null</code>
	 */
	public void controllerChanged( TabPane pane, DockController controller );
}
