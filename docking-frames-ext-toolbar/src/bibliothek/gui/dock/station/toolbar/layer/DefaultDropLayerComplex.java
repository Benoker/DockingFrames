package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * 
 * @author Herve Guillaume
 */
public class DefaultDropLayerComplex implements DockStationDropLayer{
	private ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.BASE;

	/**
	 * Creates a new layer.
	 * 
	 * @param station
	 *            the station which owns this layer
	 */
	public DefaultDropLayerComplex( ToolbarGroupDockStation station ){
		this.station = station;
	}

	public boolean canCompare( DockStationDropLayer layer ){
		return false;
	}

	public int compare( DockStationDropLayer layer ){
		return 0;
	}

	public boolean contains( int x, int y ){
		Component component = getComponent();
		if (component == null){
			return true;
		} else if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the snap
			// layer is computed with regards to the station component
			Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			return getComponent().contains(mouseCoord);
		} else{
			// if there's dockable inside the station, we compute the shape with
			// regards to the inside dockables
			Point mouseCoord = new Point(x, y);
			Area zone = new Area();
			// we take into account each column station
			for (int columnIndex = 0; columnIndex < station.columnCount(); columnIndex++){
				// the first dockable of the column
				Component firstComponent = station.getDockable(columnIndex, 0)
						.getComponent();
				Rectangle firstBoundsDraft = firstComponent.getBounds();
				Point upperleft = firstBoundsDraft.getLocation();
				SwingUtilities.convertPointToScreen(upperleft, firstComponent);
				Rectangle firstBounds = new Rectangle(upperleft.x, upperleft.y,
						firstBoundsDraft.width, firstBoundsDraft.height);

				// the last dockable of the column
				Component lastComponent = station.getDockable(columnIndex,
						station.lineCount(columnIndex) - 1).getComponent();
				Rectangle lastBoundsDraft = lastComponent.getBounds();
				upperleft = lastBoundsDraft.getLocation();
				SwingUtilities.convertPointToScreen(upperleft, lastComponent);
				Rectangle lastBounds = new Rectangle(upperleft.x, upperleft.y,
						lastBoundsDraft.width, lastBoundsDraft.height);

				// the bounds of the column increased with the snap size
				Rectangle deducted = new Rectangle(firstBounds.x,
						firstBounds.y,
						((int) lastBounds.getMaxX() - firstBounds.x),
						((int) lastBounds.getMaxY() - firstBounds.y));

				zone.add(new Area(deducted));
			}
			return zone.contains(mouseCoord);
		}
	}

	public Component getComponent(){
		Dockable dockable = station.asDockable();
		if (dockable == null){
			return null;
		}
		return dockable.getComponent();
	}

	public LayerPriority getPriority(){
		return priority;
	}

	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}

	public DockStation getStation(){
		return station;
	}

	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
