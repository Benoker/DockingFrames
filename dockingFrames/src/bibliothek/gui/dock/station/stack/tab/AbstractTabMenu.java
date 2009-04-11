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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Component;

import bibliothek.gui.Dockable;

/**
 * An abstract implementation of {@link TabMenu} based on a real {@link Component}.
 * @author Benjamin Sigg
 */
public abstract class AbstractTabMenu extends AbstractTabPaneComponent implements TabMenu{
	/** the elements of this menu */
	private Dockable[] dockables;
	
	/**
	 * Creates a new menu.
	 * @param parent the parent of this menu
	 * @param dockables the children, not <code>null</code>
	 */
	public AbstractTabMenu( TabPane parent, Dockable[] dockables ){
		super( parent );
		
		if( dockables == null )
			throw new IllegalArgumentException( "dockables must not be null" );
		
		this.dockables = dockables;
	}
	
	public Dockable[] getDockables(){
		return dockables;
	}
}
