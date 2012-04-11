package bibliothek.gui.dock.station.toolbar.group;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.toolbar.layout.ToolbarGridLayoutManager;

/**
 * This class contains and computes information about a drag and drop action.
 * Especially, where the {@link Dockable} should be inserted into which
 * {@link DockStation}
 * 
 * @author Herve Guillaume
 * @param <S>
 *            the kind of station using this {@link ToolbarGroupDropInfo}
 */
public abstract class ToolbarGroupDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private final Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private final ToolbarGroupDockStation stationHost;
	/** Location of the mouse */
	private final int mouseX, mouseY;
	/** closest dockable beneath the mouse with regards to the mouse coordinates */
	private Dockable dockableBeneathMouse = null;
	/**
	 * closest side of the the closest component with regards to the mouse
	 * coordinates
	 */
	private Position sideDockableBeneathMouse = null;
	/**
	 * Position of the drag dockable with regards to the closest component above
	 * the mouse
	 */
	private Position dragDockablePosition = null;

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
	public ToolbarGroupDropInfo( Dockable dockable,
			ToolbarGroupDockStation stationHost, int mouseX, int mouseY ){
		// System.out.println(this.toString()
		// + "## new ToolbarComplexDropInfo ## ");
		dragDockable = dockable;
		this.stationHost = stationHost;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	public Dockable getItem(){
		return dragDockable;
	}

	@Override
	public ToolbarGroupDockStation getTarget(){
		return stationHost;
	}

	@Override
	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	@Override
	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}

	/**
	 * Gets the <code>Dockable</code> beneath the mouse.
	 * 
	 * @return the dockable and <code>null</code> if there's no dockable
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
	 * return {@link Position#NORTH})
	 * 
	 * @return the closest side
	 */
	public Position getSideDockableBeneathMouse(){
		if (sideDockableBeneathMouse == null){
			sideDockableBeneathMouse = computeSideDockableBeneathMouse();
		}
		return sideDockableBeneathMouse;

	}

	/**
	 * Gets the relative position between: the initial position of the dockable
	 * and the dockable beneath the mouse. The relative position is computed
	 * either on horizontal or vertical axis.
	 * 
	 * @return the relative position (if the drag dockable and the dockable
	 *         beneath mouse are the same, return {@link Position#CENTER})
	 */
	public Position getItemPositionVSBeneathDockable(){
		if (dragDockablePosition == null){
			dragDockablePosition = computeItemPositionVSBeneathDockable();
		}
		return computeItemPositionVSBeneathDockable();
	}

	/**
	 * Computes the closest dockable beneath the mouse (euclidean distance)
	 * 
	 * @return the dockable and <code>null</code> if there's no dockable
	 */
	private Dockable computeDockableBeneathMouse(){
		System.out.println("computeDockableBeneathMouse");
		// Notes: We d'ont take into account the distance from mouse to the
		// center because is not a valid cue to determine which dockable is the
		// closest. Indeed, imagine a rectangular dockable thin ant tall. At the
		// same time, the center can be far and the mouse close of one side.
		final ToolbarGridLayoutManager<StationChildHandle> layout = stationHost
				.getLayoutManager();
		final int columnCount = stationHost.columnCount();
		// distances and closer dockable
		double currentDistance = Double.MAX_VALUE;
		double generalMinDistance = Double.MAX_VALUE;
		Dockable closestDockable = null;
		Point corner = null;
		for (int column = 0; column < columnCount; column++){
			for (int line = 0; line < stationHost.lineCount(column); line++){
				Rectangle bounds = layout.getScreenBounds(column, line);
				// the four corners
				final Point[] fourCorners = new Point[4];
				fourCorners[0] = new Point(bounds.x, bounds.y);
				fourCorners[1] = new Point((int) bounds.getMaxX(), bounds.y);
				fourCorners[2] = new Point((int) bounds.getMaxX(),
						(int) bounds.getMaxY());
				fourCorners[3] = new Point(bounds.x, (int) bounds.getMaxY());
				for (int i = 0; i < fourCorners.length; i++){
					currentDistance = Point2D.distance(fourCorners[i].getX(),
							fourCorners[i].getY(), mouseX, mouseY);
					if (currentDistance < generalMinDistance){
						corner = new Point((int) fourCorners[i].getX(), (int) fourCorners[i].getY());
						generalMinDistance = currentDistance;
						closestDockable = stationHost.getDockable(column, line);
					}
				}

			}
		}
		System.out.print("closest corner: ");
		System.out.println(corner.x + " / " + corner.y);
		System.out.println("Closest component: " + closestDockable.getComponent());
		return closestDockable;
	}


	/**
	 * Computes the closest <code>side</code> of the dockable beneath mouse.
	 * 
	 * @return the closest side, null if there's no dockable beneath mouse
	 */
	private Position computeSideDockableBeneathMouse(){
		// mouse coordinates on the screen
		final Point mouseCoordinate = new Point(mouseX, mouseY);
		// the dockable the closest of the mouse
		final Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}

		// we determine the boundaries of the column which contain the dockable
		// beneath mouse. This bounds are in the frame of reference of what?
		final ToolbarGridLayoutManager<StationChildHandle> layout = stationHost
				.getLayoutManager();
		final Rectangle bounds = layout.getBounds(stationHost
				.column(dockableBeneathMouse));
		// System.out.println("BOUNDS: " + bounds);
		// we translate this rectangle in the screen coordinates
		Point upperLeft = new Point(bounds.x, bounds.y);
		ToolbarColumnModel<StationChildHandle> columnModel = stationHost
				.getColumnModel();
		ToolbarColumn<StationChildHandle> column = columnModel
				.getColumn(dockableBeneathMouse);
		SwingUtilities.convertPointToScreen(upperLeft, column.getDockable(0)
				.getComponent());
		Rectangle boundsTranslate = new Rectangle(upperLeft.x, upperLeft.y,
				bounds.width, bounds.height);
		System.out.println("BOUNDS translate: " + boundsTranslate);
		// System.out.println("dockableBeneathMouse: " + dockableBeneathMouse);
		// we define a small rectangle with same proportions
		final double ratio = boundsTranslate.getHeight()
				/ boundsTranslate.getWidth();
		final Rectangle2D.Double rec = new Rectangle2D.Double();
		if (ratio <= 1){
			rec.setFrameFromCenter(boundsTranslate.getCenterX(),
					boundsTranslate.getCenterY(), boundsTranslate.getCenterX()
							+ (0.5 / ratio), boundsTranslate.getCenterY() + 0.5);
		} else{
			rec.setFrameFromCenter(boundsTranslate.getCenterX(),
					boundsTranslate.getCenterY(),
					boundsTranslate.getCenterX() + 0.5,
					boundsTranslate.getCenterY() + (0.5 * ratio));
		}
		// System.out.println("Ratio: " + ratio);
		// System.out.println("CenterX: " + (boundsTranslate.getCenterX()
		// - ((2 / ratio) / 2)));
		// System.out.println("CenterY: " + (boundsTranslate.getCenterY() - 1));
		// System.out.println("CornerX: " + boundsTranslate.getCenterX() + (0.5
		// /ratio));
		// System.out.println("CornerY: " + (boundsTranslate.getCenterY() +
		// 0.5));
		// System.out.println("Rectangle: " + rec.getMaxX());
		final Point2D.Double[] fourCorners = new Point2D.Double[4];
		fourCorners[0] = new Point2D.Double(rec.x, rec.y);
		fourCorners[1] = new Point2D.Double(rec.getMaxX(), rec.y);
		fourCorners[2] = new Point2D.Double(rec.getMaxX(), rec.getMaxY());
		fourCorners[3] = new Point2D.Double(rec.x, rec.getMaxY());
		final Line2D.Double mouseToCenter = new Line2D.Double(
				mouseCoordinate.x, mouseCoordinate.y, rec.getCenterX(),
				rec.getCenterY());
		// System.out.println("LINE Mouse to Center: " +
		// mouseToCenter.toString());
		for (final Position pos : Position.values()){
			if (pos == Position.CENTER){
				break;
			}
			final Line2D.Double line = new Line2D.Double(
					fourCorners[pos.ordinal()].x, fourCorners[pos.ordinal()].y,
					fourCorners[(pos.ordinal() + 1) % 4].x,
					fourCorners[(pos.ordinal() + 1) % 4].y);
			// System.out.println("LINE Side: Position => " + pos + " / "
			// + line.x1 + " / " + line.y1 + " / " + line.x2 + " / " + line.y2);
			if (line.intersectsLine(mouseToCenter)){
				return pos;
			}
		}
		return null;
	}

	/**
	 * Computes the relative position between: the initial position of the
	 * dockable and the dockable beneath the mouse. The relative position is
	 * computed either on horizontal or vertical axis.
	 * 
	 * @return the relative position (if the drag dockable and the dockable
	 *         beneath mouse are the same, return {@link Position#CENTER})
	 */
	private Position computeItemPositionVSBeneathDockable(){
		final Point coordDockableDragged = getItem().getComponent()
				.getLocation();
		if ((getDockableBeneathMouse() != null)
				&& (getSideDockableBeneathMouse() != null)){
			final Point coordDockableBeneathMouse = getDockableBeneathMouse()
					.getComponent().getLocation();
			// The dockable is now in the frame of reference of the dockable
			// beneath mouse
			SwingUtilities.convertPointFromScreen(coordDockableDragged,
					getDockableBeneathMouse().getComponent());
			if (getItem() == getDockableBeneathMouse()){
				return Position.CENTER;
			} else{
				Orientation axis;
				if ((getSideDockableBeneathMouse() == Position.EAST)
						|| (getSideDockableBeneathMouse() == Position.WEST)){
					axis = Orientation.HORIZONTAL;
				} else{
					axis = Orientation.VERTICAL;
				}
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
				+ Integer.toHexString(hashCode());
	}

	/**
	 * Returns a string describing field values
	 * 
	 * @return string describing fields
	 */
	public String toSummaryString(){
		final String ln = System.getProperty("line.separator");
		return "	=> Drag dockable: " + getItem() + ln + "	=> Station target: "
				+ getTarget() + ln + "	=> Dockable beneath mouse:"
				+ getDockableBeneathMouse() + ln + "	=> Closest side:"
				+ getSideDockableBeneathMouse() + ln
				+ "	=> Drag dockable VS dockable beneath mouse: "
				+ getItemPositionVSBeneathDockable();
	}
}