package bibliothek.gui.dock.station;

import bibliothek.gui.Orientation;

/**
 * A listener that is added to an {@link OrientingDockStation}.
 * @author Benjamin Sigg
 */
public interface OrientingDockStationListener {
	/**
	 * Called if the {@link Orientation} of one or many children of some {@link OrientingDockStation}
	 * has changed.
	 * @param event detailed information about the event
	 */
	public void changed( OrientingDockStationEvent event );
}
