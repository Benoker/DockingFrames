package bibliothek.gui.dock.station;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;

/**
 * An event fired by an {@link OrientedDockStation} if one or many children
 * changed their orientation.
 * 
 * @author Benjamin Sigg
 */
public class OrientingDockStationEvent{
	/** the source of the event */
	private final OrientingDockStation station;
	/** the children whose {@link Orientation} may have changed */
	private final Set<Dockable> children = new HashSet<Dockable>();

	/**
	 * Creates a new event, this is equivalent of calling
	 * {@link #OrientingDockStationEvent(OrientingDockStation, Dockable[])} with
	 * the <code>children</code> array set to <code>null</code>
	 * 
	 * @param station
	 *            the source of the event
	 */
	public OrientingDockStationEvent( OrientingDockStation station ){
		this(station, null);
	}

	/**
	 * Creates a new event.
	 * 
	 * @param station
	 *            the source of the event
	 * @param children
	 *            the affected children or <code>null</code>, if
	 *            <code>null</code> then all children of <code>station</code>
	 *            are affected
	 */
	public OrientingDockStationEvent( OrientingDockStation station,
			Dockable[] children ){
		this.station = station;

		if (children == null){
			for (int i = 0, n = station.getDockableCount(); i < n; i++){
				this.children.add(station.getDockable(i));
			}
		} else{
			for (final Dockable child : children){
				this.children.add(child);
			}
		}
	}

	/**
	 * Gets the source of the event.
	 * 
	 * @return the source
	 */
	public OrientingDockStation getStation(){
		return station;
	}

	/**
	 * Tells whether <code>dockable</code> was affected by the event.
	 * 
	 * @param dockable
	 *            some dockable which may or may not be affected
	 * @return <code>true</code> if <code>dockable</code> may have changed its
	 *         orientation
	 */
	public boolean isAffected( Dockable dockable ){
		return children.contains(dockable);
	}

	/**
	 * Gets all affected children, the collection is not modifiable.
	 * 
	 * @return all affected {@link Dockable}s
	 */
	public Collection<Dockable> getChildren(){
		return Collections.unmodifiableCollection(children);
	}
}
