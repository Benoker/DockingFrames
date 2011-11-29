package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * This layer slim the drop area of {@link ToolbarGroupDockStation}, as it take into
 * account means an area where no dockable can be droped into the station.
 * 
 * @author Herve Guillaume
 */
public class ToolbarSlimDropLayer extends DefaultDropLayer{
	private ToolbarDockStation station;

	/**
	 * Creates a new layer.
	 * 
	 * @param station
	 *            the station which owns this level
	 */
	public ToolbarSlimDropLayer( ToolbarDockStation station ){
		super(station);
		this.station = station;
		setPriority(LayerPriority.BASE);
	}

	@Override
	public boolean contains( int x, int y ){
		if (super.contains(x, y)){
			Rectangle stationArea = station.getComponent().getBounds();
			Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			int size = station.getLateralNodropZoneSize();
			if (station.getOrientation() == Orientation.VERTICAL){
				if (mouseCoord.x > (stationArea.getX() + size)
						&& mouseCoord.x < (stationArea.getMaxX() - size)){
					return true;
				} else{
					return false;
				}
			} else{
				if (mouseCoord.y > (stationArea.getY() + size)
						&& mouseCoord.y < (stationArea.getMaxY() - size)){
					return true;
				} else{
					return false;
				}
			}
		} else{
			return false;
		}
	}

}
