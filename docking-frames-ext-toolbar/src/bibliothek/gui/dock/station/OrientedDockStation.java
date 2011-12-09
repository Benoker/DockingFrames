package bibliothek.gui.dock.station;

import bibliothek.gui.Orientation;


/**
 * An oriented dockstation is a station where dockables are oriented either
 * vertically either horizontally. The methods allow to set the orientation and
 * to know what is the current orientation.
 * 
 * @author Herve Guillaume
 */
public interface OrientedDockStation extends OrientingDockStation{

	/**
	 * Sets the orientation of dockables in this station.
	 * 
	 * @param orientation
	 */
	public void setOrientation( Orientation orientation );

	/**
	 * Gets the orientation of dockables in this station.
	 * 
	 * @return The orientation of dockables in this station
	 */
	public Orientation getOrientation();

}
