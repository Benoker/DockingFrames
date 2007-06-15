package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A listener used by a {@link bibliothek.gui.dock.control.DockRelocationManager}
 * to inform when a {@link bibliothek.gui.Dockable} is moved around.
 * @author Benjamin Sigg
 *
 */
public interface DockRelocationManagerListener {
    /**
     * Invoked before a {@link Dockable} is moved around. This method is called
     * after the user has released the mouse which was dragging a {@link DockTitle}
     * around.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the future parent of <code>dockable</code>
     * @see #dockablePut(DockController, Dockable, DockStation)
     */
    public void dockableDrag( DockController controller, Dockable dockable, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was moved.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the new parent of <code>dockable</code>
     * @see #dockableDrag(DockController, Dockable, DockStation)
     */
    public void dockablePut( DockController controller, Dockable dockable, DockStation station );
    
}
