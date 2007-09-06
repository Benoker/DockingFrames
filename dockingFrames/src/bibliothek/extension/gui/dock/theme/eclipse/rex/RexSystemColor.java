package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.Map;

/**
 * @author Janni Kovacs
 */
public class RexSystemColor {

	private RexSystemColor() {
	}

	public static boolean isXPThemeActive() {
		Object prop = Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
		return (prop != null && (Boolean) prop);
	}

	public static Color getActiveTitleColor() {
		return decide(SystemColor.activeCaption, "sysmetrics.activecaption");
	}

	public static Color getActiveTitleColorGradient() {
		return decide(SystemColor.activeCaption.brighter(), "sysmetrics.gradientactivecaption");
	}

	public static Color getInactiveTitleColor() {
		return decide(SystemColor.inactiveCaption, "sysmetrics.inactivecaption");
	}

	public static Color getInactiveTitleColorGradient() {
		return decide(SystemColor.inactiveCaption.brighter(), "sysmetrics.gradientinactivecaption");
	}

	private static Color decide(Color defaultColor, String propertyKey) {
		if (isXPThemeActive()) {
			Color c = getXPStyleColor(propertyKey);
			if(c != null)
				return c;
		}
		return defaultColor;
	}


	public static Color getXPStyleColor(String s) {
		if (!isXPThemeActive())
			return null;
		Map xpStyleResources = (Map) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.resources.strings");
		if (xpStyleResources == null)
			return null;
		String color = (String) xpStyleResources.get(s);
		String[] rgb = color.split(" ");
		int r = Integer.parseInt(rgb[0]);
		int g = Integer.parseInt(rgb[1]);
		int b = Integer.parseInt(rgb[2]);
		return new Color(r, g, b);
	}
}
