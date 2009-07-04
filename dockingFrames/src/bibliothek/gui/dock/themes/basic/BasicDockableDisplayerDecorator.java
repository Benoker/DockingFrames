/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.themes.basic;

import java.awt.Component;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * A decorator may add a {@link Component} between a {@link BasicDockableDisplayer}
 * and a {@link Dockable}.
 * @author Benjamin Sigg
 */
public interface BasicDockableDisplayerDecorator {
	/**
	 * Sets the element to show on this decorator, can be <code>null</code>
	 * @param component the component which represents <code>dockable</code>
	 * @param dockable the element to show
	 */
	public void setDockable( Component component, Dockable dockable );
	
	/**
	 * Sets the controller in whose realm this decorator works
	 * @param controller the controller
	 */
	public void setController( DockController controller );
	
	/**
	 * Gets the {@link Component} which represents this decorator,
	 * the result of this method may change whenever {@link #setController(DockController)}
	 * or {@link #setDockable(Component, Dockable)} is called.
	 * @return the component or <code>null</code> to show nothing
	 */
	public Component getComponent();
}
