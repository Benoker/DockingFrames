package bibliothek.gui.dock.event;

import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;

/**
 * An observer of a {@link SelectableDockAction}. This listener is triggered every
 * time when the selected-state of a {@link Dockable} changes.
 * @author Benjamin Sigg
 */
public interface SelectableDockActionListener {
	/**
	 * Triggered by a {@link SelectableDockAction} if the selection-state of
	 * a {@link Dockable} has changed.
	 * @param action the invoking action
	 * @param dockables a set of Dockables whose state has been changed
	 */
	public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables );
}
