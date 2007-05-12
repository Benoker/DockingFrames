package bibliothek.extension.gui.dock.theme;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import bibliothek.extension.gui.dock.theme.bubble.BubbleStackDockComponent;
import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.event.DockControllerAdapter;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.stack.DefaultStackDockComponent;
import bibliothek.gui.dock.themes.DefaultTheme;
import bibliothek.gui.dock.themes.ThemeProperties;

@ThemeProperties(
		authors = { "Ivan Seidl", "Benjamin Sigg" }, 
		descriptionBundle = "theme.bubble.description", 
		nameBundle = "theme.bubble", 
		webpages = { "" }  )
public class BubbleTheme extends DefaultTheme {
    private Map<String, Color> colors = new HashMap<String, Color>();
    
	private Listener listener = new Listener();
	
    public BubbleTheme(){
        colors.put( "tab.border.active",            new Color( 150, 0, 0 ) );
        colors.put( "tab.border.active.mouse",      new Color( 200, 100, 100 ) );
        colors.put( "tab.border.inactive",          new Color( 100, 100, 100 ) );
        colors.put( "tab.border.inactive.mouse",    new Color( 100, 175, 100 ) );
        colors.put( "tab.top.active",               new Color( 200, 0, 0 ) );
        colors.put( "tab.top.active.mouse",         new Color( 255, 100, 100 ) );
        colors.put( "tab.top.inactive",             new Color( 150, 150, 150 ) );
        colors.put( "tab.top.inactive.mouse",       new Color( 150, 255, 150 ) );
        colors.put( "tab.bottom.active",            new Color( 255, 100, 100 ) );
        colors.put( "tab.bottom.active.mouse",      new Color( 255, 200, 200 ) );
        colors.put( "tab.bottom.inactive",          new Color( 200, 200, 200 ) );
        colors.put( "tab.bottom.inactive.mouse",    new Color( 220, 255, 220 ) );
        colors.put( "tab.text.active",              new Color( 0, 0, 0 ));
        colors.put( "tab.text.active.mouse",        new Color( 0, 0, 0 ));
        colors.put( "tab.text.inactive",            new Color( 100, 100, 100 ));
        colors.put( "tab.text.inactive.mouse",      new Color( 25, 25, 25 ));
    }
    
    public Color getColor( String key ){
        return colors.get( key );
    }
    
	@Override
	public void install( DockController controller ){
		super.install( controller );
        
		// Exchange the DockComponents
		for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) )
        			stack.setStackComponent( new BubbleStackDockComponent( this ) );
        	}
        }
		
		controller.addDockControllerListener( listener );
	}
	
	@Override
	public void uninstall( DockController controller ){
		super.uninstall( controller );
		
		controller.removeDockControllerListener( listener );
		
    	// Exchange the DockComponents
        for( int i = 0, n = controller.getStationCount(); i<n; i++ ){
        	DockStation station = controller.getStation(i);
        	if( station instanceof StackDockStation ){
        		StackDockStation stack = (StackDockStation)station;
        		if( stack.getStackComponent() instanceof BubbleStackDockComponent )
        			stack.setStackComponent( new DefaultStackDockComponent() );
        	}
        }
	}
	
    /**
     * A listener to the Controller
     * @author Benjamin Sigg
     */
    private class Listener extends DockControllerAdapter{
		@Override
		public void dockableRegistered(DockController controller, Dockable dockable) {
			if( dockable instanceof StackDockStation ){
				StackDockStation stack = (StackDockStation)dockable;
				if( !(stack.getStackComponent() instanceof BubbleStackDockComponent) ){
					stack.setStackComponent( new BubbleStackDockComponent( BubbleTheme.this ) );
				}
			}
		}
    }
}
