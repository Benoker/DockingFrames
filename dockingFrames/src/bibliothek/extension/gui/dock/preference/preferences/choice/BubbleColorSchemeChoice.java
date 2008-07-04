/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.SimpleBubbleColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme.Distribution;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.themes.ColorScheme;

/**
 * A list of {@link ColorScheme}s for {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleColorSchemeChoice extends DefaultChoice<ColorScheme>{
	/**
	 * Creates a new choice
	 */
	public BubbleColorSchemeChoice(){
		DockUI ui = DockUI.getDefaultDockUI();
		
		add( "rgb", ui.getString( "preference.theme.bubble.color.rgb" ), new BubbleColorScheme( Distribution.RGB ) );
		add( "rbg", ui.getString( "preference.theme.bubble.color.rbg" ), new BubbleColorScheme( Distribution.RBG ) );
		
		add( "grb", ui.getString( "preference.theme.bubble.color.grb" ), new BubbleColorScheme( Distribution.GRB ) );
		add( "gbr", ui.getString( "preference.theme.bubble.color.gbr" ), new BubbleColorScheme( Distribution.GBR ) );
		
		add( "brg", ui.getString( "preference.theme.bubble.color.brg" ), new BubbleColorScheme( Distribution.BRG ) );
		add( "bgr", ui.getString( "preference.theme.bubble.color.bgr" ), new BubbleColorScheme( Distribution.BGR ) );

		add( "blop", ui.getString( "preference.theme.bubble.color.blops" ), SimpleBubbleColorScheme.BLOPS );
		add( "bright", ui.getString( "preference.theme.bubble.color.bright" ), SimpleBubbleColorScheme.BRIGHT );
		add( "looAndFeel", ui.getString( "preference.theme.bubble.color.system" ), SimpleBubbleColorScheme.LOOK_AND_FEEL );
		
		setDefaultChoice( "rgb" );
	}
}
