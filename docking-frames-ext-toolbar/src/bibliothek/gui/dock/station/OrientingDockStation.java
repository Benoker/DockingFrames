package bibliothek.gui.dock.station;

import bibliothek.gui.Dockable;

import bibliothek.gui.DockStation;

/**
 * A {@link DockStation} where the children have an orientation.
 * @author Benjamin Sigg
 */
public interface OrientingDockStation extends DockStation{
	/**
	 * Tells what orientation <code>child</code> has. 
	 * @param child a child of this station
	 * @return the orientation, never <code>null</code>
	 * @throws IllegalArgumentException if <code>child</code> is not a child
	 */
	public Orientation getOrientationOf( Dockable child );
	
	/**
	 * Adds the observer <code>listener</code> to this station. The observer receives an event if the
	 * orientation of a child of this station changed. The observer may or may not receive an event
	 * upon dropping a new {@link Dockable} onto this station.
	 * @param listener the new observer, not <code>null</code>
	 */
	public void addOrientingDockStationListener( OrientingDockStationListener listener );
	
	/**
	 * Removes the observer <code>listener</code> from this station.
	 * @param listener the listener to remove
	 */
	public void removeOrientingDockStationListener( OrientingDockStationListener listener );
}
