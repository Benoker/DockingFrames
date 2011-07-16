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
package bibliothek.gui.dock.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.themes.border.DockBorder;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * Represents a {@link BorderModifier} that is used by a component that shows a {@link DockAction}.
 * @author Benjamin Sigg
 */
public interface ActionDockBorder extends DockBorder{
	/** the kind of {@link UIValue} this is */
	public static final Path KIND = DockBorder.KIND.append( "action" );
	
	/**
	 * Gets the action which is represented by this border.
	 * @return the action
	 */
	public DockAction getAction();
	
	/**
	 * Gets the element which owns the action.
	 * @return the owner of the action
	 */
	public Dockable getDockable();
}
