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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocator;

/**
 * A default implementation of {@link DockRelocatorEvent}.
 * @author Benjamin Sigg
 */
public class DefaultDockRelocatorEvent implements DockRelocatorEvent{
	private boolean cancel;
	private boolean drop;
	private boolean forbid;
	private DockController controller;
	private Dockable dockable;
	private Dockable[] implicit;
	private DockStation target;
	
	/**
	 * Creates a new event.
	 * @param controller the controller in whose realm the event happens
	 * @param dockable the element that is moved around
	 * @param target the potential parent of <code>dockable</code>
	 */
	public DefaultDockRelocatorEvent( DockController controller, Dockable dockable, DockStation target ){
		this( controller, dockable, new Dockable[]{}, target );
	}
	
	/**
	 * Creates a new event.
	 * @param controller the controller in whose realm the event happens
	 * @param dockable the element that is moved around
	 * @param implicit the elements that change their position too
	 * @param target the potential parent of <code>dockable</code>
	 */
	public DefaultDockRelocatorEvent( DockController controller, Dockable dockable, Dockable[] implicit, DockStation target ){
		this.implicit = implicit;
		this.controller = controller;
		this.dockable = dockable;
		this.target = target;
	}
	
	public void cancel(){
		cancel = true;	
	}

	public void drop(){
		drop = true;
	}

	public void forbid(){
		forbid = true;
	}

	public DockController getController(){
		return controller;
	}

	public Dockable getDockable(){
		return dockable;
	}

	public Dockable[] getImplicitDockables(){
		return implicit;
	}
	
	public DockRelocator getSource(){
		return controller.getRelocator();
	}

	public DockStation getTarget(){
		return target;
	}

	public boolean isCanceled(){
		return cancel;
	}

	public boolean isDropping(){
		return drop;
	}

	public boolean isForbidden(){
		return forbid;
	}
	
}
