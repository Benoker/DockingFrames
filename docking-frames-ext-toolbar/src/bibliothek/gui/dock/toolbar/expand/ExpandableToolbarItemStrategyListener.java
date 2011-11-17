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
	 * Called if <code>item</code> was stretched.
	 * @param item the item whose state changed
	 */
	public void stretched( Dockable item );
	
	/**
	 * Called if <code>item</code> was made small.
	 * @param item the item show state changed
	 */
	public void shrunk( Dockable item );
	
	/**
	 * Called if the result of {@link ExpandableToolbarItemStrategy#isEnabled(Dockable, ExpandedState)} changed
	 * for <code>item</code> and <code>state</code>.
	 * @param item the item whose enablement changed
	 * @param state the state whose enablement changed
	 * @param enabled whether <code>item</code> can be in mode <code>state</code>
	 */
	public void enablementChanged( Dockable item, ExpandedState state, boolean enabled );
	
}
