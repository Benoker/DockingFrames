/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.station.toolbar;

import java.awt.Component;

import bibliothek.gui.dock.ComponentDockable;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A factory for reading and writing {@link ComponentDockable}s. Since there is
 * no way to guess which {@link Component} is actually shown by the
 * {@link ComponentDockable} this factory does never create new
 * {@link ComponentDockable}s nor does it store any information.
 * 
 * @author Benjamin Sigg
 * @deprecated will be removed
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
description="to be removed")
public class ToolbarPartDockFactory extends DummyDockFactory<ComponentDockable>{
	/** unique unmodifiable identifier of this factory */
	public static final String ID = "ToolbarPartDockFactory";

	@Override
	public String getID(){
		return ID;
	}
}
