package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.dock.AbstractToolbarDockStation;

/**
 * A listener that can be added to an {@link AbstractToolbarDockStation}.
 * 
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemListener{
	/**
	 * Called if the state of <code>item</code> changed from
	 * <code>oldState</code> to <code>newState</code>.
	 * 
	 * @param item
	 *            the source of the event
	 * @param oldState
	 *            the old state of <code>item</code>
	 * @param newState
	 *            the new state of <code>item</code>
	 */
	public void changed( ExpandableToolbarItem item, ExpandedState oldState,
			ExpandedState newState );
}
