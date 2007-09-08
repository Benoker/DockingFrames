package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;

/**
 * A list of {@link DockAction DockActions} filtered by the 
 * {@link EclipseThemeConnector}, using {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)}.
 * @author Benjamin Sigg
 *
 */
public class EclipseDockActionSource extends FilteredDockActionSource {
	/** the theme for which this source is used */
	private EclipseTheme theme;
	/** the Dockable for which actions are filtered */
	private Dockable dockable;
	/** the expected result of {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)} */
	private boolean tab;
	
	/**
	 * Creates a new source
	 * @param theme the theme for which this source is used
	 * @param source the source which is filtered
	 * @param dockable the Dockable for which the actions are shown
	 * @param tab the expected result of {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)}
	 */
	public EclipseDockActionSource( EclipseTheme theme, DockActionSource source, Dockable dockable, boolean tab ){
		super( source );
		this.theme = theme;
		this.dockable = dockable;
		this.tab = tab;
	}
	
	@Override
	protected boolean include( DockAction action ){
		return theme.getThemeConnector().isTabAction( dockable, action ) == tab;
	}
}
