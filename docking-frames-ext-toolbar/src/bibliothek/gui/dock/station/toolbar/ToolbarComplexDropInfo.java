package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.OrientedDockStation;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * This class contains and computes information about a drag and drop action.
 * Especially, where the {@link Dockable} should be inserted into which
 * {@link DockStation}
 * 
 * @author Herve Guillaume
 * @param <S>
 *            the kind of station using this {@link ToolbarDropInfo}
 */
public abstract class ToolbarComplexDropInfo<S extends OrientedDockStation>
		implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private S stationHost;
	/** Location of the mouse */
	private int mouseX, mouseY;
	/** closest dockable beneath the mouse with regards to the mouse coordinates */
	private Dockable dockableBeneathMouse = null;
	/**
	 * closest side of the the closest component with regards to the mouse
	 * coordinates
	 */
	private Position sideDockableBeneathMouse = null;
	
	/**
	 * Constructs a new info.
	 * 
	 * @param dockable
	 *            the dockable to drop
	 * @param stationHost
	 *            the station where drop the dockable
	 * @param mouseX
	 *            the mouse position on X axis
	 * @param mouseY
	 *            the mouse position on Y axis
	 */
	public ToolbarComplexDropInfo( Dockable dockable, S stationHost,
			int mouseX, int mouseY ){
		System.out.println(this.toString()
				+ "## new ToolbarComplexDropInfo ## ");
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public Dockable getItem(){
		return dragDockable;
	}

	public S getTarget(){
		return stationHost;
	}

	public abstract void destroy();

	// enable this ToolbarDropInfo to draw some markings on the stationHost
	public abstract void draw();

	public abstract void execute();

	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}

	/**
	 * Gets the <code>index</code> of the component beneath the mouse
	 * 
	 * @return the index
	 */
	public Dockable getDockableBeneathMouse(){
		if (dockableBeneathMouse == null){
			dockableBeneathMouse = computeDockableBeneathMouse();
		}
		return dockableBeneathMouse;
	}

	/**
	 * Gets the closest <code>side</code> of the component beneath the mouse.
	 * Example: if the mouse is over a button, near the top of the button, this
	 * return NORTH position
	 * 
	 * @return the side
	 */
	public Position getSideDockableBeneathMouse(){
		if (sideDockableBeneathMouse == null){
			sideDockableBeneathMouse = computeSideDockableBeneathMouse();
		}
		return sideDockableBeneathMouse;

	}

	/**
	 * Gets the relative position of drag dockable with the closest dockable
	 * above the mouse.
	 * 
	 * @return the position
	 */
	public Position getItemPositionVSBeneathDockable( Orientation axis ){
		return computeItemPositionVSBeneathDockable(axis);
	}

	/**
	 * compute the closest dockable beneath the mouse (euclidean distance)
	 * 
	 * @return the dockable and <code>null</code> if there's no dockable
	 */
	private Dockable computeDockableBeneathMouse(){
		final int dockableCount = stationHost.getDockableCount();
		if (dockableCount <= 0){
			return null;
		}
		// distances and closer dockable
		double currentDistance = 0;
		double generalMinDistance = Double.MAX_VALUE;
		Dockable currentDockable = stationHost.getDockable(0), closerDockable = stationHost
				.getDockable(0);
		Rectangle currentBounds = currentDockable.getComponent().getBounds();
		// 4 corners in this order: upper left corner, upper right corner,
		// bottom left corner, bottom right corner
		final Point[] fourCorners = new Point[4];
		fourCorners[0] = new Point(currentBounds.x, currentBounds.y);
		fourCorners[1] = new Point((int) currentBounds.getMaxX(),
				currentBounds.y);
		fourCorners[2] = new Point(currentBounds.x,
				(int) currentBounds.getMaxY());
		fourCorners[3] = new Point((int) currentBounds.getMaxX(),
				(int) currentBounds.getMaxY());
		for (int i = 0; i < fourCorners.length; i++){
			SwingUtilities.convertPointToScreen(fourCorners[i], stationHost
					.getDockable(0).getComponent());
			currentDistance = Point2D.distance(fourCorners[i].getX(),
					fourCorners[i].getY(), mouseX, mouseY);
			if (currentDistance < generalMinDistance){
				generalMinDistance = currentDistance;
				closerDockable = currentDockable;
			}
		}

		for (int i = 1; i < dockableCount; i++){
			currentDockable = stationHost.getDockable(i);
			currentBounds = currentDockable.getComponent().getBounds();
			fourCorners[0] = new Point(currentBounds.x, currentBounds.y);
			fourCorners[1] = new Point((int) currentBounds.getMaxX(),
					currentBounds.y);
			fourCorners[2] = new Point(currentBounds.x,
					(int) currentBounds.getMaxY());
			fourCorners[3] = new Point((int) currentBounds.getMaxX(),
					(int) currentBounds.getMaxY());
			for (int j = 0; j < fourCorners.length; j++){
				SwingUtilities.convertPointToScreen(fourCorners[j], stationHost
						.getDockable(0).getComponent());
				currentDistance = Point2D.distance(fourCorners[j].getX(),
						fourCorners[j].getY(), mouseX, mouseY);
				if (currentDistance < generalMinDistance){
					generalMinDistance = currentDistance;
					closerDockable = currentDockable;
				}
			}
		}
		return closerDockable;
	}

	/**
	 * Computes the closest <code>side</code> of the dockable beneath mouse.
	 * 
	 * @return the closest side, null if there's no dockable beneath mouse
	 */
	private Position computeSideDockableBeneathMouse(){
		// the dockable the closest of the mouse
		Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		Rectangle bounds = dockableBeneathMouse.getComponent().getBounds();

		// mouse coordinates in the frame of reference of the component beneath
		// mouse
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		SwingUtilities.convertPointFromScreen(mouseCoordinate,
				dockableBeneathMouse.getComponent());
		// 4 corners in this order: upper left corner, upper right corner,
		// bottom left corner, bottom right corner
		final Point[] fourCorners = new Point[4];
		fourCorners[0] = new Point(bounds.x, bounds.y);
		fourCorners[1] = new Point((int) bounds.getMaxX(), bounds.y);
		fourCorners[2] = new Point(bounds.x, (int) bounds.getMaxY());
		fourCorners[3] = new Point((int) bounds.getMaxX(),
				(int) bounds.getMaxY());
		// looking for the closest side. For each segment of the dockable we
		// compute the cumulative distance between the two corners and the
		// mouse. The side corresponds to the segment with the smallest
		// cumulative distance.
		double maxDist = Double.MAX_VALUE;
		Position sideWin = null;
		for (Position pos : Position.values()){
			if (pos == Position.CENTER){
				break;
			}
			double dist = (mouseCoordinate.distance(fourCorners[pos.ordinal()]))
					+ (mouseCoordinate
							.distance(fourCorners[(pos.ordinal() + 1) % 4]));
			if (maxDist < dist){
				maxDist = dist;
				sideWin = pos;
			}

		}
		return sideWin;

	}

	/**
	 * Computes the relative position between: the drag dockable original
	 * position and the dockable beneath the mouse. The relative position is
	 * computed either on horizontal or vertical axis.
	 * 
	 * @param axis
	 *            indicates if the relative position is computed on the
	 *            horizontal or the vertical axis
	 * 
	 * @return the relative position (if the drag dockable and the dockable
	 *         beneath mouse are the same, return {@link Position#CENTER})
	 */
	private Position computeItemPositionVSBeneathDockable( Orientation axis ){
		Point coordDockableDragged = getItem().getComponent().getLocation();
		if (getDockableBeneathMouse() != null){
			Point coordDockableBeneathMouse = getDockableBeneathMouse()
					.getComponent().getLocation();
			// The dockable is now in the frame of reference of the dockable
			// beneath mouse
			SwingUtilities.convertPointFromScreen(coordDockableDragged,
					getDockableBeneathMouse().getComponent());
			if (getItem() == getDockableBeneathMouse()){
				return Position.CENTER;
			} else{
				switch (axis) {
				case VERTICAL:
					if (coordDockableDragged.getY() <= coordDockableBeneathMouse
							.getY()){
						return Position.NORTH;
					} else{
						return Position.SOUTH;
					}
				case HORIZONTAL:
					if (coordDockableDragged.getX() <= coordDockableBeneathMouse
							.getX()){
						return Position.EAST;
					} else{
						return Position.WEST;
					}
				}
			}
			throw new IllegalArgumentException();
		} else{
			return null;
		}
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Return a string describing field values
	 * 
	 * @return string describing fields
	 */
	public String toSummaryString(){
		String ln = System.getProperty("line.separator");
		return "	=> Drag dockable: " + getItem() + ln + "	=> Station target: "
				+ getTarget() + ln + "	=> Dockable beneath mouse:"
				+ getDockableBeneathMouse() + ln + "	=> Closest side:"
				+ this.getSideDockableBeneathMouse() + ln
				+ "	=> Drag dockable VS dockable beneath mouse:"
				+ " Horizontal -> " + this.getItemPositionVSBeneathDockable(Orientation.HORIZONTAL)
				+ " / Vertical -> " + this.getItemPositionVSBeneathDockable(Orientation.VERTICAL);
	}
}