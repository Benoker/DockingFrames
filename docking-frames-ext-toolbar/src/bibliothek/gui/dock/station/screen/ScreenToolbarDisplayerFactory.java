package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.themes.DefaultDisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;

/**
 * A {@link DisplayerFactory} creating special {@link DockableDisplayer}s for
 * items where {@link ToolbarStrategy#isToolbarPart(bibliothek.gui.Dockable)}
 * returns <code>true</code>. This factory forwards any calls to a
 * {@link DisplayerFactory} that is registered at the {@link ThemeManager} with
 * a key "toolbar.screen".
 * 
 * @author Benjamin Sigg
 */
public class ScreenToolbarDisplayerFactory implements DisplayerFactory{
	private final DockController controller;

	/**
	 * Creates a new factory
	 * 
	 * @param controller
	 *            the controller in whose realm this factory is used
	 */
	public ScreenToolbarDisplayerFactory( DockController controller ){
		this.controller = controller;
	}

	@Override
	public void request( DisplayerRequest request ){
		final ToolbarStrategy strategy = controller.getProperties().get(
				ToolbarStrategy.STRATEGY);
		if (strategy.isToolbarPart(request.getTarget())){
			final DefaultDisplayerFactoryValue value = new DefaultDisplayerFactoryValue(
					ThemeManager.DISPLAYER_FACTORY + ".toolbar.screen",
					request.getParent());
			try{
				value.setController(request.getController());
				final DisplayerFactory factory = value.get();
				if (factory != null){
					factory.request(request);
				}
			} finally{
				value.setController(null);
			}
		}
	}
}
