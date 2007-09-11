package bibliothek.extension.gui.dock.theme;

import bibliothek.extension.gui.dock.theme.eclipse.DefaultEclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseDisplayerFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStackDockComponent;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseStationPaint;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseTitleFactory;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.rex.tab.TabPainter;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
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
import bibliothek.gui.dock.util.PropertyKey;

/**
 * @author Janni Kovacs
 */
@ThemeProperties(
		authors = {"Janni Kovacs", "Benjamin Sigg"},
		descriptionBundle = "theme.eclipse.description",
		nameBundle = "theme.eclipse",
		webpages = {""})
public class EclipseTheme extends BasicTheme implements StackDockComponentFactory {
	public static final PropertyKey<Boolean> PAINT_ICONS_WHEN_DESELECTED = 
		new PropertyKey<Boolean>( "EclipseTheme paint icons when deselected" );
	
	public static final PropertyKey<TabPainter> TAB_PAINTER =
		new PropertyKey<TabPainter>( "EclipseTheme tab painter" );
	
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
}

