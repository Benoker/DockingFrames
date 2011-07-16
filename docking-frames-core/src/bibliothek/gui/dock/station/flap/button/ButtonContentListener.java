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
package bibliothek.gui.dock.station.flap.button;

import bibliothek.gui.Dockable;

/**
 * A listener that is added to a {@link ButtonContent} to receive events when the
 * content changes.
 * @author Benjamin Sigg
 */
public interface ButtonContentListener {
	/**
	 * Called if any property of <code>dockable</code> changed such that a property 
	 * of <code>content</code> may have changed.
	 * @param content the content which may now have a different behavior
	 * @param dockable the element which caused the event
	 */
	public void changed( ButtonContent content, Dockable dockable );
}
