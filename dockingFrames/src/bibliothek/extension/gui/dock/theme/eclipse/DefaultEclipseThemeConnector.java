/**
 * 
 */
package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;

public class DefaultEclipseThemeConnector implements EclipseThemeConnector {
	public boolean isTitleBarShown(Dockable d) {
		return true;
	}
	
	public boolean isTabAction( Dockable dockable, DockAction action ){
		EclipseTabDockAction tab = action.getClass().getAnnotation( EclipseTabDockAction.class );
		return tab != null;
	}
}