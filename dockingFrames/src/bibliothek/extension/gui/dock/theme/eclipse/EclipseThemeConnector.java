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
package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;

/**
 * @author Janni Kovacs
 */
public interface EclipseThemeConnector {
	public boolean isTitleBarShown(Dockable d);
		
	/**
	 * Tells whether <code>action</code> should be displayed on the tab
	 * of <code>dockable</code> or on the right side.
	 * @param dockable the owner of <code>action</code>
	 * @param action the action to display
	 * @return <code>true</code> if <code>action</code> should be child of
	 * the tab
	 */
	public boolean isTabAction( Dockable dockable, DockAction action );
}
