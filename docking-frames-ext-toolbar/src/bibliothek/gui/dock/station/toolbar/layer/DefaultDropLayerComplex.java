package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumn;
import bibliothek.gui.dock.station.toolbar.group.ToolbarColumnModel;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

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
		final Point mouseCoord = new Point(x, y);
		@SuppressWarnings("unchecked")
		final ToolbarGridLayoutManager<StationChildHandle> layout = station
				.getLayoutManager();
		if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the layer
			// is computed with regards to the station component
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			// System.out.println("MOUSE: " + mouseCoord);
			// System.out.println("Component: " + getComponent().getBounds());
			// System.out.println("DefaultDropLayerComplex: "
			// + getComponent().contains(mouseCoord));
			return getComponent().contains(mouseCoord);
		} else{
			// System.out.println("MOUSE: " + mouseCoord);
			// System.out.println("Columns number: " + station.columnCount());
			for (int columnIndex = 0; columnIndex < station.columnCount(); columnIndex++){
				Rectangle columnBounds = layout.getBounds(columnIndex);
				Point a = new Point(columnBounds.x, columnBounds.y);
				SwingUtilities.convertPointToScreen(a, getComponent());
				Rectangle columnBoundsTranslate = new Rectangle(a.x, a.y,
						columnBounds.width, columnBounds.height);

//				System.out.println("Column" + columnIndex + ": "
//						+ columnBoundsTranslate);
				if (columnBoundsTranslate.contains(mouseCoord)){
//					System.out.println("DefaultDropLayerComplex: TRUE");
					return true;
				} else{
//					System.out.println("DefaultDropLayerComplex: FALSE");
				}
			}
		}
//		System.out.println("DefaultDropLayerComplex: FALSE");
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
	public DockStation getStation(){
		return station;
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}
}
