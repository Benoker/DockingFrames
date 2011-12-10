package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockFullscreenFilter;
import bibliothek.gui.dock.util.SilentPropertyValue;

/**
 * This filter can be added to a {@link ScreenDockStation} and ensures that no
 * toolbar element can be in fullscreen mode
 * 
 * @author Benjamin Sigg
 */
public class ToolbarFullscreenFilter implements ScreenDockFullscreenFilter{
	private final DockController controller;

	/**
	 * Creates a new filter
	 * 
	 * @param controller
	 *            the controller in whose realm this filter will be used
	 */
	public ToolbarFullscreenFilter( DockController controller ){
		this.controller = controller;
	}

	@Override
	public boolean isFullscreenEnabled( Dockable dockable ){
		final SilentPropertyValue<ToolbarStrategy> value = new SilentPropertyValue<ToolbarStrategy>(
				ToolbarStrategy.STRATEGY, controller);
		final ToolbarStrategy strategy = value.getValue();
		value.setProperties((DockController) null);

		return !strategy.isToolbarPart(dockable);
	}
}
