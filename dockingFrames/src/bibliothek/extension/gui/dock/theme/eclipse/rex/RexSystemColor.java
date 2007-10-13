/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.extension.gui.dock.theme.eclipse.rex;

import java.awt.Color;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.util.Map;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 * @author Janni Kovacs
 */
public class RexSystemColor {

    /*
     * 
        activeLeftColor = UIManager.getColor( "MenuItem.selectionBackground");
        inactiveLeftColor = UIManager.getColor( "MenuItem.background");
        
        activeRightColor = UIManager.getColor( "MenuItem.selectionBackground");
        inactiveRightColor = UIManager.getColor( "MenuItem.background");
        
        activeTextColor = UIManager.getColor( "MenuItem.selectionForeground");
        inactiveTextColor = UIManager.getColor( "MenuItem.foreground");
     */
    
	private RexSystemColor() {
	}

	public static boolean isXPThemeActive() {
		Object prop = Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
		return (prop != null && (Boolean) prop);
	}

	public static Color getActiveTitleColor() {
		return decide( "MenuItem.selectionBackground", SystemColor.activeCaption, "sysmetrics.activecaption");
	}

	public static Color getActiveTitleColorGradient() {
		return decide( "MenuItem.selectionBackground.[brighter]", SystemColor.activeCaption.brighter(), "sysmetrics.gradientactivecaption");
	}

	public static Color getInactiveTitleColor() {
		return decide( "MenuItem.background", SystemColor.inactiveCaption, "sysmetrics.inactivecaption");
	}

	public static Color getInactiveTitleColorGradient() {
		return decide( "MenuItem.background.[brighter]", SystemColor.inactiveCaption.brighter(), "sysmetrics.gradientinactivecaption");
	}
	
	public static Color getActiveTextColor(){
	    return decide( "MenuItem.selectionForeground", SystemColor.activeCaptionText, null );
	}
	
	public static Color getInactiveTextColor(){
	    return decide( "MenuItem.foreground", SystemColor.inactiveCaptionText, null );
	}

	public static Color getBorderColor(){
	    return SystemColor.controlShadow;
	}
	
	private static Color decide(String lookAndFeelKey, Color defaultColor, String propertyKey) {
	    if (isXPThemeActive()) {
			Color c = getXPStyleColor(propertyKey);
			if(c != null)
				return c;
		}
	    
	    boolean brighter = lookAndFeelKey.endsWith( "[brighter]" );
	    if( brighter )
	        lookAndFeelKey = lookAndFeelKey.substring( 0, lookAndFeelKey.lastIndexOf( '.' ) );
	    
	    Color result = UIManager.getDefaults().getColor( lookAndFeelKey );
	    if( result == null )
	        return defaultColor;
	    
	    if( brighter )
	        result = result.brighter();
	    
	    return result;
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
