package bibliothek.extension.gui.dock.theme;

import bibliothek.extension.gui.dock.theme.eclipse.EclipseDisplayerFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStackDockComponent;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStationPaint;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTitleFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.StackDockStation;
import bibliothek.gui.dock.station.StationPaint;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.StackDockComponentFactory;
import bibliothek.gui.dock.themes.BasicTheme;
import bibliothek.gui.dock.themes.ThemeProperties;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.title.DockTitleFactory;
import bibliothek.gui.dock.title.MovingTitleGetter;

/**
 * @author Janni Kovacs
 */
@ThemeProperties(
		authors = {"Janni Kovacs", "Benjamin Sigg"},
		descriptionBundle = "theme.eclipse.description",
		nameBundle = "theme.eclipse",
		webpages = {""})
public class EclipseTheme extends BasicTheme implements StackDockComponentFactory {
	private static final EclipseThemeConnector DEFAULT_ECLIPSE_THEME_CONNECTOR = new DefaultEclipseThemeConnector();

	private EclipseTitleFactory ECLIPSE_TITLE_FACTORY = new EclipseTitleFactory();
	private EclipseStationPaint ECLIPSE_STATION_PAINT = new EclipseStationPaint();
	private EclipseDisplayerFactory ECLIPSE_DISPLAYER_FACTORY;
	private EclipseThemeConnector connector = DEFAULT_ECLIPSE_THEME_CONNECTOR;

	public EclipseTheme() {
		setStackDockComponentFactory(this);
		ECLIPSE_DISPLAYER_FACTORY = new EclipseDisplayerFactory(this);
	}

	@Override
	public DisplayerFactory getDisplayFactory(DockStation station) {
		return ECLIPSE_DISPLAYER_FACTORY;
	}

	@Override
	public StationPaint getPaint(DockStation station) {
		return ECLIPSE_STATION_PAINT;
	}

	@Override
	public MovingTitleGetter getMovingTitleGetter(DockController controller) {
		return new MovingTitleGetter() {
			public DockTitle get(DockController controller, DockTitle snatched) {
				return null;
			}

			public DockTitle get(DockController controller, Dockable dockable) {
				return null;
			}
		};
	}

	@Override
	public DockTitleFactory getTitleFactory(DockController controller) {
		return ECLIPSE_TITLE_FACTORY;
	}

	public StackDockComponent create(StackDockStation station) {
		return new EclipseStackDockComponent(this, station);
	}

	@Override
	public void install(DockController controller) {
		super.install(controller);
	}

	public void setThemeConnector(EclipseThemeConnector connector) {
		if(connector == null)
			connector = DEFAULT_ECLIPSE_THEME_CONNECTOR;
		this.connector = connector;
	}

	public EclipseThemeConnector getThemeConnector() {
		return connector;
	}

	
	private static class DefaultEclipseThemeConnector implements EclipseThemeConnector {

		public boolean isClosable(Dockable d) {
			return false;
		}

		public boolean isFullscreenEnabled(Dockable d) {
			return false;
		}

		public void dockableClosing(Dockable d) {
			DockStation station = d.getDockParent();
			if (station != null) {
				station.drag(d);
			}
		}

		public boolean isTitleBarShown(Dockable d) {
			return true;
		}
		
		public boolean isTabAction( Dockable dockable, DockAction action ){
			return false;
		}
	}
}

