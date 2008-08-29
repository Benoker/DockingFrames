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

package bibliothek.gui.dock;

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockConverter;
import bibliothek.gui.dock.layout.DockLayoutInfo;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A {@link DockConverter} which can not only store and load the contents of an
 * element, but also create a new {@link DockElement} with the content.
 * @author Benjamin Sigg
 * @param <D> the type of element which can be written and read by this factory
 * @param <L> the type of object that stores the contents of a <code>D</code>
 */
public interface DockFactory<D extends DockElement, L> extends DockConverter<D, L>{
	/**
	 * Tries to estimate the {@link DockableProperty}s of the children of the
	 * station which is represented by <code>layout</code>.
	 * @param layout this station
	 * @param location the location of <code>layout</code>, may be <code>null</code>
	 * @param children the children of the station, this method should call
	 * {@link DockLayoutInfo#setLocation(DockableProperty)} for as many
	 * children as possible
	 */
	public void estimateLocations( L layout, DockableProperty location, Map<Integer, DockLayoutInfo> children );
	
    /**
     * Creates a new {@link DockElement} and changes the layout of the new 
     * element such that is matches <code>layout</code>.
     * @param layout the new layout
     * @param children some children, note that the map may not contain all elements
     * which were present when the layout was created. 
     * @return a new element or <code>null</code> if layout can't be used
     */
    public D layout( L layout, Map<Integer, Dockable> children );
    
    /**
     * Creates a new {@link DockElement} and changes the layout of the new 
     * element such that is matches <code>layout</code>. This method should
     * not add any children to the element.
     * @param layout the new layout
     * @return a new element or <code>null</code> if layout can't be used
     */
    public D layout( L layout );
}
