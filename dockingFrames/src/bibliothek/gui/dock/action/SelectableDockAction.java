package bibliothek.gui.dock.action;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * An action which has two states "selected" and "not selected".
 * @author Benjamin Sigg
 *
 */
public interface SelectableDockAction extends StandardDockAction, StandardDropDownItemAction {
    /**
     * Tells whether this DockAction is selected or not (in respect
     * to the given <code>dockable</code>).
     * @param dockable The {@link Dockable} for which this action may be selected
     * or not selected
     * @return <code>true</code> if this DockAction is selcted, <code>false</code>
     * otherwise
     */
    public boolean isSelected( Dockable dockable );
	
    /**
     * Sets the selected state for <code>dockable</code>.
     * @param dockable the affected dockable
     * @param selected the new state
     */
	public void setSelected( Dockable dockable, boolean selected );
	
	/**
	 * Adds a listener to this action. The listener will be invoked whenever
	 * the selected state of a {@link Dockable} changes.
	 * @param listener the new listener
	 */
	public void addSelectableListener( SelectableDockActionListener listener );
	
	/**
	 * Removes a listener from this action.
	 * @param listener the listener to remove
	 */
	public void removeSelectableListener( SelectableDockActionListener listener );
}
