package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * Information where to insert a {@link Dockable} into a
 * {@link ToolbarGroupDockStation} or a {@link ToolbarDockStationFactory}.
 * 
 * @author Herve Guillaume
 * @param <S>
 *            the kind of station using this {@link ToolbarDropInfo}
 */
public abstract class ToolbarDropInfo<S extends DockStation> implements
		StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private S stationHost;
	/** Location of the mouse */
	public int mouseX, mouseY;
	/** index of the closest component with regards to the mouse coordinates */
	private int index = -1;
	/**
	 * closest side of the the closest component with regards to the mouse
	 * coordinates
	 */
	private Position side = null;
	/**
	 * Position of the drag dockable with regards to the closest component above
	 * the mouse
	 */
	private Position dragDockablePosition;

	/**
	 * Constructs a new info.
	 * 
	 * @param station
	 *            the owner of this info
	 * @param dockable
	 *            the {@link Dockable} which will be inserted
	 */
	public ToolbarDropInfo( Dockable dockable, S stationHost, int mouseX,
			int mouseY ){
		System.out.println(this.toString() + "## new ToolbarDropInfo ## ");
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

	public void destroy(){
	}

	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	public void draw(){
		// enable this ToolbarDropInfo to draw some markings on the stationHost
	}

	public boolean isMove(){
		return getItem().getDockParent() == getTarget();
	}

	/**
	 * Gets the <code>index</code> of the component beneath the mouse
	 * 
	 * @return the index
	 */
	public int getIndex(){
		if (index == -1){
			index = computeIndex();
		}
		return index;

	}

	/**
	 * Gets the closest <code>side</code> of the component beneath the mouse
	 * 
	 * @return the side
	 */
	public Position getSide(){
		if (side == null){
			side = computeSide();
		}
		return side;

	}

	/**
	 * Gets the relative position of drag dockable and the closest dockable
	 * above the mouse.
	 * 
	 * @return the position
	 */
	public Position getDragDockablePosition(){
		if (dragDockablePosition == null){
			dragDockablePosition = this.computeDragDockablePosition();
		}
		return dragDockablePosition;

	}

	/**
	 * compute the closest <code>side</code> of the component beneath the mouse
	 * 
	 * @return the side
	 */
	private Position computeSide(){
		// mouse coordinate in the frame of reference of the station
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		Dockable stationDockable = (Dockable) stationHost;
		SwingUtilities.convertPointFromScreen(mouseCoordinate,
				stationDockable.getComponent());
		// the dockable the closest of the mouse
		int index = getIndex();
		// compute if the mouse is on top or bottom this dockable
		OrientedDockStation orientedStation = (OrientedDockStation) stationHost;
		switch (orientedStation.getOrientation()) {
		case VERTICAL:
			double middleY = (stationHost.getDockable(index).getComponent()
					.getBounds().getMinY() + stationHost.getDockable(index)
					.getComponent().getBounds().getMaxY()) / 2.0;
			if (Math.abs(mouseCoordinate.getY()) < middleY){
				return Position.NORTH;
			} else{
				return Position.SOUTH;
			}
		case HORIZONTAL:
			double middleX = (stationHost.getDockable(index).getComponent()
					.getBounds().getMinX() + stationHost.getDockable(index)
					.getComponent().getBounds().getMaxX()) / 2.0;
			if (Math.abs(mouseCoordinate.getX()) < middleX){
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
	private int computeIndex(){
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		Dockable stationDockable = (Dockable) stationHost;
		SwingUtilities.convertPointFromScreen(mouseCoordinate,
				stationDockable.getComponent());
		double formerDistance;
		int dockableCount = stationHost.getDockableCount();
		OrientedDockStation orientedStation = (OrientedDockStation) stationHost;
		switch (orientedStation.getOrientation()) {
		case VERTICAL:
			// loop on dockables too see which of them is closer of the mouse
			double middleY = (stationHost.getDockable(0).getComponent()
					.getBounds().getMinY() + stationHost.getDockable(0)
					.getComponent().getBounds().getMaxY()) / 2.0;
			formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
			for (int i = 1; i < dockableCount; i++){
				middleY = (stationHost.getDockable(i).getComponent()
						.getBounds().getMinY() + stationHost.getDockable(i)
						.getComponent().getBounds().getMaxY()) / 2.0;
				if (Math.abs(mouseCoordinate.getY() - middleY) >= formerDistance){
					// the mouse is closer of the former dockable
					return i - 1;
				}
				formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
			}
			return dockableCount - 1;
		case HORIZONTAL:
			// loop on dockables too see which of them is closer of the mouse
			double middleX = (stationHost.getDockable(0).getComponent()
					.getBounds().getMinX() + stationHost.getDockable(0)
					.getComponent().getBounds().getMaxX()) / 2.0;
			formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
			for (int i = 1; i < dockableCount; i++){
				middleX = (stationHost.getDockable(i).getComponent()
						.getBounds().getMinX() + stationHost.getDockable(i)
						.getComponent().getBounds().getMaxX()) / 2.0;
				if (Math.abs(mouseCoordinate.getX() - middleX) >= formerDistance){
					// the mouse is closer of the former dockable
					return i - 1;
				}
				formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
			}
			return dockableCount - 1;
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Compute the relative position of drag dockable and the closest dockable
	 * above the mouse
	 * 
	 * @return the position
	 */
	private Position computeDragDockablePosition(){
		int indexOfClosestDockable = this.getIndex();
		if (this.dragDockable == stationHost
				.getDockable(indexOfClosestDockable)){
			return Position.CENTER;
		} else{
			OrientedDockStation orientedStation = (OrientedDockStation) stationHost;
			switch (orientedStation.getOrientation()) {
			case VERTICAL:
				if (dragDockable.getComponent().getBounds().getMinY() < stationHost
						.getDockable(indexOfClosestDockable).getComponent()
						.getBounds().getMinY()){
					return Position.NORTH;
				} else{
					return Position.SOUTH;
				}
			case HORIZONTAL:
				if (dragDockable.getComponent().getBounds().getMinX() < stationHost
						.getDockable(indexOfClosestDockable).getComponent()
						.getBounds().getMinX()){
					return Position.WEST;
				} else{
					return Position.EAST;
				}
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

}
