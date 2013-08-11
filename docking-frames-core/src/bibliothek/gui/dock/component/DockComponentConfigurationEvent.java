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
 * Describes a {@link Component} and its source {@link DockComponentRoot}.
 * @author Benjamin Sigg
 */
public class DockComponentConfigurationEvent {
	/** the parent of <code>component</code> */
	private DockComponentRoot root;
	
	/** a {@link Component} that needs a configuration*/
	private Component component;
	
	/**
	 * Creates a new event.
	 * @param root the parent of <code>component</code>
	 * @param component a {@link Component} that needs a configuration
	 */
	public DockComponentConfigurationEvent( DockComponentRoot root, Component component ){
		this.root = root;
		this.component = component;
	}
	
	/**
	 * Gets the parent of {@link #getComponent() the component},
	 * @return the parent, not <code>null</code>
	 */
	public DockComponentRoot getRoot() {
		return root;
	}
	
	/**
	 * Gets the {@link Component} that needs a configuration.
	 * @return the component, not <code>null</code>
	 */
	public Component getComponent() {
		return component;
	}
}
