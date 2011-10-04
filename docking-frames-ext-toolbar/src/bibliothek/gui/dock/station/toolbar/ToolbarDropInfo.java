package bibliothek.gui.dock.station.toolbar;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.OrientedDockStation;
import bibliothek.gui.OrientedDockStation.Orientation;
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
	/**
	 * The multiple {@link Dockable} which belong to the dockstation
	 */
	private ArrayList<Dockable> list = new ArrayList<Dockable>();
	/** Location of the mouse */
	public int mouseX, mouseY;
	/**
	 * index computed with reference take on upper left corner on the underneath
	 * dockables
	 */
	private int indexUpperLeft = -1;
	/**
	 * index computed with reference take on bottom right corner on the
	 * underneath dockables
	 */
	private int indexBottomRight = -1;

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
		this.dragDockable = dockable;
		this.stationHost = stationHost;
		for (int i = 0; i < stationHost.getDockableCount(); i++){
			list.add(this.stationHost.getDockable(i));
		}
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
		// at the moment nothing to do
		System.out
				.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
		System.out
				.println("¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤");
		Dockable dockable = (Dockable) stationHost;
		dockable.getComponent().repaint();

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
		// TODO
		if (stationHost.asDockable() != null){
			System.out
					.println("ééééééééééééééééééééééééééééééééééééééééééééééééééééééééééé");
			System.out
					.println("ééééééééééééééééééééééééééééééééééééééééééééééééééééééééééé");
			Dockable dockable = (Dockable) stationHost;
			dockable.getComponent().repaint();
		}
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
	public int getIndex( ReferencePoint reference ){
		OrientedDockStation orientedStation = (OrientedDockStation) stationHost;
		switch (reference) {
		case UPPERLEFT:
			if (indexUpperLeft == -1){
				indexUpperLeft = computeIndex(list, mouseX, mouseY,
						orientedStation.getOrientation(), reference);
			}
			return indexUpperLeft;
		case BOTTOMRIGHT:
			if (indexBottomRight == -1){
				indexBottomRight = computeIndex(list, mouseX, mouseY,
						orientedStation.getOrientation(), reference);
			}
			return indexBottomRight;
		default:
			return 0;
		}
	}

	/**
	 * compute the <code>index</code> of the component beneath the mouse
	 * 
	 * @param list
	 *            list of the dockables in the middle of which the drag dockable
	 *            will be inserted
	 * @param mouseX
	 *            position X of the mouse
	 * @param mouseY
	 *            position Y of the mouse
	 * @param orientation
	 *            orientation of the dockables
	 * @param reference
	 *            reference point used to compute relative position of the
	 *            dockables
	 * @return the index
	 */
	public int computeIndex( ArrayList<Dockable> list, int mouseX, int mouseY,
			Orientation orientation, ReferencePoint reference ){
		int dockableCount = list.size();
		Point mousePoint = new Point(this.mouseX, this.mouseY);
		Dockable stationDockable = (Dockable) stationHost;
		SwingUtilities.convertPointFromScreen(mousePoint,
				stationDockable.getComponent());
		switch (reference) {
		case UPPERLEFT:
			for (int i = dockableCount - 1; i > -1; i--){
				Point componentPoint = new Point((int) stationHost
						.getDockable(i).getComponent().getBounds().getMinX(),
						(int) stationHost.getDockable(i).getComponent()
								.getBounds().getMinY());
				switch (orientation) {
				case VERTICAL:
					if (mousePoint.getY() > componentPoint.getY()){
						return i + 1;
					}
					break;
				case HORIZONTAL:
					if (mousePoint.getX() > componentPoint.getX()){
						return i + 1;
					}
					break;
				}
			}
		case BOTTOMRIGHT:
			for (int i = dockableCount - 1; i > -1; i--){
				Point componentPoint = new Point((int) stationHost
						.getDockable(i).getComponent().getBounds().getMaxX(),
						(int) stationHost.getDockable(i).getComponent()
								.getBounds().getMaxY());
				switch (orientation) {
				case VERTICAL:
					if (mousePoint.getY() > componentPoint.getY()){
						return i + 1;
					}
					break;
				case HORIZONTAL:
					if (mousePoint.getX() > componentPoint.getX()){
						return i + 1;
					}
					break;
				}
			}
		}
		return 0;
	}

	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

}
