package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleRequest;
import bibliothek.gui.dock.title.DockTitleVersion;

public class ToolbarDockTitleRequest extends DockTitleRequest{

	public ToolbarDockTitleRequest( DockStation parent, Dockable target,
			DockTitleVersion version ){
		super(parent, target, version);
	}

	@Override
	protected void answer( DockTitle previous, DockTitle title ){
		if (previous != null){
			// you probably don't need to add/remove MouseListeners, but this is
			// how it would be done
			// previous.removeMouseInputListener( mouseListener );

			// Dockables know which DockTitle is associated with them. On
			// removal you need to call "unbind"
			this.getTarget().unbind(previous);
			// finally you remove the title Component from its Container
			ToolbarContainerDockStation station = (ToolbarContainerDockStation) this
					.getParent();
//			station.getComponent().get remove(previous.getComponent());
		}
		if (title != null){
			// if you would need a MouseListener, which you don't, then you
			// would add it here
			// title.addMouseInputListener(mouseListener);

			// here you can configure the title, e.g. whether it is painted
			// horizontal or vertical.
//			title.setOrientation(orientation(direction));
			// you need to tell the Dockable about the DockTitle. Otherwise the
			// title does not update its text or icon
			this.getTarget().bind(title);
			// and you need to show the Component
//			parent.add(title.getComponent());
		}
		// this.answer(title);
	}

}
