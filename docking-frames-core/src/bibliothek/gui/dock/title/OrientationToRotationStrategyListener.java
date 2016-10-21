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
package bibliothek.gui.dock.title;

import bibliothek.gui.Dockable;

/**
 * A listener to a {@link OrientationToRotationStrategy}, gets informed if the rotation of some
 * text changes.
 * @author Benjamin Sigg
 */
public interface OrientationToRotationStrategyListener {
	/**
	 * Informs that the rotation of the text on any <code>title</code> representing <code>dockable</code> has changed. 
	 * Any argument of this method can be <code>null</code>, in such a case it functions as wildcard. For example
	 * if <code>title</code> is <code>null</code> than any {@link DockTitle} representing <code>dockable</code> needs
	 * an update.
	 * @param dockable the affected dockable, <code>null</code> if all dockables are affected
	 * @param title the affected title, <code>null</code> if all titles are affected
	 */
	public void rotationChanged( Dockable dockable, DockTitle title );
}
