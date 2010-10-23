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
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;

/**
 * A {@link DockConverter} which can not only store and load the contents of an
 * element, but also create a new {@link DockElement} with the content.
 * @author Benjamin Sigg
 * @param <D> the type of element which can be written and read by this factory
 * @param <P> the type of {@link PerspectiveElement} which can be written and read by this factory
 * @param <L> the type of object that stores the contents of a <code>D</code>. If
 * clients cannot guarantee that always the same factory will be mapped
 * to the same identifier, then <code>L</code> should be set to {@link Object}
 * and the methods which receive a <code>L</code> should use 
 * <code>instanceof</code> before casting the argument.
 */
//public interface DockFactory<D extends DockElement, P extends PerspectiveElement<L>, L> extends DockConverter<D, L>{
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
    
//    /**
//     * Creates an element that can be used by a {@link Perspective} to create a layout
//     * without creating any {@link DockElement}s. This method may return <code>null</code> only
//     * if the client is guaranteed not to use a {@link Perspective}. 
//     * @param layout the new layout
//     * @param children some children, note that the map may not contain all elements
//     * which were present when the layout was created. 
//     * @return the new element, can be <code>null</code>, the return value of {@link PerspectiveElement#getFactoryID()} should
//     * be equal to {@link #getID()}
//     */
//    public P layoutPerspective( L layout, Map<Integer, PerspectiveDockable<?>> children );
//    
//	
//	/**
//	 * Gets the layout information that is associated with this element.
//	 * The layout information can be any {@link Object}. The only restriction
//	 * of the object is, that the associated {@link DockFactory} understands
//	 * how to read that object.  
//	 * @param children a map providing identifiers for the children of this element. The
//	 * identifiers are in the range from 0 (incl.) to <code>children.size()</code> (excl.), 
//	 * the exact same identifiers would be given to {@link DockConverter#getLayout(bibliothek.gui.dock.DockElement, Map)}.
//	 * @return the layout information not <code>null</code>
//	 */
//	public L getPerspectiveLayout( P element, Map<PerspectiveDockable<?>, Integer> children );
}
