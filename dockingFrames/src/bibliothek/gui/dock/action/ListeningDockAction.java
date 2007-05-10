package bibliothek.gui.dock.action;

import bibliothek.gui.DockController;

/**
 * An action that should receive an event when the {@link DockController} of
 * the structure using this action changes.
 * @author Benjamin Sigg
 *
 */
public interface ListeningDockAction extends DockAction {
    /**
     * Called when a new controller has been set.
     * @param controller the new controller
     */
    public void setController( DockController controller );
}
