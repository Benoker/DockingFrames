package bibliothek.gui;

/**
 * An oriented dockstation is a station where dockables are oriented either
 * vertically either horizontally. The methods allow to set the orientation and
 * to know what is the current orientation.
 * 
 * @author Herv� Guillaume
 */
public interface OrientedDockStation {

	/**
	 * Describe the orientation
	 * 
	 * @author Herv� Guillaume
	 * 
	 */
	public enum Orientation {
		VERTICAL, HORIZONTAL
	}

	/**
	 * Set the orientation of dockables in this station
	 * 
	 * @param orientation
	 */
	public void setOrientation( Orientation orientation );

	/**
	 * Get the orientation of dockables in this station. I
	 * 
	 * @return The orientation of dockables in this station
	 */
	public Orientation getOrientation();

}
