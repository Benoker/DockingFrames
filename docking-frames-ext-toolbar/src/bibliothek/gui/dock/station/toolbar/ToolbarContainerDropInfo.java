/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;
import java.awt.Point;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationChildHandle;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;

/**
 * This class contains and computes information about a drag and drop action.
 * Especially, where the {@link Dockable} should be inserted into which
 * {@link ToolbarContainerDockStation}
 * 
 * @author Herve Guillaume
 */
public abstract class ToolbarContainerDropInfo implements StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private final Dockable dragDockable;
	/**
	 * The {@link DockStation} which received the dockable (WARNING: this can be
	 * different to the original dock parent of the dockable!)
	 */
	private final ToolbarContainerDockStation stationHost;
	/** the drag dockable will be insert inside this {@link Dockable}s */
	private final DockablePlaceholderList<StationChildHandle> associateToolbars;
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
	private Position dragDockablePosition;

	/**
	 * Constructs a new info to know where drop a dockable
	 * 
	 * @param dockable
	 *            the dockable to drop
	 * @param stationHost
	 *            the station where drop the dockable
	 * @param associateToolbars
	 *            the other dockables in the station
	 * @param mouseX
	 *            the mouse position on X axis
	 * @param mouseY
	 *            the mouse position on Y axis
	 */
	public ToolbarContainerDropInfo( Dockable dockable,
			ToolbarContainerDockStation stationHost,
			DockablePlaceholderList<StationChildHandle> associateToolbars,
			int mouseX, int mouseY ){
		dragDockable = dockable;
		this.stationHost = stationHost;
		this.associateToolbars = associateToolbars;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}

	@Override
	public Dockable getItem(){
		return dragDockable;
	}

	@Override
	public DockStation getTarget(){
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
	 * Gets the index of {@link #getDockableBeneathMouse()} in the parent {@link DockStation}.
	 * @return the index or -1 if not available
	 */
	public abstract int getIndex();
	
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
			dragDockablePosition = computeItemPositionVSBeneathDockable();
		}
		return dragDockablePosition;

	}

	/**
	 * compute the closest <code>side</code> of the closest component with
	 * regards to the mouse
	 * 
	 * @return the side or null if there's no dockable beneath mouse
	 */
	private Position computeSideDockableBeneathMouse(){
		// the dockable the closest of the mouse
		final Dockable dockableBeneathMouse = getDockableBeneathMouse();
		if (dockableBeneathMouse == null){
			return null;
		}
		// mouse coordinate
		final Point mouseCoordinate = new Point(mouseX, mouseY);
		switch (stationHost.getOrientation()) {
		case VERTICAL:
			// The mouse is now in the frame of reference of the area beneath
			// mouse
			SwingUtilities.convertPointFromScreen(mouseCoordinate,
					dockableBeneathMouse.getComponent());
			final double middleY = (dockableBeneathMouse.getComponent()
					.getBounds().getMinY() + dockableBeneathMouse
					.getComponent().getBounds().getMaxY()) / 2.0;
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
			final double middleX = (dockableBeneathMouse.getComponent()
					.getBounds().getMinX() + dockableBeneathMouse
					.getComponent().getBounds().getMaxX()) / 2.0;

			if (Math.abs(mouseCoordinate.getX()) <= middleX){
				return Position.WEST;
			} else{
				return Position.EAST;
			}
		}
		throw new IllegalArgumentException();
	}

	/**
	 * identify the dockable component beneath the mouse
	 * 
	 * @return the dockable beneath mouse and null if none
	 */
	private Dockable computeDockableBeneathMouse(){
		final DockablePlaceholderList.Filter<StationChildHandle> associateToolbars = this.associateToolbars
				.dockables();

		final int dockableCount = associateToolbars.size();
		if (dockableCount <= 0){
			return null;
		}

		final Point mouseCoordinate = new Point(mouseX, mouseY);
		// This component stands for the panel beneath the mouse. This
		// rectangle will be the frame of reference of component inside this
		// panel.
		final Component panelBeneathMouse = stationHost.getContainerPanel();
		// The mouse is now in the frame of reference of the area beneath
		// mouse
		SwingUtilities.convertPointFromScreen(mouseCoordinate,
				panelBeneathMouse);
		Component componentBeneathMouse;
		double formerDistance;
		final Orientation orientation = stationHost.getOrientation();
		switch (orientation) {
		case VERTICAL:
			componentBeneathMouse = associateToolbars.get(0).getDisplayer()
					.getComponent();
			double middleY = (componentBeneathMouse.getBounds().getMinY() + componentBeneathMouse
					.getBounds().getMaxY()) / 2.0;
			formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
			// loop on dockables too see which of them is closer of the
			// mouse
			for (int i = 1; i < dockableCount; i++){
				componentBeneathMouse = associateToolbars.get(i).getDisplayer()
						.getComponent();
				middleY = (componentBeneathMouse.getBounds().getMinY() + componentBeneathMouse
						.getBounds().getMaxY()) / 2.0;
				if (Math.abs(mouseCoordinate.getY() - middleY) >= formerDistance){
					// the mouse is closer of the former dockable
					return associateToolbars.get(i - 1).getDockable();
				}
				formerDistance = Math.abs(mouseCoordinate.getY() - middleY);
			}
			return associateToolbars.get(dockableCount - 1).getDockable();
		case HORIZONTAL:
			componentBeneathMouse = associateToolbars.get(0).getDisplayer()
					.getComponent();
			double middleX = (componentBeneathMouse.getBounds().getMinX() + componentBeneathMouse
					.getBounds().getMaxX()) / 2.0;
			formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
			// loop on dockables too see which of them is closer of the
			// mouse
			for (int i = 1; i < dockableCount; i++){
				componentBeneathMouse = associateToolbars.get(i).getDisplayer()
						.getComponent();
				middleX = (componentBeneathMouse.getBounds().getMinX() + componentBeneathMouse
						.getBounds().getMaxX()) / 2.0;
				if (Math.abs(mouseCoordinate.getX() - middleX) >= formerDistance){
					// the mouse is closer of the former dockable
					return associateToolbars.get(i - 1).getDockable();
				}
				formerDistance = Math.abs(mouseCoordinate.getX() - middleX);
			}
			return associateToolbars.get(dockableCount - 1).getDockable();
		}
		throw new IllegalArgumentException();
	}

	/**
	 * Compute the relative position of drag dockable and the closest dockable
	 * above the mouse
	 * 
	 * @return the position and null if there's no dockable beneath mouse
	 */
	private Position computeItemPositionVSBeneathDockable(){
		final Point coordDockableDragged = getItem().getComponent()
				.getLocation();
		if (getDockableBeneathMouse() != null){
			final Point coordDockableBeneathMouse = getDockableBeneathMouse()
					.getComponent().getLocation();
			// The dockable is now in the frame of reference of the dockable
			// beneath mouse
			SwingUtilities.convertPointFromScreen(coordDockableDragged,
					getDockableBeneathMouse().getComponent());
			if (getDockableBeneathMouse() == null){
				return null;
			}
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
				+ Integer.toHexString(hashCode());
	}

	/**
	 * Return a string describing field values
	 * 
	 * @return string describing fields
	 */
	public String toSummaryString(){
		final String ln = System.getProperty("line.separator");
		return "	=> Drag dockable: " + getItem() + ln + "	=> Station target: "
				+ getTarget() + ln + "	=> Dockable beneath mouse:"
				+ getDockableBeneathMouse() + ln + "	=> Closest side:"
				+ getSideDockableBeneathMouse() + ln
				+ "	=> Drag dockable VS dockable beneath mouse:"
				+ getItemPositionVSBeneathDockable();
	}

}
