package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.ToolbarInterface;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.ToolbarTabDockStation;

/**
 * The default implementation of {@link ToolbarStrategy}.
 * 
 * @author Benjamin Sigg
 */
public class DefaultToolbarStrategy implements ToolbarStrategy {
	public Dockable ensureToolbarLayer( DockStation station, Dockable dockable ){
		// if (station instanceof ToolbarDockStation){
		// if (dockable instanceof ToolbarGroupDockStation){
		// return dockable;
		// } else{
		// ToolbarGroupDockStation result = new ToolbarGroupDockStation();
		// result.setController(station.getController());
		// result.drop(dockable);
		// return result;
		// }
		// }
		if( station instanceof ToolbarGroupDockStation ) {
			return dockable;
		}

		if( station instanceof ToolbarDockStation ) {
			if( dockable instanceof ToolbarGroupDockStation ) {
				return dockable;
			}
			else {
				ToolbarGroupDockStation result = new ToolbarGroupDockStation();
				result.setController( station.getController() );
				result.drop( dockable );
				return result;
			}
		}

		// if( station instanceof ToolbarContainerDockStation ) {
		if( station instanceof ToolbarContainerDockStation || station instanceof ScreenDockStation ) {
			if( dockable instanceof ToolbarDockStation ){
			//if( dockable.getClass() == ToolbarDockStation.class ) {
				return dockable;
			}
			else {
				ToolbarDockStation result = new ToolbarDockStation();
				result.setController( station.getController() );
				result.drop( dockable );
				return result;
			}
		}

		return null;
	}

	@Override
	public boolean isToolbarGroupPartParent( DockStation parent, Dockable child ){
		// if (child instanceof ToolbarDockStation
		// && parent instanceof ScreenDockStation){
		// return true;
		// }
		if( child instanceof ToolbarDockStation && parent instanceof ScreenDockStation ) {
			return true;
		}
		if( child instanceof ComponentDockable && parent instanceof ToolbarTabDockStation ){
			return true;
		}
		return parent instanceof ToolbarInterface;
	}

	@Override
	public boolean isToolbarGroupPart( Dockable dockable ){
		return dockable instanceof ComponentDockable || dockable instanceof ToolbarGroupDockStation;
	}

	@Override
	public boolean isToolbarPart( Dockable dockable ){
		return dockable instanceof ToolbarDockStation || isToolbarGroupPart( dockable );
	}
}
