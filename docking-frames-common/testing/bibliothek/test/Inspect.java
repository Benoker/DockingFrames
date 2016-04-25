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
package bibliothek.test;

/**
 * Represents some field or data that can be inspected. 
 * @author Benjamin Sigg
 */
public interface Inspect {
	/**
	 * Gets a human readable name for this field.
	 * @return a name
	 */
	public String getName();
	
	/**
	 * Gets the value represented by this field. This value will be converted
	 * into a {@link String} to be shown on the screen.
	 * @return the value, can be <code>null</code>
	 */
	public Object getValue();
	
	/**
	 * Gets all the children of this inspect.
	 * @return all the children of this inspect, the result may be <code>null</code>
	 * or contain <code>null</code> entries. It may also contain data that has
	 * cyclic dependencies.
	 */
	public Object[] getChildren();
	
	/**
	 * Updates the contents of this {@link Inspect}. This method may only be called by the
	 * EDT (Event Dispatcher Thread).
	 * @return <code>true</code> if either name, value or children changed, <code>false</code>
	 * if nothing was updated
	 */
	public boolean update();
}
