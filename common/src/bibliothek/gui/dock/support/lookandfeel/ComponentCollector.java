package bibliothek.gui.dock.support.lookandfeel;

import java.awt.Component;
import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;

/**
 * A {@link ComponentCollector} is used by the {@link LookAndFeelList} when
 * the {@link LookAndFeel} was changed, and the {@link JComponent}s need to
 * be updated. This <code>ComponentCollector</code> has to give the 
 * {@link LookAndFeelList} the roots of some <code>Component</code>-trees. 
 * @author Benjamin Sigg
 *
 */
public interface ComponentCollector {
    /**
     * Gets a set of roots of {@link Component}-trees in order to
     * {@link JComponent#updateUI() update} the look and feel of the
     * <code>Component</code>s. 
     * @return the roots
     */
	public Collection<Component> listComponents();
}
