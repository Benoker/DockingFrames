/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.dock.common.intern.CDockable;

/**
 * The parent of a dockable that is maximized.
 * @author Benjamin Sigg
 */
public interface MaximizedModeArea {
	/**
	 * Tells this parent to show <code>dockable</code> maximized,
	 * only one dockable may be maximized at any time.
	 * @param dockable the maximized element, <code>null</code> to indicate
	 * that no element should be maximized.
	 */
	public void setMaximized( CDockable dockable );
	
	/**
	 * Gets the currently maximized element.
	 * @return the currently maximized dockable, can be <code>null</code>
	 */
	public CDockable getMaximized();
}
