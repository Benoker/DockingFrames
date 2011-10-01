package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.AttractorStrategy;

public class ToolbarAttractorStrategy implements AttractorStrategy{
	@Override
	public Attraction attract( ScreenDockStation parent, Dockable fixed, Dockable moved ){
		DockController controller = parent.getController();
		if( controller == null ){
			return Attraction.NEUTRAL;
		}
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( fixed ) && strategy.isToolbarPart( moved )){
			return Attraction.ATTRACTED;
		}
		
		return Attraction.NEUTRAL;
	}
}
