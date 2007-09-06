package bibliothek.extension.gui.dock.theme.eclipse.rex.resources;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Erlaubt den einfachen Zugriff auf net.roarsoftware.rex Resource libs.
 *
 * @author Janni Kovacs
 * @version 0.1
 */
public class Resources {

	private static ResourceBundle lang = loadBundle();
	private static ResourceBundle icons = ResourceBundle.getBundle("bibliothek.extension.gui.dock.theme.eclipse.rex.resources.icon.Resources");


	public static String getResource(String key) {
		checkLocale();
		return lang.getString(key);
	}

	public static Icon getIcon(String key) {
		checkLocale();
		URL resource = Resources.class.getResource("icon/" + key);
		if (resource == null)
			resource = Resources.class.getResource("icon/" + icons.getString(key));
		return resource != null ? new ImageIcon(resource) : null;
	}

	private static void checkLocale() {
		if (!lang.getLocale().equals(Locale.getDefault()))
			lang = loadBundle();
	}

	private static ResourceBundle loadBundle() {
		return ResourceBundle.getBundle("bibliothek.extension.gui.dock.theme.eclipse.rex.resources.lang.Resources");
	}
}