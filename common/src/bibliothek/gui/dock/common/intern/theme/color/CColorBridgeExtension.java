/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.theme.color;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.theme.CDockTheme;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;

/**
 * This extension is used by a {@link CDockTheme} to add additional {@link ColorBridge}s.
 * @author Benjamin Sigg
 */
public interface CColorBridgeExtension {
	/**
	 * The name of this extension. 
	 */
	public static final Path EXTENSION_NAME = new Path( "dock.ccolorbridgefactory" );
	
	/**
	 * The name of the parameter that links to the {@link CDockTheme} that uses this factory
	 */
	public static final String PARAMETER_NAME = "theme";
	
	/**
	 * Gets the name of the bridge
	 * @return the name, not <code>null</code>
	 */
	public Path getKey();
	
    /**
     * Creates a new bridge for <code>manager</code>.
     * @param control the control in whose realm the bridge will be used
     * @param manager the manager which will use the bridge
     * @return the new bridge
     */
    public CColorBridge create( CControl control, ColorManager manager );
}
