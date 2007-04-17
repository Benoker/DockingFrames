package bibliothek.gui.dock.event;

import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A listener of a {@link DropDownAction}. The listener gets informed whenever
 * the number of actions changes, or the selection changes.
 * @author Benjamin Sigg
 *
 */
public interface DropDownActionListener {	
	/**
	 * Called when the selection of <code>action</code> has changed.
	 * @param action the action whose selection is changed
	 * @param dockables the set of {@link Dockable Dockables} for which
	 * the selection has changed
	 * @param selection the new selected child, might be <code>null</code>
	 */
	public void selectionChanged( DropDownAction action, Set<Dockable> dockables, DockAction selection );
}
