package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.dock.displayer.DisplayerCombinerTarget;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * This class contains and computes information about a drag and drop action
 * between {@link Dockable} and {@link DockStation}, especially with toolbar
 * dockstation type. Allow to know where the Dockable should be inserted into
 * which DockStation.
 * 
 * @author Herve Guillaume
 * @param <S>
 *            the kind of station using this AbstractToolbarDropInfo
 */
public abstract class AbstractToolbarDropInfo<S extends DockStation> implements
		StationDropOperation{
	/** The {@link Dockable} which is inserted */
	private Dockable dragDockable;
	/**
	 * The {@link Dockable} which received the dockbale (WARNING: this can be
	 * different to his original dock parent!)
	 */
	private S stationHost;
	/** Location of the mouse */
	@SuppressWarnings("unused")
	private int mouseX, mouseY;

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
	public AbstractToolbarDropInfo( Dockable dockable, S stationHost,
			int mouseX, int mouseY ){
		System.out.println(this.toString()
				+ "## new ToolbarComplexDropInfo ## ");
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
	
	@Override
	public String toString(){
		return this.getClass().getSimpleName() + '@'
				+ Integer.toHexString(this.hashCode());
	}

	/**
	 * Gets the <code>Dockable</code> beneath the mouse.
	 * 
	 * @return the dockable and <code>null</code> if there's no dockable
	 */
	public abstract Dockable getDockableBeneathMouse();

	/**
	 * Gets the closest <code>side</code> of the component beneath the mouse.
	 * Example: if the mouse is over a button, near the top of the button, this
	 * return {@link Position#NORTH})
	 * 
	 * @return the closest side
	 */
	public abstract Position getSideDockableBeneathMouse();

	/**
	 * Gets the relative position between: the initial position of the dockable
	 * and the dockable beneath the mouse. The relative position is computed
	 * either on horizontal or vertical axis.
	 * 
	 * @return the relative position (if the drag dockable and the dockable
	 *         beneath mouse are the same, return {@link Position#CENTER})
	 */
	public abstract Position getItemPositionVSBeneathDockable();

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