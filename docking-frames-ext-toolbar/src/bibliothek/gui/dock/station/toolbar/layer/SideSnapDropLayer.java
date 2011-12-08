package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * Describes the area around a {@link ToolbarDockStation} where the user can
 * drop a {@link Dockable} into.
 * 
 * @author Herve Guillaume
 */
public class SideSnapDropLayer implements DockStationDropLayer{
	private ToolbarGroupDockStation station;
	private LayerPriority priority = LayerPriority.OUTSIDE_LOW;

	/**
	 * Creates a new layer
	 * 
	 * @param station
	 *            the owner of this level
	 */
	public SideSnapDropLayer( ToolbarGroupDockStation station ){
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
		if (!station.isAllowSideSnap()){
			return false;
		}
		Point point = new Point(x, y);
		SwingUtilities.convertPointFromScreen(point, getComponent());
		Rectangle bounds = getComponent().getBounds();
		if (bounds.contains(point)){
			// if the mouse in inside component, so it is not inside the snap
			// extended zone
			return false;
		}
		int size = station.getBorderSideSnapSize();
		Rectangle extendedBounds = new Rectangle();
		extendedBounds.setBounds(bounds.x - size, bounds.y - size, bounds.width
				+ (size * 2), bounds.height + (size * 2));

		// DEBUG:
		// int deltaX = Math.min(Math.abs(point.x),
		// Math.abs(point.x - bounds.width));
		// int deltaY = Math.min(Math.abs(point.y),
		// Math.abs(point.y - bounds.height));
		// System.out.println("Mouse : " + point.x + " / deltaX :" + deltaX
		// + " / deltaY :" + deltaY);
		// if (extendedBounds.contains(point)){
		// System.out.println("TRUETRUETRUETRUE");
		// } else{
		// System.out.println("FALSEFALSEFALSE");
		// }

		return extendedBounds.contains(point);

	}
}
