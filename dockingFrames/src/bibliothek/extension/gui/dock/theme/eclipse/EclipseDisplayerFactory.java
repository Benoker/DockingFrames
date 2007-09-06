package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.extension.gui.dock.theme.EclipseTheme;

/**
 * @author Janni Kovacs
 */
public class EclipseDisplayerFactory extends BasicDisplayerFactory {
	private EclipseTheme theme;

	public EclipseDisplayerFactory(EclipseTheme theme) {
		this.theme = theme;
	}

	@Override
	public DockableDisplayer create(DockStation station, Dockable dockable, DockTitle title) {
		DockableDisplayer displayer;
		if (dockable.asDockStation() == null) {
			if (theme.getThemeConnector().isTitleBarShown(dockable)) {
				displayer = new EclipseDockableDisplayer(theme, station, dockable);
			} else {
				displayer = new NoTitleDisplayer(station, dockable);
			}
		} else {
			displayer = super.create(station, dockable, title);
		}
		displayer.setBorder(null);
		return displayer;
	}
}
