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
package bibliothek.gui.dock.perspective;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.DockFactory;

/**
 * Representation of a {@link DockElement}
 * @author Benjamin Sigg
 */
public interface PerspectiveElement {
	/**
	 * Gets the identifier of the {@link DockFactory} that will be able to
	 * understand the layout information this element generates. 
	 * @return the factories identifier
	 */
	public String getFactoryID();
	
	/**
	 * Returns the representation of <code>this</code> as {@link PerspectiveStation}
	 * @return either <code>this</code>, a representation of <code>this</code> or <code>null</code>
	 */
	public PerspectiveStation asStation();
	
	/**
	 * Returns the representation of <code>this</code> as {@link PerspectiveDockable}.
	 * @return either <code>this</code>, a representation of <code>this</code> or <code>null</code>
	 */
	public PerspectiveDockable asDockable();
}
