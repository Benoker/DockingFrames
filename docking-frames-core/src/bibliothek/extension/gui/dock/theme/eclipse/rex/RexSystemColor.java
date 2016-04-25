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

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;

/**
 * A set of colors used to set up the {@link ColorScheme}s of the {@link EclipseTheme}.
 * @author Janni Kovacs
 */
public class RexSystemColor {
	private RexSystemColor() {
		// nothing to do
	}

	public static Color getActiveColor() {
	    return decide( LookAndFeelColors.TITLE_SELECTION_BACKGROUND + ".[brighter]", SystemColor.activeCaption.brighter() );
	}

	public static Color getActiveColorGradient() {
		return decide( LookAndFeelColors.TITLE_SELECTION_BACKGROUND, SystemColor.activeCaption );
	}

	public static Color getInactiveColor() {
		//return decide( "MenuItem.background", SystemColor.inactiveCaption );
		return decide( LookAndFeelColors.TITLE_BACKGROUND + ".[darker]", SystemColor.inactiveCaption.darker() );
	}

	public static Color getInactiveColorGradient() {
		//return decide( "MenuItem.background.[brighter]", SystemColor.inactiveCaption.brighter() );
		return decide( LookAndFeelColors.PANEL_BACKGROUND, SystemColor.inactiveCaption );
	}
	
	public static Color getActiveTextColor(){
	    return decide( LookAndFeelColors.TITLE_SELECTION_FOREGROUND, SystemColor.activeCaptionText );
	}
	
	public static Color getInactiveTextColor(){
	    return decide( LookAndFeelColors.TITLE_FOREGROUND, SystemColor.inactiveCaptionText );
	}

	public static Color getBorderColor(){
//	    return decide( LookAndFeelColors.CONTROL_SHADOW, SystemColor.controlShadow );
		return decide( LookAndFeelColors.WINDOW_BORDER + ".[darker]", SystemColor.windowBorder.darker() );
	}
	
	private static Color decide(String lookAndFeelKey, Color defaultColor ) {
	    boolean brighter = lookAndFeelKey.endsWith( "[brighter]" );
	    boolean darker = lookAndFeelKey.endsWith( "[darker]" );
	    
	    if( brighter || darker )
	        lookAndFeelKey = lookAndFeelKey.substring( 0, lookAndFeelKey.lastIndexOf( '.' ) );
	    
	    Color result = DockUI.getDefaultDockUI().getColors().getColor( lookAndFeelKey );
	    if( result == null )
	        return defaultColor;
	    
	    if( brighter )
	        result = result.brighter();
	    
	    if( darker )
	    	result = result.darker();
	    
	    return result;
	}
}
