package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * Information where to insert a {@link Dockable} into a
 * {@link ToolbarGroupDockStation} or a Toolbar
 * {@link ToolbarContainerDockStation}
 * 
 * @author Herve Guillaume
 */
public class ToolbarContainerDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to the original dock parent of the dockable!)
	 */
	private ToolbarContainerDockStation stationHost;
	/** the drag dockable will be insert inside this {@link Dockable}s */
	private ArrayList<Dockable> associateToolbars;
	/** Location of the mouse */
	public int mouseX, mouseY;
	/** index of the closest component with regards to the mouse coordinates */
	private int index = -1;
	/** The area below the mouse */
	private Position area;
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
	public ToolbarContainerDropInfo( Dockable dockable,
			ToolbarContainerDockStation stationHost,
			ArrayList<Dockable> associateToolbars, Position area, int mouseX,
			int mouseY ){
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		this.associateToolbars = associateToolbars;
		this.area = area;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	public Dockable getItem(){
		return dragDockable;
	}

	public DockStation getTarget(){
		return stationHost;
	}

	public void destroy(){
		// nothing to do
	}
	
	public void draw(){
		// TODO
	}
	
	public void execute(){
		stationHost.drop(this);
	}
	
	public CombinerTarget getCombination(){
		// not supported by this kind of station
		return null;
	}

	public DisplayerCombinerTarget getDisplayerCombination(){
		// not supported by this kind of station
		return null;
	}

	public boolean isMove(){
		return getItem().getDockParent() == getTarget() & getItem().getA;
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
	 * Gets the closest <code>side</code> of the component beneath the mouse.
	 * Example: if the mouse is over a button, near the top of the button, this
	 * return NORTH position
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
	 * Gets the relative position of drag dockable with the closest dockable
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
	 * Gets the {@link Position} which will be dropped or moved on the station.
	 * 
	 * @return
	 */
	public Position getArea(){
		return this.area;
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
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}
	
//	/**
//	 * compute the <code>index</code> of the component beneath the mouse
//	 * 
//	 * @param list
//	 *            list of the dockables in the middle of which the drag dockable
//	 *            will be inserted
//	 * @param mouseX
//	 *            position X of the mouse
//	 * @param mouseY
//	 *            position Y of the mouse
//	 * @param orientation
//	 *            orientation of the dockables
//	 * @param reference
//	 *            reference point used to compute relative position of the
//	 *            dockables
//	 * @return the index
//	 */
//	public int computeIndex( ArrayList<Dockable> list, int mouseX, int mouseY,
//			Orientation orientation, ReferencePoint reference ){
//		if (area != null){
//			int dockableCount = list.size();
//			Point mousePoint = new Point(this.mouseX, this.mouseY);
//
//			SwingUtilities.convertPointFromScreen(mousePoint,
//					stationHost.getComponent());
//			switch (reference) {
//			case UPPERLEFT:
//				for (int i = dockableCount - 1; i > -1; i--){
//					Point componentPoint = new Point((int) stationHost
//							.getDockable(i).getComponent().getBounds()
//							.getMinX(), (int) stationHost.getDockable(i)
//							.getComponent().getBounds().getMinY());
//					switch (orientation) {
//					case VERTICAL:
//						if (mousePoint.getY() > componentPoint.getY()){
//							return i + 1;
//						}
//						break;
//					case HORIZONTAL:
//						if (mousePoint.getX() > componentPoint.getX()){
//							return i + 1;
//						}
//						break;
//					}
//				}
//			case BOTTOMRIGHT:
//				for (int i = dockableCount - 1; i > -1; i--){
//					Point componentPoint = new Point((int) stationHost
//							.getDockable(i).getComponent().getBounds()
//							.getMaxX(), (int) stationHost.getDockable(i)
//							.getComponent().getBounds().getMaxY());
//					switch (orientation) {
//					case VERTICAL:
//						if (mousePoint.getY() > componentPoint.getY()){
//							return i + 1;
//						}
//						break;
//					case HORIZONTAL:
//						if (mousePoint.getX() > componentPoint.getX()){
//							return i + 1;
//						}
//						break;
//					}
//				}
//			}
//			return 0;
//		} else{
//			return -1;
//		}
//	}
	
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

}
