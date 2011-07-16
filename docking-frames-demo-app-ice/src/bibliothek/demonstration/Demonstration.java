package bibliothek.demonstration;

import java.awt.image.BufferedImage;

import javax.swing.Icon;

/**
 * A Demonstration is a standalone-application which is started by this
 * framework.
 * @author Benjamin Sigg
 */
public interface Demonstration {
	/**
	 * A small (16x16 pixel) icon for this Demonstration.
	 * @return the icon
	 */
	public Icon getIcon();
	
	/**
	 * Gets the name of this Demonstration.
	 * @return the name
	 */
	public String getName();
	
	/**
	 * Gets a screencapture of this demonstration
	 * @return the image
	 */
	public BufferedImage getImage();
	
	/**
	 * Gets a description of this application formated in HTML.
	 * @return the description
	 */
	public String getHTML();
	
	/**
	 * Starts a new instance of this Demonstration
	 * @param monitor the monitor to inform about the state of this Demonstration
	 */
	public void show( Monitor monitor );
}
