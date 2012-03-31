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
package bibliothek.gui.dock.station;

import java.awt.Dimension;

import bibliothek.gui.Dockable;

/**
 * This class offers information about a {@link Dockable} that is going to be dropped. The properties include:
 * <ul>
 * 	<li> The {@link #getDockable() dockable} itself </li>
 *  <li> The location of the mouse on the screen {@link #getMouseX() mouseX} and {@link #getMouseY() mouseY} </li>
 *  <li> the location of the title on the screen {@link #getTitleX() titleX} and {@link #getTitleY() titleY} </li>
 * </ul>
 * @author Benjamin Sigg
 */
public class StationDropItem {
	private int mouseX;
	private int mouseY;
	private int titleX;
	private int titleY;
	private Dockable dockable;
	private Dimension size;
	private Dimension minimum;
	
	/**
	 * Creates a new item
	 * @param mouseX the position of the mouse
	 * @param mouseY the position of the mouse
	 * @param titleX the location of the title
	 * @param titleY the location of the title
	 * @param dockable the item that is moved around
	 */
	public StationDropItem( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable ){
		this( mouseX, mouseY, titleX, titleY, dockable, dockable.getComponent().getSize(), dockable.getComponent().getMinimumSize() );
	}

	/**
	 * Creates a new item
	 * @param mouseX the position of the mouse
	 * @param mouseY the position of the mouse
	 * @param titleX the location of the title
	 * @param titleY the location of the title
	 * @param dockable the item that is moved around
	 * @param size the original size of <code>dockable</code>
	 * @param minimum the original minimum size of <code>dockable</code>
	 */
	public StationDropItem( int mouseX, int mouseY, int titleX, int titleY, Dockable dockable, Dimension size, Dimension minimum ){
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.titleX = titleX;
		this.titleY = titleY;
		this.dockable = dockable;
		this.size = size;
		this.minimum = minimum;
	}
	
	/**
	 * Gets the item that is moved around
	 * @return the item that is moved around
	 */
	public Dockable getDockable(){
		return dockable;
	}
	
	/**
	 * Gets the original size of the {@link Dockable}, the size it had when the drag and drop operation started.
	 * @return the original size
	 */
	public Dimension getOriginalSize(){
		return size;
	}
	
	/**
	 * Gets the original minimum size of the {@link Dockable}.
	 * @return the original minimum sizes
	 */
	public Dimension getMinimumSize(){
		return minimum;
	}
	
	/**
	 * Gets the position of the mouse on the screen
	 * @return the x coordinate
	 */
	public int getMouseX(){
		return mouseX;
	}
	
	/**
	 * Gets the position of the mouse on the screen
	 * @return the y coordinate
	 */
	public int getMouseY(){
		return mouseY;
	}
	
	/**
	 * Gets the position of the title on the screen
	 * @return the x coordinate
	 */
	public int getTitleX(){
		return titleX;
	}
	
	/**
	 * Gets the position of the title on the screen
	 * @return the y coordinate
	 */
	public int getTitleY(){
		return titleY;
	}
}
