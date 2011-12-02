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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * This strategy tells a {@link CLocation} how to represents a
 * {@link DockableProperty}.
 * @author Benjamin Sigg
 */
public interface CLocationExpandStrategy {
	/**
	 * Expands <code>location</code> by creating a new location that represents
	 * <code>property</code>.
	 * @param location the location to expand, not <code>null</code>
	 * @param property the property to expand, not <code>null</code>, the 
	 * {@link DockableProperty#getSuccessor() successor} can be ignored by this method.
	 * @return the expanded location, can be <code>null</code> to indicate that
	 * <code>property</code> could not be understood
	 */
	public CLocation expand( CLocation location, DockableProperty property );
}
