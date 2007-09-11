package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;

/**
 * @author Janni Kovacs
 */
public interface EclipseThemeConnector {
	public boolean isTitleBarShown(Dockable d);
	
	public boolean isFullscreenEnabled(Dockable d);

	
	/**
	 * Tells whether <code>action</code> should be displayed on the tab
	 * of <code>dockable</code> or on the right side.
	 * @param dockable the owner of <code>action</code>
	 * @param action the action to display
	 * @return <code>true</code> if <code>action</code> should be child of
	 * the tab
	 */
	public boolean isTabAction( Dockable dockable, DockAction action );
}
