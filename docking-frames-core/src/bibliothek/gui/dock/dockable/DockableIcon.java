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
package bibliothek.gui.dock.dockable;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Path;

/**
 * An icon that is used for a {@link Dockable}
 * @author Benjamin Sigg
 */
public abstract class DockableIcon extends DockIcon{
	/** what kind of {@link UIValue} this is */
	public static final Path KIND_DOCKABLE = KIND_ICON.append( "dockable" );
	
	private Dockable dockable;
	
	/**
	 * Creates a new icon
	 * @param id the identifier of the icon
	 * @param dockable the element for which the icon is used
	 */
	public DockableIcon( String id, Dockable dockable ){
		super( id, KIND_DOCKABLE );
		this.dockable = dockable;
	}
	
	/**
	 * Gets the {@link Dockable} for which this icon is used.
	 * @return the dockable
	 */
	public Dockable getDockable(){
		return dockable;
	}
}
