package bibliothek.gui.dock.control;

import java.awt.event.MouseEvent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;

/**
 * A {@link DoubleClickObserver} is added to the {@link DoubleClickController}
 * and may receive an event if a {@link Dockable}, which is child or equal
 * to {@link #getTreeLocation()}, was clicked twice. The event is only received
 * if no other <code>DoubleClickObserver</code> processed the event.
 * @author Benjamin Sigg
 *
 */
public interface DoubleClickObserver {
    /**
     * Gets the location of this observer in the tree.
     * @return the location, <code>null</code> for an imaginary root.
     */
    public DockElement getTreeLocation();
    
    /**
     * Called when the user has clicked twice on <code>dockable</code> or
     * on one of the titles of <code>dockable</code>.
     * @param dockable the clicked element
     * @param event the cause of the invocation of this method
     * @return <code>true</code> if this observer processed the event (and
     * the event must no be forwarded to any other observer), <code>false</code>
     * if this observer is not interested in the event
     */
    public boolean process( Dockable dockable, MouseEvent event );
}
