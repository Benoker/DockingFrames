package bibliothek.gui.dock.control;

import java.awt.event.InputEvent;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.accept.DockAcceptance;

/**
 * A {@link DockRelocatorMode} that adds itself as a {@link DockAcceptance}
 * to the {@link DockController} when activated.
 * @author Benjamin Sigg
 *
 */
public abstract class AcceptanceDockRelocatorMode implements DockRelocatorMode, DockAcceptance {
    /** whether this mode is active */
    private boolean active;
    /** the keys that must be pressed to activate */
    private int onmask;
    /** the keys that must not be pressed to activate */
    private int offmask;
    
    /**
     * Creates a new mode. The masks are created by using the
     * constants "xzy_DOWN_MASK" from {@link InputEvent}.
     * @param onmask the keys that must be pressed to activate this mode
     * @param offmask the keys that must not be pressed to activate this mode
     */
    public AcceptanceDockRelocatorMode( int onmask, int offmask ){
        this.onmask = onmask;
        this.offmask = offmask;
    }
    
    public void setActive( DockController controller, boolean active ) {
        if( this.active != active ){
            if( active )
                controller.addAcceptance( this );
            else
                controller.removeAcceptance( this );
            
            this.active = active;
        }
    }

    public boolean shouldBeActive( DockController controller, int modifiers ) {
        return (modifiers & (onmask | offmask)) == onmask;
    }
}
