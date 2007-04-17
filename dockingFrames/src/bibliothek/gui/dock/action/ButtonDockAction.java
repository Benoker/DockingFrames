package bibliothek.gui.dock.action;

import bibliothek.gui.dock.Dockable;

/**
 * An action that can be pressed like a button.
 * @author Benjamin Sigg
 *
 */
public interface ButtonDockAction extends StandardDockAction, StandardDropDownItemAction {
    /**
     * Invoked when this action is triggered by the user.
     * @param dockable The {@link Dockable} which is associated with
     * this DockAction, and which shall be used for the current action.
     */
	public void action( Dockable dockable );
}
