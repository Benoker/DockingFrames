package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A {@link Combiner} normally used by the {@link FlatTheme} to replace the
 * default combiner.
 * @author Benjamin Sigg
 */
public class BubbleCombiner implements Combiner{
	private BubbleTheme theme;
	
	public BubbleCombiner( BubbleTheme theme ){
		if( theme == null )
			throw new IllegalArgumentException( "Theme must not be null" );
		
		this.theme = theme;
	}
	
    public Dockable combine( Dockable old, Dockable drop, DockStation parent ) {
        StackDockStation stack = createStackDockStation( parent.getTheme() );
        
        stack.setStackComponent( new BubbleStackDockComponent( theme ));
        stack.drop( old );
        stack.drop( drop );
        
        return stack;
    }
    
    /**
     * Creates a new {@link StackDockStation} which will be populated
     * with two {@link Dockable Dockables}.
     * @param theme The theme that the station will have, might be <code>null</code>
     * @return the new station
     */
    protected StackDockStation createStackDockStation( DockTheme theme ){
        return new StackDockStation( theme );
    }
}
