package bibliothek.gui.dock.station.toolbar.layer;

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
 * Describes the area around a {@link ToolbarGroupDockStation} where the user
 * can drop a {@link Dockable} into.
 * 
 * @author Herve Guillaume
 */
public class ToolbarGroupOuterLayer extends ToolbarGroupInnerLayer{
	/**
	 * Creates a new <code>SideSnapDropLayerComplex</code>
	 * 
	 * @param station
	 *            the owner of this level
	 */
	public ToolbarGroupOuterLayer( ToolbarGroupDockStation station ){
		super( station );
		setPriority( LayerPriority.OUTSIDE_LOW );
	}

	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		return child;
	}

	@Override
	public boolean contains( int x, int y ){
		ToolbarGroupDockStation station = getStation();
		
		if (!station.isAllowSideSnap()){
			return false;
		}
		if (isComponentContain(x, y)){
			return false;
		}

		final int size = station.getBorderSideSnapSize();

		if (station.columnCount() == 0){
			// if there's no dockable inside the station, the shape of the snap
			// layer is computed with regards to the station component
			Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			Rectangle rec = getComponent().getBounds();
			Rectangle recSnap = new Rectangle(rec.x - size, rec.y - size, rec.width + (size * 2), rec.height + (size * 2));
			return recSnap.contains(mouseCoord);

		} else{
			final Point mouseCoord = new Point(x, y);
			SwingUtilities.convertPointFromScreen(mouseCoord, getComponent());
			
			final ToolbarGridLayoutManager<StationChildHandle> layout = station.getLayoutManager();
			
			int count = station.columnCount();
			
			// check if the point is *inside* a child
			for( int i = 0; i < count; i++ ){
				Rectangle bound = layout.getBounds( i );
				if( isNear( bound, size, mouseCoord )){
					return true;
				}
			}
			
			// check if the point is *between* two children
			for( int i = 0; i <= count; i++ ){
				Rectangle bound = layout.getGapBounds( i );
				if( isNear( bound, size, mouseCoord )){
					return true;
				}
			}
			
			return false;
		}
	}
	
	private boolean isNear( Rectangle boundaries, int size, Point location ){
		if( boundaries.contains( location )){
			return false;
		}
		
		int dx = Math.min( Math.abs( boundaries.x - location.x ), Math.abs( location.x - boundaries.x - boundaries.width ));
		int dy = Math.min( Math.abs( boundaries.y - location.y ), Math.abs( location.y - boundaries.y - boundaries.height ));
		if( dx <= size ){
			return dy <= size || (boundaries.y <= location.y && boundaries.y + boundaries.height >= location.y);
		}
		else if( dy <= size ){
			return dx <= size || (boundaries.x <= location.x && boundaries.x + boundaries.width >= location.x);
		}
		return false;
	}

	/**
	 * Tells if the mouse is inside the station
	 * @param x x mouse position in screen coordinates
	 * @param y y mouse position in screen coordinates
	 * @return true if the mouse is inside the station
	 */
	private boolean isComponentContain( int x, int y ){
		return super.contains( x, y );
	}
}
