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

import javax.swing.LookAndFeel;

import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.gui.dock.util.laf.LookAndFeelColors;
import bibliothek.util.Path;

/**
 * A <code>ColorScheme</code> is a collection of colors and bridges.
 * 
 * @author Benjamin Sigg
 */
public interface ColorScheme {
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
	
    /**
     * Searches for a color that can be used for the identifier <code>id</code>.
     * @param id an identifier of some color
     * @return some color or <code>null</code>
     */
    public Color getColor( String id );
    
    /**
     * Searches for a factory for a bridge that can be used for a specific
     * kind of {@link DockColor}.
     * @param kind the kind of color the provider should support 
     * @return some a factory for a bridge or <code>null</code>
     */
    public ColorBridgeFactory getBridgeFactory( Path kind );
    
    /**
     * Transmits all values in this scheme to <code>manager</code>.
     * @param priority the priority to use when registering colors
     * and providers.
     * @param manager the manager to fill
     */
    public void transmitAll( Priority priority, ColorManager manager );
    
    /**
     * Called when the {@link LookAndFeel} or a color of the
     * {@link LookAndFeelColors} changed and this scheme
     * perhaps needs to update its colors.
     * @return <code>true</code> if anything changed, <code>false</code>
     * if this scheme was not changed.
     */
    public boolean updateUI();
}
