package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;

/**
 * @author Janni Kovacs
 */
public interface EclipseThemeConnector {

	public boolean isTitleBarShown(Dockable d);

	public boolean isClosable(Dockable d);

	public boolean isFullscreenEnabled(Dockable d);

	/**
	 * Is called when the close icon on one of the tabs is clicked. This method should perform the
	 * close action and for example call <code>frontend.hide(d)</code>.
	 *
	 * @param d Dockable whose close icon wsa clicked
	 */
	public void dockableClosing(Dockable d);
	
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
