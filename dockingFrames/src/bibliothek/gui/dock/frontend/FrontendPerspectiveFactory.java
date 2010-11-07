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
package bibliothek.gui.dock.frontend;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveElement;

/**
 * This factory is used by a {@link DockFrontend} to create a {@link Perspective}. The factory
 * will be provided with the registered {@link DockElement}s of the frontend (root-stations and
 * dockables) and has to convert them into {@link PerspectiveElement}s.<br>
 * There are two good approaches for this job:
 * <ul>
 * 	<li>When adding elements to the {@link DockFrontend} the client creates a map with matching {@link PerspectiveElement}s </li>
 * 	<li>The factory analyzes type and id of elements and creates new {@link PerspectiveElement}s.</li>
 * </ul>
 * @author Benjamin Sigg
 */
public interface FrontendPerspectiveFactory {
	/**
	 * Given an <code>element</code> that was earlier created by the client and added to a 
	 * {@link DockFrontend}, this method converts <code>element</code> to a {@link PerspectiveElement}.<br>
	 * When called multiple times with the same arguments, then this method is free to either return
	 * different objects or to always return the same object.
	 * @param id the unique identifier under which <code>element</code> is known to the {@link DockFrontend}
	 * @param element the element whose perspective must be created
	 * @param isRootStation whether <code>element</code> is registered as root-station at the {@link DockFrontend}
	 * or as {@link Dockable}
	 * @return the perspective, must not be <code>null</code>
	 */
	public PerspectiveElement get( String id, DockElement element, boolean isRootStation );
}
