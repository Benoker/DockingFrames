package bibliothek.gui.dock.toolbar.measurement;

import java.awt.Color;
import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDropInfo;

public class ToolbarContainerDockStationSample implements DropSample{
	private ToolbarContainerDockStation station;
	private Dockable dockable = new ToolbarGroupDockStation();
	
	public ToolbarContainerDockStationSample(){
		station = new ToolbarContainerDockStation( Orientation.VERTICAL );
		station.setDockablesMaxNumber( -1 );
		DockController controller = new DockController();
		controller.add( station );
	}
	
	@Override
	public ToolbarContainerDockStation getStation(){
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
		if( operation == null ){
			return Color.BLACK;
		}
		
		ToolbarContainerDropInfo info = (ToolbarContainerDropInfo)operation;
		
		for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
			if( station.getDockable( i ) == info.getDockableBeneathMouse() ){
				int index = i;
				if( (info.getSideDockableBeneathMouse() == Position.SOUTH) || (info.getSideDockableBeneathMouse() == Position.EAST) ) {
					index++;
				}
				if( index % 2 == 0 ){
					return Color.RED;
				}
				else{
					return Color.BLUE;
				}
			}
		}
		
		return Color.WHITE;
	}
}
