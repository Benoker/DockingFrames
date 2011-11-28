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
package bibliothek.gui.dock.control.relocator;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElementRepresentative;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * Default implementation of {@link InserterSource}. 
 * @author Benjamin Sigg
 */
public class DefaultInserterSource implements InserterSource{
	private int mouseX;
	private int mouseY;
	private int titleX;
	private int titleY;
	private DockStation parent;
	private Dockable child;
	private StationDropOperation operation;
	
	/**
	 * Creates a new {@link InserterSource}.
	 * @param parent the future parent of <code>child</code>
	 * @param child the future child of <code>parent</code>
	 * @param mouseX the x-position of the mouse on the screen
	 * @param mouseY the y-position of the mouse on the screen
	 * @param titleX the x-position of the grabbed {@link DockElementRepresentative}
	 * @param titleY the y-position of the grabbed {@link DockElementRepresentative}
	 */
	public DefaultInserterSource( DockStation parent, Dockable child, int mouseX, int mouseY, int titleX, int titleY ){
		this.parent = parent;
		this.child = child;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.titleX = titleX;
		this.titleY = titleY;
	}
	
	public int getMouseX(){
		return mouseX;
	}

	public int getMouseY(){
		return mouseY;
	}

	public int getTitleX(){
		return titleX;
	}
	
	public int getTitleY(){
		return titleY;
	}
	
	public DockStation getParent(){
		return parent;
	}

	public Dockable getChild(){
		return child;
	}

	/**
	 * Sets the result of {@link #getOperation()}.
	 * @param operation the operation that might be executed, can be <code>null</code>
	 */
	public void setOperation( StationDropOperation operation ){
		this.operation = operation;
	}
	
	public StationDropOperation getOperation(){
		return operation;
	}
	
}
