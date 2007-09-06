package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.basic.BasicDockTitle;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * @author Janni Kovacs
 */
public class EclipseTitleFactory implements DockTitleFactory {
	public EclipseTitleFactory() {
	}

	public DockTitle createDockableTitle(Dockable dockable, DockTitleVersion version) {
		return new EclipseDockTitle(dockable, version);
	}

	public <D extends Dockable & DockStation> DockTitle createStationTitle(D dockable, DockTitleVersion version) {
		return null;
	}
}
