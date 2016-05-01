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

import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * An extension to a {@link DockTheme}, may be used by the {@link DockTheme} to
 * override some settings.
 * @author Benjamin Sigg
 */
public interface DockThemeExtension {
	/**
	 * The name of a {@link ExtensionName} creating {@link DockThemeExtension}s.
	 */
	public static final Path DOCK_THEME_EXTENSION = new Path( "dock.theme" );
    
	/**
	 * The parameter name for the theme that is extended.
	 */
	public static final String THEME_PARAMETER = "theme";
	
	
	/**
	 * Called by the {@link DockTheme} before it installs itself.
	 * @param controller the controller on which the theme is installed
	 * @param theme the theme that is about to get installed
	 */
	public void install( DockController controller, DockTheme theme );
	
	/**
	 * Called by the {@link DockTheme} after it installed itself.
	 * @param controller the controller on which the theme is installed
	 * @param theme the theme that was installed
	 */
	public void installed( DockController controller, DockTheme theme );

	/**
	 * Called by the {@link DockTheme} after it was uninstalled from <code>controller</code>.
	 * @param controller the controller which no longer uses <code>theme</code>
	 * @param theme the theme calling using this extension
	 */
	public void uninstall( DockController controller, DockTheme theme);
	
}
