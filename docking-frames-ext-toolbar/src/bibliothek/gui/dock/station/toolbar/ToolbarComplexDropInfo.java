package bibliothek.gui.dock.station.toolbar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
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
public abstract class ToolbarComplexDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private ToolbarGroupDockStation stationHost;
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
	 * Position of the drag dockable with regards to the closest component above
	 * the mouse
	 */
	private Position dragDockablePosition;

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
	public ToolbarComplexDropInfo( Dockable dockable,
			ToolbarGroupDockStation stationHost, int mouseX, int mouseY ){
		// System.out.println(this.toString()
		// + "## new ToolbarComplexDropInfo ## ");
		this.dragDockable = dockable;
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
	public abstract void destroy();

	// enable this ToolbarDropInfo to draw some markings on the stationHost
	@Override
	public abstract void draw();

	@Override
	public abstract void execute();

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
		// Notes: the distance from mouse to the center is not a valid cue to
		// determine which dockable is the closest. Indeed, imagine a
		// rectangular dockable with a very big height. The center can be far
		// and the mouse close of one side at the same side.
		final int dockableCount = stationHost.getDockableCount();
		if (dockableCount <= 0){
			return null;
		}
		// distances and closer dockable
		double currentDistance = Double.MAX_VALUE;
		double generalMinDistance = Double.MAX_VALUE;
		Dockable currentDockable = stationHost.getDockable(0), closestDockable = stationHost
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
				SwingUtilities.convertPointToScreen(fourCorners[j], currentDockable.getComponent());
				currentDistance = Point2D.distance(fourCorners[j].getX(),
						fourCorners[j].getY(), mouseX, mouseY);
				if (currentDistance < generalMinDistance){
					generalMinDistance = currentDistance;
					closestDockable = currentDockable;
				}
			}
		}
		return closestDockable;
	}

	// /**
	// * Computes the closest <code>side</code> of the dockable beneath mouse.
	// *
	// * @return the closest side, null if there's no dockable beneath mouse
	// */
	// private Position computeSideDockableBeneathMouse(){
	// // the dockable the closest of the mouse
	// Dockable dockableBeneathMouse = getDockableBeneathMouse();
	// if (dockableBeneathMouse == null){
	// return null;
	// }
	// Rectangle bounds = dockableBeneathMouse.getComponent().getBounds();
	//
	// // mouse coordinates in the frame of reference of the component beneath
	// // mouse
	// Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
	// SwingUtilities.convertPointFromScreen(mouseCoordinate,
	// dockableBeneathMouse.getComponent());
	// // 4 corners in this order: upper left corner, upper right corner,
	// // bottom left corner, bottom right corner
	// final Point[] fourCorners = new Point[4];
	// fourCorners[0] = new Point(bounds.x, bounds.y);
	// fourCorners[1] = new Point((int) bounds.getMaxX(), bounds.y);
	// fourCorners[2] = new Point(bounds.x, (int) bounds.getMaxY());
	// fourCorners[3] = new Point((int) bounds.getMaxX(),
	// (int) bounds.getMaxY());
	// // looking for the closest side. For each segment of the dockable we
	// // compute the cumulative distance between the two corners and the
	// // mouse. The side corresponds to the segment with the smallest
	// // cumulative distance.
	// double minDist = Double.MAX_VALUE;
	// Position sideWin = null;
	// for (Position pos : Position.values()){
	// if (pos == Position.CENTER){
	// break;
	// }
	// double currentDist =
	// (mouseCoordinate.distance(fourCorners[pos.ordinal()]))
	// + (mouseCoordinate
	// .distance(fourCorners[(pos.ordinal() + 1) % 4]));
	// System.out.println(pos);
	// System.out.println("minDist: " + minDist + " / " + currentDist);
	// if (currentDist < minDist){
	// minDist = currentDist;
	// sideWin = pos;
	// }
	// }
	// System.out.println("WINNER: " + sideWin);
	// return sideWin;
	// }

	/**
	 * Computes the closest <code>side</code> of the dockable beneath mouse.
	 * 
	 * @return the closest side, null if there's no dockable beneath mouse
	 */
	private Position computeSideDockableBeneathMouse(){
		// mouse coordinates in the frame of reference of the component beneath
		// mouse
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		SwingUtilities.convertPointFromScreen(mouseCoordinate,
				dockableBeneathMouse.getComponent());
		// the dockable the closest of the mouse
		Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		Rectangle bounds = dockableBeneathMouse.getComponent().getBounds();
		// we "draw" a small rectangle with same proportions
		double ratio = bounds.getHeight() / bounds.getWidth();
		Rectangle2D.Double rec = new Rectangle2D.Double();
		rec.setFrameFromCenter(bounds.getCenterX(), bounds.getCenterY(),
				rec.getCenterX() - 1, rec.getCenterY() - (1 * ratio));
		// ... and we look if the lin from mouse to the center of the rectangle
		// intersects one of the four side of the rectangle
		final Point[] fourCorners = new Point[4];
		fourCorners[0] = new Point((int) rec.x, (int) rec.y);
		fourCorners[1] = new Point((int) rec.getMaxX(), (int) rec.y);
		fourCorners[2] = new Point((int) rec.getMaxX(), (int) rec.getMaxY());
		fourCorners[3] = new Point((int) rec.x, (int) rec.getMaxY());
		Line2D.Double mouseToCenter = new Line2D.Double(mouseCoordinate.x,
				mouseCoordinate.y, bounds.getCenterX(), bounds.getCenterY());
		for (Position pos : Position.values()){
			if (pos == Position.CENTER){
				break;
			}
			Line2D.Double line = new Line2D.Double(
					fourCorners[pos.ordinal()].x, fourCorners[pos.ordinal()].y,
					fourCorners[(pos.ordinal() + 1) % 4].x,
					fourCorners[(pos.ordinal() + 1) % 4].y);
			if (line.intersectsLine(mouseToCenter)){
				return pos;
			}
		}
		return Position.SOUTH;
	}

	// // we imagine one circle centered on the center of the bounds
	// // and with a max radius. The four corner of the bounds define four
	// arc
	// // area.
	// // The closest side is determined accordingly with this area.
	// // Angle between of the segment defined by the center and the upper
	// // right corner (The result will be in the range -180 to +180
	// degrees,
	// // measured anti-clockwise from East)
	// double halfEastAngle = Math.toDegrees(Math.atan2(
	// bounds.getY() - bounds.getCenterY(),
	// bounds.getMaxX() - bounds.getCenterX()));
	// System.out.println("Angle: " + halfEastAngle);
	// // four arcs
	// Arc2D arc = new Arc2D.Double();
	// arc.setArcByCenter(bounds.getCenterX(), bounds.getCenterY(),
	// Double.MAX_VALUE, -halfEastAngle, halfEastAngle, Arc2D.PIE);
	// if (arc.contains(mouseCoordinate)){
	// System.out.println(Position.EAST);
	// return Position.EAST;
	// }
	// double northAngle = halfEastAngle + ((90 - halfEastAngle) * 2);
	// arc.setArcByCenter(bounds.getCenterX(), bounds.getCenterY(),
	// Double.MAX_VALUE, halfEastAngle, northAngle, Arc2D.PIE);
	// double westAngle = northAngle + (halfEastAngle);
	// if (arc.contains(mouseCoordinate)){
	// System.out.println(Position.NORTH);
	// return Position.NORTH;
	// }
	// arc.setArcByCenter(bounds.getCenterX(), bounds.getCenterY(),
	// Double.MAX_VALUE, northAngle, westAngle, Arc2D.PIE);
	// if (arc.contains(mouseCoordinate)){
	// System.out.println(Position.WEST);
	// return Position.WEST;
	// }
	// System.out.println(Position.SOUTH);
	// return Position.SOUTH;

	// // Rectangle2D.Double maxRectangle = new Rectangle2D.Double();
	// // maxRectangle.setFrameFromCenter(bounds.getCenterX(),
	// // bounds.getCenterY(), bounds.getCenterX() - (Double.MAX_VALUE),
	// // bounds.getCenterY() - (Double.MAX_VALUE));
	// // Ellipse2D ellipse = new Ellipse2D.Double();
	// // ellipse.setFrameFromCenter(bounds.getCenterX(),
	// bounds.getCenterY(),
	// // Double.MAX_VALUE, Double.MAX_VALUE,
	// // );
	//

	// // 4 corners in this order: upper left corner, upper right corner,
	// // bottom left corner, bottom right corner
	// final Point[] fourCorners = new Point[4];
	// fourCorners[0] = new Point(bounds.x, bounds.y);
	// fourCorners[1] = new Point((int) bounds.getMaxX(), bounds.y);
	// fourCorners[2] = new Point(bounds.x, (int) bounds.getMaxY());
	// fourCorners[3] = new Point((int) bounds.getMaxX(),
	// (int) bounds.getMaxY());
	// // looking for the closest side. For each segment of the dockable we
	// // compute the cumulative distance between the two corners and the
	// // mouse. The side corresponds to the segment with the smallest
	// // cumulative distance.
	// double minDist = Double.MAX_VALUE;
	// Position sideWin = null;
	// for (Position pos : Position.values()){
	// if (pos == Position.CENTER){
	// break;
	// }
	// double currentDist = (mouseCoordinate.distance(fourCorners[pos
	// .ordinal()]))
	// + (mouseCoordinate
	// .distance(fourCorners[(pos.ordinal() + 1) % 4]));
	// System.out.println(pos);
	// System.out.println("minDist: " + minDist + " / " + currentDist);
	// if (currentDist < minDist){
	// minDist = currentDist;
	// sideWin = pos;
	// }
	// }
	// System.out.println("WINNER: " + sideWin);
	// return sideWin;

	/**
	 * Computes the relative position between: the initial position of the
	 * dockable and the dockable beneath the mouse. The relative position is
	 * computed either on horizontal or vertical axis.
	 * 
	 * @return the relative position (if the drag dockable and the dockable
	 *         beneath mouse are the same, return {@link Position#CENTER})
	 */
	private Position computeItemPositionVSBeneathDockable(){
		Point coordDockableDragged = getItem().getComponent().getLocation();
		if (getDockableBeneathMouse() != null
				&& getSideDockableBeneathMouse() != null){
			Point coordDockableBeneathMouse = getDockableBeneathMouse()
					.getComponent().getLocation();
			// The dockable is now in the frame of reference of the dockable
			// beneath mouse
			SwingUtilities.convertPointFromScreen(coordDockableDragged,
					getDockableBeneathMouse().getComponent());
			if (getItem() == getDockableBeneathMouse()){
				return Position.CENTER;
			} else{
				Orientation axis;
				if (getSideDockableBeneathMouse() == Position.EAST
						|| getSideDockableBeneathMouse() == Position.WEST){
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
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Returns a string describing field values
	 * 
	 * @return string describing fields
	 */
	public String toSummaryString(){
		String ln = System.getProperty("line.separator");
		return "	=> Drag dockable: " + getItem() + ln + "	=> Station target: "
				+ getTarget() + ln + "	=> Dockable beneath mouse:"
				+ getDockableBeneathMouse() + ln + "	=> Closest side:"
				+ this.getSideDockableBeneathMouse() + ln
				+ "	=> Drag dockable VS dockable beneath mouse: "
				+ this.getItemPositionVSBeneathDockable();
	}
}