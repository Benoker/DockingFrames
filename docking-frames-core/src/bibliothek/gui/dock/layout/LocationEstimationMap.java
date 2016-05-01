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
package bibliothek.gui.dock.layout;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;

/**
 * A map used for {@link DockFactory}, telling what children a {@link DockElement} has.<br>
 * This map is also a list of {@link DockLayoutInfo}s. The element at <code>index</code>
 * in this list matches the {@link Dockable} that was stored with key
 * <code>index</code> in the map of {@link DockConverter#getLayout(DockElement, java.util.Map)}.
 * @author Benjamin Sigg
 */
public interface LocationEstimationMap{
	/**
	 * Gets the number of children of this map
	 * @return the total number of keys 
	 */
	public int getChildCount();
	
	/**
	 * Gets the {@link DockLayoutInfo} that is associated with key <code>childIndex</code>.
	 * @param childIndex some key between 0 and {@link #getChildCount()}
	 * @return the info, not <code>null</code>
	 */
	public DockLayoutInfo getChild( int childIndex );
	
	/**
	 * Gets the number of children the item <code>childIndex</code> has. This includes direct
	 * children and grand-children.
	 * @param childIndex the key of some element
	 * @return the number of children of <code>childIndex</code>
	 */
	public int getSubChildCount( int childIndex );
	
	/**
	 * Gets the <code>subChildIndex</code>'th child of <code>childIndex</code>. This includes direct
	 * children and grand-children.
	 * @param childIndex the key to some child
	 * @param subChildIndex the index of some child of <code>childIndex</code>
	 * @return the info, not <code>null</code>
	 */
	public DockLayoutInfo getSubChild( int childIndex, int subChildIndex );
	
	/**
	 * Informs this map that the jump from this station to <code>childIndex</code> can be described
	 * by <code>location</code>.
	 * @param childIndex the key of some child
	 * @param location the location of <code>childIndex</code>, <code>null</code> will be ignored
	 */
	public void setLocation( int childIndex, DockableProperty location );
	
	/**
	 * Informs this map that the path to child <code>childIndex, subChildIndex</code> contains
	 * <code>location</code>. The part <code>location</code> describes the jump from this
	 * station to the child <code>childIndex</code>. 
	 * @param childIndex the key of some child
	 * @param subChildIndex the index of some grand-child
	 * @param location the location, <code>null</code> will be ignored
	 */
	public void setLocation( int childIndex, int subChildIndex, DockableProperty location );
}
