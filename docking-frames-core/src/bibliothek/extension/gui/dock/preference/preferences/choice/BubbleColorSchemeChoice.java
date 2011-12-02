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
import bibliothek.extension.gui.dock.theme.IdentifiedColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme.Distribution;
import bibliothek.extension.gui.dock.theme.bubble.SimpleBubbleColorScheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A list of {@link ColorScheme}s for {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleColorSchemeChoice extends DefaultChoice<ColorScheme> {
	/**
	 * Creates a new choice
	 * @param properties default settings
	 */
	public BubbleColorSchemeChoice( DockProperties properties ){
		super( properties.getController() );

		addLinked( "rgb", "preference.theme.bubble.color.rgb", new IdentifiedColorScheme( "rgb", new BubbleColorScheme( Distribution.RGB ) ) );
		addLinked( "rbg", "preference.theme.bubble.color.rbg", new IdentifiedColorScheme( "rbg", new BubbleColorScheme( Distribution.RBG ) ) );

		addLinked( "grb", "preference.theme.bubble.color.grb", new IdentifiedColorScheme( "grb", new BubbleColorScheme( Distribution.GRB ) ) );
		addLinked( "gbr", "preference.theme.bubble.color.gbr", new IdentifiedColorScheme( "gbr", new BubbleColorScheme( Distribution.GBR ) ) );

		addLinked( "brg", "preference.theme.bubble.color.brg", new IdentifiedColorScheme( "brg", new BubbleColorScheme( Distribution.BRG ) ) );
		addLinked( "bgr", "preference.theme.bubble.color.bgr", new IdentifiedColorScheme( "bgr", new BubbleColorScheme( Distribution.BGR ) ) );

		addLinked( "blop", "preference.theme.bubble.color.blops", new IdentifiedColorScheme( "blops", SimpleBubbleColorScheme.BLOPS ) );
		addLinked( "bright", "preference.theme.bubble.color.bright", new IdentifiedColorScheme( "bright", SimpleBubbleColorScheme.BRIGHT ) );
		addLinked( "looAndFeel", "preference.theme.bubble.color.system", new IdentifiedColorScheme( "system", SimpleBubbleColorScheme.LOOK_AND_FEEL ) );

		if( properties != null ) {
			setDefaultChoice( "rgb" );
		}
	}
}
