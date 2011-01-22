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
package bibliothek.extension.gui.dock.preference.preferences;

import bibliothek.extension.gui.dock.preference.preferences.choice.BubbleColorSchemeChoice;
import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * Allows to set the {@link ColorScheme} of the {@link BubbleTheme}.
 * @author Benjamin Sigg
 */
public class BubbleColorSchemePreference extends ChoiceDockPropertyPreference<ColorScheme>{
	/**
	 * Creates a new preference.
	 * @param properties the properties which is read by this preference
	 */
	public BubbleColorSchemePreference( DockProperties properties ){
		super( properties, BubbleTheme.BUBBLE_COLOR_SCHEME, new Path( "dock.theme.bubble.colorscheme" ), new BubbleColorSchemeChoice( properties ) );
		
		setLabelId( "preference.theme.bubble.color.label" );
		setDescriptionId( "preference.theme.bubble.color.description" );
	}
}
