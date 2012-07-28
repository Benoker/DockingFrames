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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.dock.DockElement;

import bibliothek.gui.dock.perspective.PerspectiveElement;

/**
 * An extension for {@link DefaultFrontendPerspectiveCache} (or any other {@link FrontendPerspectiveCache}),
 * this extension adds new types of elements to the cache.
 * @author Benjamin Sigg
 */
public interface FrontendPerspectiveCacheExtension {
	/**
	 * Converts <code>element</code> into a {@link PerspectiveElement}.
	 * @param id the unique identifier of the element, may be <code>null</code>
	 * @param element the element to convert
	 * @param isRootStation whether <code>element</code> is used as a root station
	 * @return the converted element or <code>null</code> if this extension does not know how to
	 * handle <code>element</code>
	 */
	public PerspectiveElement get( String id, DockElement element, boolean isRootStation );
	
	/**
	 * Gets the unique identifier of <code>element</code>.
	 * @param element the element whose unique identifier is searched
	 * @return the unique identifier or <code>null</code> if not found
	 */
	public String get( PerspectiveElement element );
}
