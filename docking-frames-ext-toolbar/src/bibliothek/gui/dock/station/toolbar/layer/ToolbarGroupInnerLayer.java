package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

/**
 * This layer is used to define the non rectangular surface of a
 * {@link ToolbarGroupDockStation}, it means the surface which is really
 * occupied by dockables.
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupInnerLayer implements DockStationDropLayer{
	private final ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.BASE;

	/**
	 * Creates a new layer.
	 * 
	 * @param station
	 *            the station which owns this layer
	 */
	public ToolbarGroupInnerLayer( ToolbarGroupDockStation station ){
		this.station = station;
	}

	@Override
	public boolean canCompare( DockStationDropLayer layer ){
		return false;
	}

	@Override
	public int compare( DockStationDropLayer layer ){
		return 0;
	}

	@Override
	public boolean contains( int x, int y ){
		final Point mouseCoord = new Point(x, y);
		SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
		final ToolbarGridLayoutManager<StationChildHandle> layout = station.getLayoutManager();
		if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the layer
			// is computed with regards to the station component
			return getComponent().contains(mouseCoord);
		} else {
			int count = station.columnCount();
			
			// check if the point is *inside* a child
			for( int i = 0; i < count; i++ ){
				Rectangle bound = layout.getBounds( i );
				if( bound.contains( mouseCoord )){
					return true;
				}
			}
			
			// check if the point is *between* two children
			for( int i = 0; i <= count; i++ ){
				Rectangle bound = layout.getGapBounds( i );
				if( bound.contains( mouseCoord )){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Component getComponent(){
		final Dockable dockable = station.asDockable();
		if (dockable == null){
			return null;
		}
		return dockable.getComponent();
	}

	@Override
	public LayerPriority getPriority(){
		return priority;
	}

	@Override
	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}

	@Override
	public ToolbarGroupDockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
