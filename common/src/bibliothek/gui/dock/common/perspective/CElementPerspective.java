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
package bibliothek.gui.dock.common.perspective;


/**
 * Represents a dockable or a station in a {@link CPerspective}.
 * @author Benjamin Sigg
 */
public interface CElementPerspective {
	/**
	 * Gets the internal representation for this element.<br> 
	 * If {@link #asDockable()} returns a non-<code>null</code> value, then <code>intern().asDockable()</code> must not
	 * return <code>null</code> either.<br>
	 * If {@link #asStation()} returns a non-<code>null</code> value, then <code>intern().asStation()</code> must not
	 * return <code>null</code> either.<br>
	 * @return the internal representation
	 */
	public CommonElementPerspective intern();
	
	/**
	 * Gets <code>this</code> as dockable, if this is a dockable.
	 * @return <code>this</code> or <code>null</code>
	 */
	public CDockablePerspective asDockable();
	
	/**
	 * Gets <code>this</code> as station, if this is a station.
	 * @return <code>this</code> or <code>null</code>
	 */
	public CStationPerspective asStation();
}
