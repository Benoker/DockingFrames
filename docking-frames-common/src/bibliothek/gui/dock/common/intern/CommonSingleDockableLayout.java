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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.dock.common.SingleCDockable;

/**
 * An intermediate representation of the layout of a {@link CommonDockable} that
 * is connected with a {@link SingleCDockable}.
 * @author Benjamin Sigg
 */
public class CommonSingleDockableLayout {
	/** the unique identifier of the dockable */
	private String id;
	
	/** whether {@link #setArea(String)} was ever called */
	private boolean areaSet;
	
	/** the working-area with which the dockable is associated */
	private String area;
	
	/**
	 * Sets the unique identifier of the dockable.
	 * @param id the unique identifier
	 */
	public void setId( String id ){
		this.id = id;
	}
	
	/**
	 * Gets the unique identifier of the dockable
	 * @return the unique identifier
	 */
	public String getId(){
		return id;
	}
	
	/**
	 * Tells whether {@link #setArea(String)} was ever called. This method returns
	 * <code>false</code> if this layout represents a layout that was created with
	 * a version of the framework that was before 1.1.0.
	 * @return whether {@link #setArea(String)} was ever called
	 */
	public boolean isAreaSet(){
		return areaSet;
	}
	
	/**
	 * Sets the working-area of the dockable.
	 * @param area the working-area, can be <code>null</code>
	 */
	public void setArea( String area ){
		areaSet = true;
		this.area = area;
	}
	
	/**
	 * Gets the working-area of the dockable.
	 * @return the working-area, can be <code>null</code>
	 */
	public String getArea(){
		return area;
	}
}
