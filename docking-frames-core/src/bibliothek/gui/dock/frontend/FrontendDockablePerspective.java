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
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.dockable.DefaultDockablePerspective;

/**
 * This {@link DefaultDockablePerspective} represents a {@link DefaultDockable} that was added
 * to a {@link DockFrontend} with a unique identifier.
 * @author Benjamin Sigg
 */
public class FrontendDockablePerspective extends DefaultDockablePerspective{
	private String id;
	
	/**
	 * Creates a new representation.
	 * @param id the unique identifier of this perspective, not <code>null</code>
	 */
	public FrontendDockablePerspective( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		this.id = id;
	}
	
	/**
	 * Gets the unique identifier this {@link DefaultDockable} has on its 
	 * {@link DockFrontend}.
	 * @return the unique identifier, not <code>null</code>
	 */
	public String getId(){
		return id;
	}
}
