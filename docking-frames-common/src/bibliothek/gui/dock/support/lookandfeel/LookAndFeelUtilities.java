package bibliothek.gui.dock.support.lookandfeel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;

/**
 * A set of methods that used to update the {@link LookAndFeel} of {@link Component}s.
 * @author Benjamin Sigg
 */
public class LookAndFeelUtilities {
	/**
	 * Updates the look and feel for all windows that can be found through
	 * the collection of components.
	 * @param components a set of known components
	 */
	public static void updateUI( Collection<Component> components ){
		Set<Component> visit = new HashSet<Component>();
		
		for( Component component : components ){
		    component = getAncestor( component );
			Window window = SwingUtilities.getWindowAncestor( component );
			if( window != null )
				change( window, visit );
			else
				change( component, visit );
		}
	}
	
	/**
	 * Gets the one parent of <code>component</code> which does not have a
	 * parent itself.
	 * @param component some component
	 * @return a parent of <code>component</code> or <code>component</code> itself.
	 */
	private static Component getAncestor( Component component ){
	    Container parent = component.getParent();
	    if( parent == null )
	        return component;
	    
	    return getAncestor( parent );
	}
	
	/**
	 * Updates the look and feel of <code>base</code> and all its
	 * children. Recursively goes through all {@link Window}s that
	 * are owned by <code>base</code> (assuming <code>base</code>
	 * is itself a <code>Window</code>).<br>
	 * @param base the root of a component-tree
	 * @param visit the set of roots that were already visited, <code>base</code>
	 * is added to this set and if <code>base</code> was already in the set,
	 * then this method returns immediately
	 */
    private static void change( Component base, Set<Component> visit ){
        if( visit.add( base )){
            SwingUtilities.updateComponentTreeUI( base );
            if( base instanceof Window ){
                Window window = (Window)base;
                
                for( Window child : window.getOwnedWindows() )
                    change( child, visit );
            }
        }
    }
}
