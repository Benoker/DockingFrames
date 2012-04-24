package bibliothek.gui.dock.toolbar.measurement;

import java.awt.Color;
import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDropInfo;

public class ToolbarGroupDockStationSample implements DropSample {
	private ToolbarGroupDockStation station;
	private Dockable dockable = new ToolbarDockStation();

	public ToolbarGroupDockStationSample(){
		station = new ToolbarGroupDockStation();
		station.setOrientation( Orientation.VERTICAL );
		DockController controller = new DockController();
		controller.add( station );
	}

	@Override
	public ToolbarGroupDockStation getStation(){
		return station;
	}

	@Override
	public Component getComponent(){
		return station.getComponent();
	}

	@Override
	public Color dropAt( int mouseX, int mouseY ){
		StationDropItem item = new StationDropItem( mouseX, mouseY, mouseX, mouseY, dockable );
		StationDropOperation operation = station.prepareDrop( item );
		if( operation == null ) {
			return Color.BLACK;
		}
		ToolbarGroupDropInfo info = (ToolbarGroupDropInfo)operation;
		
		int column = info.getColumn();
		int line = info.getLine();
		
		if( line == -1 ){
			if( column % 2 == 0 ){
				return Color.RED;
			}
			else{
				return Color.ORANGE;
			}
		}
		else{
			if( line % 2 == 0 ){
				return Color.GREEN;
			}
			else{
				return Color.CYAN;
			}
		}
	}
}
