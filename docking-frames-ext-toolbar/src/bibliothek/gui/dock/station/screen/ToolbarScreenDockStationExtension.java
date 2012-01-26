package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;

/**
 * Modifies the behavior of {@link ScreenDockStation} such that dropping a toolbar item results in
 * the creation of additional, new {@link DockStation}s.
 * @author Benjamin Sigg
 */
public class ToolbarScreenDockStationExtension implements ScreenDockStationExtension {
	private DockController controller;
	
	private Dockable pending;
	
	public ToolbarScreenDockStationExtension( DockController controller ){
		this.controller = controller;
	}

	@Override
	public void drop( ScreenDockStation station, DropArguments arguments ){
		if( arguments.getWindow() == null ){
			ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
			Dockable dockable = arguments.getDockable();
			
			if( strategy.isToolbarPart( dockable ) ){
				Dockable replacement = strategy.ensureToolbarLayer( station, dockable );
				if( replacement != dockable ){					
					pending = dockable;
				}
				else{
					pending = null;
				}
				arguments.setDockable( replacement );
			}
		}
		else{
			pending = null;
		}
	}

	@Override
	public void dropped( ScreenDockStation station, DropArguments arguments, boolean successfull ){
		if( pending != null && successfull ){
			DockStation child = arguments.getDockable().asDockStation();
			DockableProperty successor = arguments.getProperty().getSuccessor();
			if( successor == null || !child.drop( pending, successor )){
				child.drop( pending );
			}
		}
		pending = null;
	}
}
