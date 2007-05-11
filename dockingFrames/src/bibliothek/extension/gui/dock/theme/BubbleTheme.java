package bibliothek.extension.gui.dock.theme;

import java.awt.Color;

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
	private Color activeBorderColor = new Color( 150, 0, 0 );
	private Color activeDarkColor = new Color( 200, 0, 0 );
	private Color activeBrightColor = new Color( 255, 100, 100 );
	private Color activeTextColor = new Color( 0, 0, 0 );
	
	private Color inactiveBorderColor = new Color( 100, 100, 100 );
	private Color inactiveDarkColor = new Color( 150, 150, 150 );
	private Color inactiveBrightColor = new Color( 200, 200, 200 );
	private Color inactiveTextColor = new Color( 100, 100, 100 );
	
	private Listener listener = new Listener();
	
	public Color getActiveBorderColor(){
		return activeBorderColor;
	}
	public Color getActiveBrightColor(){
		return activeBrightColor;
	}
	public Color getActiveDarkColor(){
		return activeDarkColor;
	}
	public Color getActiveTextColor(){
		return activeTextColor;
	}
	public Color getInactiveBorderColor(){
		return inactiveBorderColor;
	}
	public Color getInactiveBrightColor(){
		return inactiveBrightColor;
	}
	public Color getInactiveDarkColor(){
		return inactiveDarkColor;
	}
	public Color getInactiveTextColor(){
		return inactiveTextColor;
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
