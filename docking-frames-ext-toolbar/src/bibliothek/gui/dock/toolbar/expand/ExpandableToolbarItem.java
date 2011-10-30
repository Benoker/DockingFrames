package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;

/**
 * An {@link ExpandableToolbarItem} is a part of a toolbar that can have different
 * shapes.
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItem extends Dockable{
	/**
	 * Adds the observer <code>listener</code> to this item.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addExpandableListener( ExpandableToolbarItemListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this item.
	 * @param listener the listener to remove
	 */
	public void removeExpandableListener( ExpandableToolbarItemListener listener );
	
	/**
	 * Changes the state of this item to <code>state</code>.
	 * @param state the new state
	 */
	public void setExpandedState( ExpandedState state );
	
	/**
	 * Gets the current state of this item.
	 * @return the current state
	 */
	public ExpandedState getExpandedState();
}
