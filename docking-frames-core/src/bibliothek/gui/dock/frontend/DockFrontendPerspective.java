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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.perspective.PredefinedPerspective;

/**
 * Represents the layout that is created and managed by a {@link DockFrontend}, offers
 * methods to easily modify that layout. Clients should call {@link DockFrontend#getPerspective(boolean)}
 * to acquire an object of this type.<br>
 * This interface is only a wrapper around {@link Perspective} and offers some shortcuts to implement
 * tasks more easily, clients could also access the {@link Perspective} directly and completely
 * ignore this object.
 * @author Benjamin Sigg
 */
public interface DockFrontendPerspective {
	/**
	 * Gets the internal representation of this layout.
	 * @return the internal representation
	 */
	public PredefinedPerspective getPerspective();
	
	/**
	 * Gets a {@link PropertyTransformer} which is used to read and write
	 * {@link DockableProperty}s.
	 * @return the transformer
	 */
	public PropertyTransformer getPropertyTransformer();
	
	/**
	 * Allows access to the root {@link DockStation} named <code>root</code>.
	 * @param root the name of the station
	 * @return the root station or <code>null</code> if not found
	 */
	public PerspectiveStation getRoot( String root );
	
	/**
	 * Applies the current layout to the {@link DockFrontend} which created this perspective.<br>
	 * Please note that implementations may restrict what information is applied, specifically:
	 * <ul>
	 * 	<li>It may not be possible to add/remove root-stations through this method.</li>
	 *  <li>Invisible elements may not be touched</li>
	 * </ul>
	 */
	public void apply();
	
	/**
	 * Stores this perspective in the {@link DockFrontend} using <code>name</code> as key. The same restrictions
	 * as found in {@link #apply()} may apply to this method.
	 * @param name the name of this layout
	 */
	public void store(String name);
}
