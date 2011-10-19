package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;

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
	private DockablePlaceholderList<Dockable> associateToolbars;
	/** Location of the mouse */
	public int mouseX, mouseY;
	/** closest dockable beneath the mouse with regards to the mouse coordinates */
	private Dockable dockableBeneathMouse = null;
	/** The area below the mouse */
	private Position areaBeneathMouse;
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
	 * @param station
	 *            the owner of this info
	 * @param dockable
	 *            the {@link Dockable} which will be inserted
	 */
	public ToolbarContainerDropInfo( Dockable dockable,
			ToolbarContainerDockStation stationHost,
			DockablePlaceholderList<Dockable> associateToolbars, Position area, int mouseX,
			int mouseY ){
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		this.associateToolbars = associateToolbars;
		this.areaBeneathMouse = area;
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
	 * Gets the {@link Position} which will be dropped or moved on the station.
	 * 
	 * @return
	 */
	public Position getArea(){
		return this.areaBeneathMouse;
	}

	/**
	 * compute the closest <code>side</code> of the closest component with
	 * regards to the mouse
	 * 
	 * @return the side or null if there's no dockable beneath mouse
	 */
	private Position computeSideDockableBeneathMouse(){
		// the dockable the closest of the mouse
		Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		// mouse coordinate in the frame of reference of the station
		Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
		// This component stands for the panel beneath the mouse. This
		// rectangle will be the frame of reference of component inside this
		// panel.
		Component areaBeneathMouse = stationHost.getPanel(this.getArea());
		// The mouse is now in the frame of reference of the area beneath
		// mouse
		SwingUtilities
				.convertPointFromScreen(mouseCoordinate, areaBeneathMouse);
		// compute if the mouse is on top or bottom this dockable
		switch (stationHost.getOrientation(this.areaBeneathMouse)) {
		case VERTICAL:
			double middleY = (dockableBeneathMouse.getComponent().getBounds()
					.getMinY() + dockableBeneathMouse.getComponent()
					.getBounds().getMaxY()) / 2.0;
			if (Math.abs(mouseCoordinate.getY()) <= middleY){
				return Position.NORTH;
			} else{
				return Position.SOUTH;
			}
		case HORIZONTAL:
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
	 * @return the dockable beneath mouse and null if none
	 */
	private Dockable computeDockableBeneathMouse(){
		if (areaBeneathMouse != null){
			DockablePlaceholderList.Filter<Dockable> associateToolbars = this.associateToolbars.dockables();
			
			int dockableCount = associateToolbars.size();
			if (dockableCount == 0){
				return null;
			}

			Point mouseCoordinate = new Point(this.mouseX, this.mouseY);
			// This component stands for the panel beneath the mouse. This
			// rectangle will be the frame of reference of component inside this
			// panel.
			Component areaBeneathMouse = stationHost.getPanel(this.getArea());
			// The mouse is now in the frame of reference of the area beneath
			// mouse
			SwingUtilities.convertPointFromScreen(mouseCoordinate,
					areaBeneathMouse);
			Component componentBeneathMouse;
			double formerDistance;
			Orientation orientation = stationHost
					.getOrientation(this.getArea());
			switch (orientation) {
			case VERTICAL:
				componentBeneathMouse = associateToolbars.get(0).getComponent();
				double middleY = (componentBeneathMouse.getBounds().getMinY() + componentBeneathMouse
						.getBounds().getMaxY()) / 2.0;
				formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
				// loop on dockables too see which of them is closer of the
				// mouse
				for (int i = 1; i < dockableCount; i++){
					componentBeneathMouse = associateToolbars.get(i)
							.getComponent();
					middleY = (componentBeneathMouse.getBounds().getMinY() + componentBeneathMouse
							.getBounds().getMaxY()) / 2.0;
					if (Math.abs(mouseCoordinate.getY() - middleY) >= formerDistance){
						// the mouse is closer of the former dockable
						return associateToolbars.get(i - 1);
					}
					formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
				}
				return associateToolbars.get(dockableCount - 1);
			case HORIZONTAL:
				componentBeneathMouse = associateToolbars.get(0).getComponent();
				double middleX = (componentBeneathMouse.getBounds().getMinX() + componentBeneathMouse
						.getBounds().getMaxX()) / 2.0;
				formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
				// loop on dockables too see which of them is closer of the
				// mouse
				for (int i = 1; i < dockableCount; i++){
					componentBeneathMouse = associateToolbars.get(i)
							.getComponent();
					middleX = (componentBeneathMouse.getBounds().getMinX() + componentBeneathMouse
							.getBounds().getMaxX()) / 2.0;
					if (Math.abs(mouseCoordinate.getX() - middleX) >= formerDistance){
						// the mouse is closer of the former dockable
						return associateToolbars.get(i - 1);
					}
					formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
				}
				return associateToolbars.get(dockableCount - 1);
			}
		}
		throw new IllegalArgumentException();
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Compute the relative position of drag dockable and the closest dockable
	 * above the mouse
	 * 
	 * @return the position and null if there's no dockable beneath mouse
	 */
	private Position computeItemPositionVSBeneathDockable(){
		Dockable dockableBeneathMouse = this.getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		if (this.dragDockable == dockableBeneathMouse){
			return Position.CENTER;
		} else{
			switch (stationHost.getOrientation(this.areaBeneathMouse)) {
			case VERTICAL:
				if (dragDockable.getComponent().getBounds().getMinY() <= dockableBeneathMouse
						.getComponent().getBounds().getMinY()){
					return Position.NORTH;
				} else{
					return Position.SOUTH;
				}
			case HORIZONTAL:
				if (dragDockable.getComponent().getBounds().getMinX() <= dockableBeneathMouse
						.getComponent().getBounds().getMinX()){
					return Position.WEST;
				} else{
					return Position.EAST;
				}
			}
		}
		throw new IllegalArgumentException();
	}

}
