package bibliothek.extension.gui.dock.theme.eclipse;

import javax.swing.JComponent;

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
			if (theme.getThemeConnector( station.getController() ).isTitleBarShown(dockable)) {
				displayer = new EclipseDockableDisplayer(theme, station, dockable);
			} else {
				displayer = new NoTitleDisplayer(station, dockable);
			}
		} else {
			displayer = super.create(station, dockable, title);
		}
		if( displayer.getComponent() instanceof JComponent )
			((JComponent)displayer.getComponent()).setBorder(null);
		return displayer;
	}
}
