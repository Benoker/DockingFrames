package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * Describes the area around a {@link ToolbarGroupDockStation} where the user
 * can drop a {@link Dockable} into.
 * 
 * @author Herve Guillaume
 */
public class SideSnapDropLayerComplex implements DockStationDropLayer{
	private final ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.OUTSIDE_LOW;

	/**
	 * Creates a new <code>SideSnapDropLayerComplex</code>
	 * 
	 * @param station
	 *            the owner of this level
	 */
	public SideSnapDropLayerComplex( ToolbarGroupDockStation station ){
		this.station = station;
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
	public boolean canCompare( DockStationDropLayer level ){
		return false;
	}

	@Override
	public int compare( DockStationDropLayer level ){
		return 0;
	}

	@Override
	public Component getComponent(){
		return station.getComponent();
	}

	@Override
	public DockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}

	@Override
	public boolean contains( int x, int y ){
		System.out.print("ToolbarGroup Snap :");
		if (!station.isAllowSideSnap()){
			// snap is not allowed
			System.out.println("false");
			return false;
		}
		if (isComponentContain(x, y)){
			// if the mouse is in inside component, the snap layer should not
			// manage the drop action
			System.out.println("isComponentContain false");
			return false;
		}

		final int size = station.getBorderSideSnapSize();

		if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the snap
			// layer is computed with regards to the station component
			Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			Rectangle rec = getComponent().getBounds();
			Rectangle recSnap = new Rectangle(rec.x - size, rec.y - size,
					rec.width + (size * 2), rec.height + (size * 2));
			System.out.println(recSnap.contains(mouseCoord));
			return recSnap.contains(mouseCoord);

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
				Rectangle deducted = new Rectangle(firstBounds.x - size,
						firstBounds.y - size,
						((int) lastBounds.getMaxX() - firstBounds.x)
								+ (size * 2),
						((int) lastBounds.getMaxY() - firstBounds.y)
								+ (size * 2));

				zone.add(new Area(deducted));
			}
			System.out.println(zone.contains(mouseCoord));
			return zone.contains(mouseCoord);
		}
	}

	/**
	 * Tells if the mouse is inside the station
	 * 
	 * @param x
	 *            x mouse position in screen coordinates
	 * @param y
	 *            y mouse position in screen coordinates
	 * @return true if the mouse is inside the station
	 */
	private boolean isComponentContain( int x, int y ){
		if (station.columnCount() == 0){
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

				// the bounds of the column
				Rectangle deducted = new Rectangle(firstBounds.x,
						firstBounds.y,
						((int) lastBounds.getMaxX() - firstBounds.x),
						((int) lastBounds.getMaxY() - firstBounds.y));

				zone.add(new Area(deducted));
			}
			return zone.contains(mouseCoord);
		}
	}

}
