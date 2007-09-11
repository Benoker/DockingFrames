package bibliothek.extension.gui.dock.theme.eclipse;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a {@link bibliothek.gui.dock.action.DockAction} that it should be shown
 * in the tabs when the {@link bibliothek.extension.gui.dock.theme.EclipseTheme}
 * is used. This annotation receives only attention when the 
 * {@link DefaultEclipseThemeConnector} is used.
 * @author Benjamin Sigg
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EclipseTabDockAction {
	// nothing
}
