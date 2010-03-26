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
import bibliothek.gui.dock.layout.LocationEstimationMap;

/**
 * A {@link DockConverter} which can not only store and load the contents of an
 * element, but also create a new {@link DockElement} with the content.
 * @author Benjamin Sigg
 * @param <D> the type of element which can be written and read by this factory
 * @param <L> the type of object that stores the contents of a <code>D</code>. If
 * clients cannot guarantee that always the same factory will be mapped
 * to the same identifier, then <code>L</code> should be set to {@link Object}
 * and the methods which receive an <code>L</code> should use 
 * <code>instanceof</code> before casting the argument.
 */
public interface DockFactory<D extends DockElement, L> extends DockConverter<D, L>{
	/**
	 * Tries to estimate the {@link DockableProperty}s of the children of the
	 * station which is represented by <code>layout</code>.<br>
	 * 
	 * The children of this station accessible through <code>children.getChild( ... )</code>, this
	 * factory may also access the leafs in the tree of {@link Dockable}s through
	 * <code>children.getSubChild(...)</code>.<br>
	 * Note: this method must not set the successor of any {@link DockableProperty},
	 * it is the callers responsibility to handle chains of stations.
	 * @param layout this station
	 * @param children the children of the station, this method should call
	 * {@link DockLayoutInfo#setLocation(DockableProperty)} or {@link LocationEstimationMap#setLocation(int, DockableProperty)} and
	 * {@link LocationEstimationMap#setLocation(int, int, DockableProperty)} for as many children as possible
	 */
	public void estimateLocations( L layout, LocationEstimationMap children );
	
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
