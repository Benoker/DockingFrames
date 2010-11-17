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
package bibliothek.gui.dock.themes;

import java.awt.Color;

import bibliothek.gui.dock.util.UIScheme;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A <code>ColorScheme</code> is a collection of colors and bridges.<br>
 * <b>Warning: </b> This interface will be replaced in version 1.1.0. 
 * 
 * @author Benjamin Sigg
 */
@Todo(priority=Todo.Priority.MAJOR, compatibility=Compatibility.BREAK_MINOR, target=Version.VERSION_1_1_0,
		description="The ColorManager should know of ColorSchemes, the method \'transmitAll\' will be removed")
public interface ColorScheme extends UIScheme<Color, DockColor, ColorBridge> {
	/**
	 * The name used in a {@link ExtensionName} to ask for an additional {@link ColorScheme}. The
	 * additional {@link ColorScheme}s will be applied after the standard scheme was applied. This
	 * means that any setting of the base {@link ColorScheme} is overridden, including those settings
	 * made by the user. Extensions should provide a mechanism such that the user can change
	 * the extension-colors.
	 */
	public static final Path EXTENSION_NAME = new Path( "dock.colorscheme" );
	
	/**
	 * A parameter linking to the {@link ColorScheme} that requests this extension.
	 */
	public static final String COLOR_SCHEME_PARAMETER = "scheme";
	
//    /**
//     * Called when the {@link LookAndFeel} or a color of the
//     * {@link LookAndFeelColors} changed and this scheme
//     * perhaps needs to update its colors.
//     */
//    public void updateUI();
}
