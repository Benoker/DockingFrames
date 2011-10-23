package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;
import bibliothek.gui.dock.title.NullTitleFactory;

/**
 * This {@link DockTitleFactory} creates special new titles for any {@link Dockable} that is
 * a {@link ToolbarStrategy#isToolbarPart(Dockable)}. To be more exact: if a toolbar part is
 * detected, the {@link DockFactory} with key {@link #TITLE_ID} is called.
 * @author Benjamin Sigg
 */
public class ScreenToolbarDockTitleFactory implements DockTitleFactory{
	/** unique identifier for the {@link DockTitleVersion} used by this factory */
	public static final String TITLE_ID = "toolbar.screen";
	
	private DockController controller;
	private DockTitleVersion version;
	
	/**
	 * Creates a new factory.
	 * @param controller the controller in whose realm the titles are used
	 */
	public ScreenToolbarDockTitleFactory( DockController controller ){
		this.controller = controller;
		version = controller.getDockTitleManager().getVersion( TITLE_ID, NullTitleFactory.INSTANCE );
	}

	@Override
	public void install( DockTitleRequest request ){
		// ignored	
	}

	@Override
	public void uninstall( DockTitleRequest request ){
		// ignored
	}
	
	@Override
	public void request( DockTitleRequest request ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		if( strategy.isToolbarPart( request.getTarget() )){
			version.request( request );
		}
	}
}
