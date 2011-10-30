package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;

/**
 * A listener that is added to an {@link ExpandableToolbarItemStrategy}.
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemStrategyListener {
	/**
	 * Called if <code>item</code> was expanded.
	 * @param item the item whose state changed
	 */
	public void expanded( Dockable item );
	
	/**
	 * Called if <code>item</code> was made small.
	 * @param item the item show state changed
	 */
	public void shrunk( Dockable item );
	
	/**
	 * Called if the {@link ExpandableToolbarItemStrategy#isExpandable(Dockable)} property
	 * changed for <code>item</code>.
	 * @param item the item whose state changed
	 * @param expandable the new state
	 */
	public void expandableChanged( Dockable item, boolean expandable );
}
