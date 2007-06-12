package bibliothek.gui.dock.themes.basic.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A handler to connect a {@link ButtonDockAction} with a 
 * {@link BasicButtonModel}.
 * @author Benjamin Sigg
 */
public class BasicButtonHandler extends BasicHandler<ButtonDockAction> {
    /**
     * Creates the new handler.
     * @param action the action to observe
     * @param dockable the dockable for which the action is shown
     */
    public BasicButtonHandler( ButtonDockAction action, Dockable dockable ) {
        super( action, dockable );
    }

    @Override
    public void triggered() {
        getAction().action( getDockable() );
    }
}
