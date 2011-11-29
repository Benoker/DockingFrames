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
 * Describes the area around a {@link ToolbarDockStation} where the user
 * can drop a {@link Dockable} into.
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

	public LayerPriority getPriority(){
		return priority;
	}

	public void setPriority( LayerPriority priority ){
		this.priority = priority;
	}

	public boolean canCompare( DockStationDropLayer level ){
		return false;
	}

	public int compare( DockStationDropLayer level ){
		return 0;
	}

	public Component getComponent(){
		return station.getComponent();
	}

	public DockStation getStation(){
		return station;
	}

	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}

	public boolean contains( int x, int y ){
		if (!station.isAllowSideSnap()){
			return false;
		}
		Point point = new Point(x, y);
		SwingUtilities.convertPointFromScreen(point, getComponent());
		Rectangle bounds = getComponent().getBounds();
		if (bounds.contains(point)){
			System.out.println("CONTAINS:FALSEFALSEFALSEFALSEFALSEFALSEFALSEFALSEFALSE");
			return false;
		}

		int deltaX = Math.min(Math.abs(x), Math.abs(x - bounds.width));
		int deltaY = Math.min(Math.abs(y), Math.abs(y - bounds.height));

		int size = station.getBorderSideSnapSize();
		if (deltaX <= size || deltaY <= size) {
			System.out.println("TRUETRUETRUETRUETRUETRUETRUETRUETRUETRUETRUE");
		} else {
			System.out.println("FALSEFALSEFALSEFALSEFALSEFALSEFALSEFALSEFALSE");
		}
		return deltaX <= size || deltaY <= size;
		
	}
}
