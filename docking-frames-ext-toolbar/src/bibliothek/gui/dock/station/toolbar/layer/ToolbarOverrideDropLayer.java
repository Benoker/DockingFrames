package bibliothek.gui.dock.station.toolbar.layer;

import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;

/**
 * Represents the {@link LayerPriority#OVERRIDE_PRECISE override} area of a
 * {@link ToolbarDockStation}, it means an area where no dockable can be droped
 * into the station.
 * 
 * @author Herve Guillaume
 */
public class ToolbarOverrideDropLayer extends DefaultDropLayer{
	private ToolbarDockStation station;

	/**
	 * Creates a new layer.
	 * 
	 * @param station
	 *            the station which owns this level
	 */
	public ToolbarOverrideDropLayer( ToolbarDockStation station ){
		super(station);
		this.station = station;
		setPriority(LayerPriority.OVERRIDE_PRECISE);
	}

	@Override
	public boolean contains( int x, int y ){
		if (super.contains(x, y)){
			Rectangle stationArea = station.getComponent().getBounds();
			Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			int size = station.getInsetsSideOverrideSize();
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
