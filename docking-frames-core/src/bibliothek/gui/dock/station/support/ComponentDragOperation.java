/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station.support;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.StationDragOperation;

/**
 * This implementation of {@link StationDragOperation} will call
 * {@link Component#repaint()} on creation and when either the
 * operation succeeeds or was canceled.
 * @author Benjamin Sigg
 */
public abstract class ComponentDragOperation implements StationDragOperation{
	private Dockable dockable;
	private Component component;
	
	/**
	 * Creates a new operation
	 * @param dockable the item that is removed
	 * @param component the component that needs to be repainted
	 */
	public ComponentDragOperation( Dockable dockable, Component component ){
		this.dockable = dockable;
		this.component = component;
		component.repaint();
	}
	
	public void canceled(){
		dockable = null;
		component.repaint();
		destroy();
	}
	
	public void succeeded(){
		dockable = null;
		component.repaint();
		destroy();
	}
	
	/**
	 * Gets the dockable that is moved around or <code>null</code> if this
	 * operation is no longer required
	 * @return the item that is dragged or <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	/**
	 * Called once this operation is no longer required
	 */
	protected abstract void destroy();
}
