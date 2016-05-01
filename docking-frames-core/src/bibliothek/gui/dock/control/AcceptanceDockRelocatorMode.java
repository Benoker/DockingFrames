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
    
    /** which keys must be pressed in order to activate this mode */
    private ModifierMask mask;
    
    /**
     * Creates a new mode. The masks are created by using the
     * constants "xzy_DOWN_MASK" from {@link InputEvent}.
     * @param onmask the keys that must be pressed to activate this mode
     * @param offmask the keys that must not be pressed to activate this mode
     */
    public AcceptanceDockRelocatorMode( int onmask, int offmask ){
        mask = new ModifierMask( onmask, offmask );
    }
    
    /**
     * Creates a new mode.
     * @param mask the pattern of keys that must be pressed to activate this
     * mode
     */
    public AcceptanceDockRelocatorMode( ModifierMask mask ){
        if( mask == null )
            throw new IllegalArgumentException( "mask must not be null" );
        
        this.mask = mask;
    }
    
    /**
     * Sets the keys that must be pressed in order to activate this mode.
     * @param mask the mask
     */
    public void setMask( ModifierMask mask ) {
        if( mask == null )
            throw new IllegalArgumentException( "mask must not be null" );
        
        this.mask = mask;
    }
    
    /**
     * Gets the mask for this mode.
     * @return the mask
     */
    public ModifierMask getMask() {
		return mask;
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
        return mask.matches( modifiers );
    }
}
