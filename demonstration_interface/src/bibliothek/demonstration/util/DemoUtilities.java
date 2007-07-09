package bibliothek.demonstration.util;

import java.awt.Component;
import java.awt.Window;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

public class DemoUtilities {
	/**
	 * Updates the look and feel for all frames that can be found through
	 * the collection of components.
	 * @param components a set of known components
	 */
	public static void updateUI( Collection<Component> components ){
		Set<Component> visit = new HashSet<Component>();
		
		for( Component component : components ){
			Window window = SwingUtilities.getWindowAncestor( component );
			if( window != null )
				change( window, visit );
			else
				change( component, visit );
		}
	}
	
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
