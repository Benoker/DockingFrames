/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.window;

import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockWindowConfiguration;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * The default implementation of {@link ScreenDockWindowConfiguration} always returns <code>null</code>
 * from its {@link #getConfiguration(ScreenDockStation, Dockable)} method, thus telling the {@link ScreenDockStation}
 * to use a default configuration.<br>
 * This configuration offers an {@link #CONFIGURATION_EXTENSION extension point}, any configuration added through
 * that point will be asked first for a configuration before returning the default value.
 * 
 * @author Benjamin Sigg
 */
public class DefaultScreenDockWindowConfiguration implements ScreenDockWindowConfiguration{
	/** name of an {@link ExtensionName} for adding additional {@link ScreenDockWindowConfiguration}s */
	public static final Path CONFIGURATION_EXTENSION = new Path( "dock.DefaultScreenDockWindowConfiguration" );
	
	/** a parameter pointing to <code>this</code>. */
	public static final String CONFIGURATION_EXTENSION_PARAM = "configuration";
	
	/** additional configurations to consider */
	private ScreenDockWindowConfiguration[] extensions;
	
	/**
	 * Creates a new configuration.
	 * @param controller used to load extension, can be <code>null</code>
	 */
	public DefaultScreenDockWindowConfiguration( DockController controller ){
		if( controller != null ){
			List<ScreenDockWindowConfiguration> list = controller.getExtensions().load( new ExtensionName<ScreenDockWindowConfiguration>( CONFIGURATION_EXTENSION, ScreenDockWindowConfiguration.class, CONFIGURATION_EXTENSION_PARAM, this ) );
			extensions = list.toArray( new ScreenDockWindowConfiguration[ list.size() ] );
		}
		else{
			extensions = new ScreenDockWindowConfiguration[]{};
		}
	}
	
	public WindowConfiguration getConfiguration( ScreenDockStation station, Dockable dockable ){
		for( ScreenDockWindowConfiguration extension : extensions ){
			WindowConfiguration result = extension.getConfiguration( station, dockable );
			if( result != null ){
				return result;
			}
		}
		return null;
	}
}
