package bibliothek.gui.dock.toolbar.measurement;

import java.awt.Color;
import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.toolbar.ToolbarDropInfo;

public class ToolbarDockStationSample implements DropSample{
	private ToolbarDockStation station;
	private Dockable dockable = new ComponentDockable();
	
	public ToolbarDockStationSample(){
		station = new ToolbarDockStation();
		DockController controller = new DockController();
		controller.add( station );
	}
	
	@Override
	public ToolbarDockStation getStation(){
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
		else{
			ToolbarDropInfo<?> info = (ToolbarDropInfo<?>)operation;
			
			int index = station.indexOf( info.getDockableBeneathMouse() );
			Position side = info.getSideDockableBeneathMouse();
			
			int color = 0;
			switch( side ){
				case CENTER:
					color = 255;
					break;
				case NORTH:
				case SOUTH:
					color = 150;
					break;
				case EAST:
				case WEST:
					color = 50;
					break;
			}
			
			index %= 3;
			if( index == 0 ){
				return new Color( color, 0, 0 );
			}
			else if( index == 1 ){
				return new Color( 0, color, 0 );
			}
			else if( index == 2 ){
				return new Color( 0, 0, color );
			}
			
			return Color.WHITE;
		}
	}

}
