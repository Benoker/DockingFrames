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
package bibliothek.gui.dock.dockable;

import javax.swing.Icon;

/**
 * Tells whether the <code>null</code> {@link Icon} should be replaced by the default icon or not.
 * @author Benjamin Sigg
 */
public enum IconHandling {
	/** The <code>null</code> {@link Icon} is kept, i.e. it is possible not to show an icon */
	KEEP_NULL_ICON,
	
	/** The <code>null</code> {@link Icon} always gets replaced by the default icon (if present) */
	REPLACE_NULL_ICON
}
