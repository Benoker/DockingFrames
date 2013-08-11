/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.component;

import java.awt.Component;

import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.DockProperties;

/**
 * This interface allows a client to modify many {@link Component}s that are used by the framework.<br>
 * Instances of this interface can be registered at the {@link DockComponentManager}, and they will receive
 * an event whenever a {@link Component} is added or removed.<br>
 * Please use this interface as a last resort when dealing with issues - many settings can be applied in more
 * typesafe manners using the {@link DockProperties} or the {@link ThemeManager}.
 * @author Benjamin Sigg
 */
public interface DockComponentConfiguration {
	/**
	 * Called if a new {@link Component} was discovered that needs a configuration.
	 * @param event information about the new component
	 */
	public void configure( DockComponentConfigurationEvent event );

	/**
	 * Called if a {@link Component} is about to be removed. This configuration may undo all the changes
	 * it made.
	 * @param event information about the component that is about to be removed
	 */
	public void unconfigure( DockComponentConfigurationEvent event );
}
