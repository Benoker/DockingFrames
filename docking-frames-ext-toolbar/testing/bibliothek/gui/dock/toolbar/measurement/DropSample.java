package bibliothek.gui.dock.toolbar.measurement;

import java.awt.Color;
import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Describes a {@link DockStation} and offers a method to visualize how the mouse position affects a drop
 * operation. It is the samples responsibility to create a {@link DockStation}, a {@link DockController}, and maybe
 * some {@link Dockable}s.
 * @author Benjamin Sigg
 */
public interface DropSample {
	/**
	 * Gets the station that is tested.
	 * @return the station, not <code>null</code>
	 */
	public DockStation getStation();
	
	/**
	 * Gets a {@link Component} that is shown on and that represents the
	 * {@link #getStation() station}. This may or may not be  {@link Dockable#getComponent()}.
	 * @return the component to show, not <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Emulates a drop operation at <code>mouseX/mouseY</code>. Returns a {@link Color} which will be used to fill
	 * the pixel at <code>mouseX/mouseY</code>. What {@link Color} to return depends on the sample, but usually the
	 * color should depend on what will happen when the {@link Dockable} is dropped, e.g. a sample could use
	 * different colors if the item is dropped at the top of the station or at the bottom of the station.
	 * @param mouseX the position of the mouse on the screen
	 * @param mouseY the position of the mouse on the screen
	 * @return the color to fill the pixel, not <code>null</code>
	 */
	public Color dropAt( int mouseX, int mouseY );
}
