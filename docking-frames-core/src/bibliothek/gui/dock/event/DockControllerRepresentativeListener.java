package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockElementRepresentative;

/**
 * A listener added to {@link DockController}, gets informed about
 * changes in the set of {@link DockElementRepresentative}s.
 * @author Benjamin Sigg
 *
 */
public interface DockControllerRepresentativeListener {
    /**
     * Informs this listener that <code>representative</code> was added to
     * <code>controller</code>.
     * @param controller the source of the event
     * @param representative the element that was added
     */
    public void representativeAdded( DockController controller, DockElementRepresentative representative );
    
    /**
     * Informs this listener that <code>representative</code> was removed
     * from <code>controller</code>.
     * @param controller the source of the event
     * @param representative the element that was removed
     */
    public void representativeRemoved( DockController controller, DockElementRepresentative representative );
}
