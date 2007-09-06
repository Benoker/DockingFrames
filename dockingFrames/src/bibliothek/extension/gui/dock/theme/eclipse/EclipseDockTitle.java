package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.AbstractDockTitle;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * @author Janni Kovacs
 */
public class EclipseDockTitle extends AbstractDockTitle {
	public EclipseDockTitle(Dockable dockable, DockTitleVersion version) {
		super(dockable, version, false);
	}
}
