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
package bibliothek.gui.dock.action.popup;

import java.awt.Component;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * The default implementation of a {@link ActionPopupMenuFactory} creates
 * new {@link DefaultActionPopupMenu}s.
 * @author Benjamin Sigg
 */
public class DefaultActionPopupMenuFactory implements ActionPopupMenuFactory{
	public ActionPopupMenu createMenu( Component owner, Dockable dockable, DockActionSource actions, Object source ){
		return new DefaultActionPopupMenu( dockable, actions );
	}
}
