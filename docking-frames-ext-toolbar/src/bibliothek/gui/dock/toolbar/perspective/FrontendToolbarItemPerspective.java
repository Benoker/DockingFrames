package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.ToolbarItemDockable;
import bibliothek.gui.dock.frontend.FrontendDockablePerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarItemDockableFactory;

/**
 * Represents a {@link ToolbarItemDockable} that was registered at a {@link DockFrontend} with
 * a unique identifier.
 * @author Benjamin Sigg
 */
public class FrontendToolbarItemPerspective extends FrontendDockablePerspective{
	/**
	 * Creates a new item.
	 * @param id the unique identifier of this item
	 */
	public FrontendToolbarItemPerspective( String id ){
		super( id );
	}

	@Override
	public String getFactoryID(){
		return ToolbarItemDockableFactory.ID;
	}
}
