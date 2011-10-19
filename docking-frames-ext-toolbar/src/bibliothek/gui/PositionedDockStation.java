package bibliothek.gui;


/**
 * An positioned dockstation is a station where dockables are layouted with
 * regards to the {@link Position} of this dockstation {@link Position#NORTH}, {@link Position#SOUTH},
 * {@link Position#WEST}, {@link Position#EAST} or {@link Position#CENTER}. 
 * The methods allow to set the position and to know what is the current orientation.
 * 
 * @author Herve Guillaume
 *
 */
public interface PositionedDockStation {
	
	/**
	 * Set the position of this station
	 * 
	 * @param position
	 */
	public void setPosition( Position position );

	/**
	 * Get the position of this station.
	 * 
	 * @return The position
	 */
	public Position getPosition();

}
