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

/**
 * A {@link DockComponentRoot} is a representation of a set of {@link Component}s, these {@link Component}s can be configured
 * by a client by applying a {@link DockComponentConfiguration}. {@link DockComponentRoot}s may be nested, but
 * the intersection of the sets of {@link Component}s of two roots should be empty. 
 * @author Benjamin Sigg
 */
public interface DockComponentRoot {
	/**
	 * Sets a configuration which is to be applied to all {@link Component}s (and maybe their children) of this {@link DockComponentRoot}.
	 * @param configuration the new configuration or <code>null</code>.
	 */
	public void setComponentConfiguration( DockComponentConfiguration configuration );
	
	/**
	 * Gets the currently applied configuration.
	 * @return the current configuration, may be <code>null</code>
	 */
	public DockComponentConfiguration getComponentConfiguration();
}
