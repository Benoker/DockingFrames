package bibliothek.gui.dock.action;

import bibliothek.gui.dock.Dockable;

/**
 * Represents a menu. If the user triggers this action, a menu with more
 * actions should pop up.
 * @author Benjamin Sigg
 */
public interface MenuDockAction extends StandardDockAction {
    /**
     * Returns the menu that is represented by this action.
     * @param dockable the Dockable for which the menu is shown
     * @return the items of the menu or <code>null</code>
     */
    public DockActionSource getMenu( Dockable dockable );
}
