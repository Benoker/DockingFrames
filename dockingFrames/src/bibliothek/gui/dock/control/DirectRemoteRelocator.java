package bibliothek.gui.dock.control;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.RemoteRelocator.Reaction;

/**
 * A simplified version of {@link RemoteRelocator}. This direct remote relocator
 * assumes that the correct mouse buttons are always pressed. A client
 * can initiate a drag & drop operation by calling {@link #init(int, int, int, int) init}.
 * Then the client has to call at least one time {@link #drag(int, int) drag}, before
 * he can invoke {@link #drop(int, int) drop} to let the dragged {@link Dockable}
 * fall down. A client can {@link #cancel() cancel} the operation at any time.<br>
 * Note that only one operation is supported at once. Note also that there is 
 * no guarantee, that a drag & drop operation is executed successfully.
 * @author Benjamin Sigg
 *
 */
public interface DirectRemoteRelocator {
    /**
     * This method starts or cancels a drag & drop operation. This method simulates
     * a mouse-pressed event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param dx the x-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     * @param dy the y-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     */
    public void init( int x, int y, int dx, int dy );
    
    /**
     * This method works on the drag-part of a drag & drop operation.
     * This method simulates a mouse-dragged event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     */
    public void drag( int x, int y );
    
    /**
     * This method works on the drop-part of a drag & drop operation.
     * This method simulates a mouse-released event.<br>
     * The drag & drop operation may not be finished after an invocation of this
     * method, clients should carefully analyze the resulting {@link Reaction} 
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     */
    public void drop( int x, int y );
    
    /**
     * Cancels the current drag & drop operation. No events have to be delivered
     * any more to this relocator.
     */
    public void cancel();
}
