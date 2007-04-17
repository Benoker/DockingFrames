package bibliothek.gui.dock.action.dropdown;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DropDownAction;

/**
 * A factory that creates {@link DropDownFilter}.
 * @author Benjamin Sigg
 */
public interface DropDownFilterFactory {
	/**
	 * Creates a new filter.
	 * @param action the action for which the filter will be used
	 * @param dockable the owner of the <code>action</code>
	 * @param view the view where the filter should write its properties into
	 * @return the new filter
	 */
	public DropDownFilter createView( DropDownAction action, Dockable dockable, DropDownView view );
}
