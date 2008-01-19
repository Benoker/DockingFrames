package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;

/**
 * A listener receiving events from a {@link bibliothek.gui.dock.control.DockRegister}.
 * @author Benjamin Sigg
 *
 */
public interface DockRegisterListener {
    /**
     * Invoked right before the <code>dockable</code> is registered in the
     * <code>controller</code>.
     * @param controller the controller who does not yet know <code>dockable</code>.
     * @param dockable the dockable who does not yet know <code>controller</code>.
     */
    public void dockableRegistering( DockController controller, Dockable dockable );
    
    /**
     * Invoked right before the <code>station</code> is registered in
     * the <code>controller</code>.
     * @param controller the controller who does not yet know <code>station</code>.
     * @param station the station who does not yet know <code>controller</code>.
     */
    public void dockStationRegistering( DockController controller, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was registered. Note that this method can 
     * be called while a {@link Dockable} is dragged. See the method
     * {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller where <code>dockable</code> was added
     * @param dockable the {@link Dockable} that was added
     */
    public void dockableRegistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after a {@link DockStation} was registered. This method can
     * be called while a {@link Dockable} is dragged. See the method
     * {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller where <code>station</code> was added
     * @param station the {@link DockStation} that was added
     */
    public void dockStationRegistered( DockController controller, DockStation station );
        
    /**
     * Invoked after <code>dockable</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller from where <code>dockable</code> was removed 
     * @param dockable the removed {@link Dockable}
     */
    public void dockableUnregistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after <code>station</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller from where <code>dockable</code> was removed 
     * @param station the removed {@link DockStation}
     */
    public void dockStationUnregistered( DockController controller, DockStation station );
    
    /**
     * Invoked when <code>dockable</code> was added and removed from the <code>controller</code>, or
     * was removed and added again to <code>controller</code>. This method is only
     * invoked if a call to {@link #dockableRegistered(DockController, Dockable)} and
     * {@link #dockableUnregistered(DockController, Dockable)} was suppressed. It
     * is unknown whether <code>dockable</code> is now registerd ad <code>controller</code>.
     * @param controller the controller whose register <code>dockable</code> cycled
     * @param dockable some {@link Dockable}
     */
    public void dockableCycledRegister( DockController controller, Dockable dockable );
}
