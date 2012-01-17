package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.window.WindowConfiguration;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;

/**
 * This class will configure {@link ScreenDockWindow}s such that grabing the
 * title of a toolbar does not start a drag and drop operation directly, but
 * first allows the user to move around the entire window.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarWindowConfiguration implements
		ScreenDockWindowConfiguration{
	private final DockController controller;

	/**
	 * Creates a new configuration
	 * 
	 * @param controller
	 *            the controller in whose realm this configuration is used
	 */
	public ToolbarWindowConfiguration( DockController controller ){
		this.controller = controller;
	}

	/**
	 * Gets the strategy which is used by this configuration.
	 * 
	 * @return the strategy used to identify toolbar items
	 */
	protected ToolbarStrategy getStrategy(){
		return controller.getProperties().get(ToolbarStrategy.STRATEGY);
	}

	@Override
	public WindowConfiguration getConfiguration( ScreenDockStation station, Dockable dockable ){
		if (getStrategy().isToolbarPart(dockable)){
			final WindowConfiguration configuration = new WindowConfiguration();
			configuration.setMoveOnTitleGrab(true);
			configuration.setAllowDragAndDropOnTitle(true);
			configuration.setResetOnDropable(false);
			configuration.setResizeable(false);
			configuration.setTransparent( true );
			return configuration;
		}

		return null;
	}

}
