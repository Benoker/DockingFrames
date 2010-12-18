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
package bibliothek.gui.dock.security;

import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link StackDockStation} which can operate in a restricted environment.
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary and will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="Remove this class, no replacement necessary" )
public class SecureStackDockStation extends StackDockStation {
	
	/**
	 * Creates a new station
	 */
	public SecureStackDockStation(){
		this( null );
	}
	
	/**
	 * Creates a new station and sets the theme of this station
	 * @param theme the theme, can be <code>null</code>
	 */
	public SecureStackDockStation( DockTheme theme ){
		super( theme, true );
	}
	
	@Override
	public String getFactoryID(){
		return SecureStackDockStationFactory.ID;
	}
}
