package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
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
public abstract class ToolbarDropInfo<S extends OrientedDockStation>
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
	public ToolbarDropInfo( Dockable dockable, S stationHost, int mouseX,
			int mouseY ){
		System.out.println(this.toString() + "## new ToolbarDropInfo ## ");
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
	public S getTarget(){
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
	public Position getItemPositionVSBeneathDockable(){
		if (dragDockablePosition == null){
			dragDockablePosition = this.computeItemPositionVSBeneathDockable();
		}
		return dragDockablePosition;

	}

	/**
	 * compute the closest <code>side</code> of the component beneath the mouse
	 * 
	 * @return the side
	 */
	private Position computeSideDockableBeneathMouse(){
		// the dockable the closest of the mouse
		Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		// mouse coordinate
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		switch (stationHost.getOrientation()) {
		case VERTICAL:
			// The mouse is now in the frame of reference of the area beneath
			// mouse
			SwingUtilities.convertPointFromScreen(mouseCoordinate,
					dockableBeneathMouse.getComponent());
			double middleY = (dockableBeneathMouse.getComponent().getBounds()
					.getMinY() + dockableBeneathMouse.getComponent()
					.getBounds().getMaxY()) / 2.0;
			if (Math.abs(mouseCoordinate.getY()) <= middleY){
				return Position.NORTH;
			} else{
				return Position.SOUTH;
			}
		case HORIZONTAL:
			// The mouse is now in the frame of reference of the area beneath
			// mouse
			SwingUtilities.convertPointFromScreen(mouseCoordinate,
					dockableBeneathMouse.getComponent());
			double middleX = (dockableBeneathMouse.getComponent().getBounds()
					.getMinX() + dockableBeneathMouse.getComponent()
					.getBounds().getMaxX()) / 2.0;
			System.out.println(Math.abs(mouseCoordinate.getX()));
			System.out.println(middleX + " / " + mouseCoordinate.getX());

			if (Math.abs(mouseCoordinate.getX()) <= middleX){
				return Position.WEST;
			} else{
				return Position.EAST;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * compute the <code>index</code> of the component beneath the mouse
	 * 
	 * @return the index
	 */
	private Dockable computeDockableBeneathMouse(){
		// if there's no dockable, then the index is 0
		final int dockableCount = stationHost.getDockableCount();
		if (dockableCount <= 0){
			return null;
		}
		// mouse coordinates
		final Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		// variables for loop search
		double formerDistance;
		OrientedDockStation orientedStation = stationHost;
		Point middleCoordinate = new Point((int) stationHost.getDockable(0)
				.getComponent().getBounds().getCenterX(), (int) stationHost
				.getDockable(0).getComponent().getBounds().getCenterY());
		SwingUtilities.convertPointToScreen(middleCoordinate, stationHost
				.getDockable(0).getComponent());
		int index = 0;
		switch (orientedStation.getOrientation()) {
		case VERTICAL:
			// loop on dockables too see which of them is closer of the mouse
			formerDistance = Math.abs(mouseCoordinate.getY()
					- middleCoordinate.y);
			for (int i = 1; i < dockableCount; i++){
				middleCoordinate = new Point((int) stationHost.getDockable(i)
						.getComponent().getBounds().getCenterX(),
						(int) stationHost.getDockable(i).getComponent()
								.getBounds().getCenterY());
				SwingUtilities.convertPointToScreen(middleCoordinate,
						stationHost.getDockable(i).getComponent());
				if (Math.abs(mouseCoordinate.getY() - middleCoordinate.y) < formerDistance){
					// the mouse is closer of the former dockable
					index = i;
				}
				formerDistance = Math.abs(mouseCoordinate.getY()
						- middleCoordinate.y);
			}
			return stationHost.getDockable(index);
		case HORIZONTAL:
			formerDistance = Math.abs(mouseCoordinate.getX()
					- middleCoordinate.x);
			for (int i = 1; i < dockableCount; i++){
				middleCoordinate = new Point((int) stationHost.getDockable(i)
						.getComponent().getBounds().getCenterX(),
						(int) stationHost.getDockable(i).getComponent()
								.getBounds().getCenterY());
				SwingUtilities.convertPointToScreen(middleCoordinate,
						stationHost.getDockable(i).getComponent());
				if (Math.abs(mouseCoordinate.getX() - middleCoordinate.x) < formerDistance){
					// the mouse is closer of the former dockable
					index = i;
				}
				formerDistance = Math.abs(mouseCoordinate.getX()
						- middleCoordinate.x);
			}
			return stationHost.getDockable(index);
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Compute the relative position of drag dockable and the closest dockable
	 * above the mouse
	 * 
	 * @return the position
	 */
	private Position computeItemPositionVSBeneathDockable(){
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
				switch (stationHost.getOrientation()) {
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
				+ this.getItemPositionVSBeneathDockable();
	}
}
