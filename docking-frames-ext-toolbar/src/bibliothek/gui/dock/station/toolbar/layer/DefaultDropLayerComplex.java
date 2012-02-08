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
 * This layer is used to define the non rectangular surface of a
 * {@link ToolbarGroupDockStation}, it means the surface which is really
 * occupied by dockables.
 * 
 * @author Herve Guillaume
 */
public class DefaultDropLayerComplex implements DockStationDropLayer{
	private final ToolbarGroupDockStation station;
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
		System.out.print("ToolbarGroup Complex :");
		final Component component = getComponent();
//		if (component == null){
//			System.out.println("true");
//			return true;
//		} else if (station.columnCount() == 0){
//			// if there's no dockable inside the station, the shape of the snap
//			// layer is computed with regards to the station component
//			final Point mouseCoord = new Point(x, y);
//			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
//			System.out.println(getComponent().contains(mouseCoord));
//			return getComponent().contains(mouseCoord);
//		} else{
			// if there are dockables inside the station, we compute the shape with
			// regards to the inside dockables
			final Point mouseCoord = new Point(x, y);
			final Area zone = new Area();
			// we take into account each column station
			for (int columnIndex = 0; columnIndex < station.columnCount(); columnIndex++){
				// the first dockable of the column

				final Component firstComponent = station.getDockable(
						columnIndex, 0).getComponent();
				final Rectangle firstBoundsDraft = firstComponent.getBounds();
				Point upperleft = firstBoundsDraft.getLocation();
				SwingUtilities.convertPointToScreen(upperleft, firstComponent);
				final Rectangle firstBounds = new Rectangle(upperleft.x,
						upperleft.y, firstBoundsDraft.width,
						firstBoundsDraft.height);

				// the last dockable of the column
				final Component lastComponent = station.getDockable(
						columnIndex, station.lineCount(columnIndex) - 1)
						.getComponent();
				final Rectangle lastBoundsDraft = lastComponent.getBounds();
				upperleft = lastBoundsDraft.getLocation();
				SwingUtilities.convertPointToScreen(upperleft, lastComponent);
				final Rectangle lastBounds = new Rectangle(upperleft.x,
						upperleft.y, lastBoundsDraft.width,
						lastBoundsDraft.height);

				// the bounds of the column
				final Rectangle deducted = new Rectangle(firstBounds.x,
						firstBounds.y,
						((int) lastBounds.getMaxX() - firstBounds.x),
						((int) lastBounds.getMaxY() - firstBounds.y));

				zone.add(new Area(deducted));
			}
			System.out.println(zone.contains(mouseCoord));
			return zone.contains(mouseCoord);
//		}
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
	public DockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
