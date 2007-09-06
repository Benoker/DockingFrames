package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;

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
}
