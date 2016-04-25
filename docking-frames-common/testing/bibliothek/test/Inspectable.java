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
 * An {@link Inspectable} is an object that provides access to an {@link Inspect}.<br>
 * <b>Note: </b> This interface is meant for debugging purposes only, subclasses are not
 * required to provide an efficient implementation or even care for memory leaks.
 * @author Benjamin Sigg
 */
public interface Inspectable {
	/**
	 * Gets internal information about this {@link Inspectable}.
	 * @param graph the graph for which the {@link Inspect} is used
	 * @return a new instance of of an {@link Inspect} object
	 */
	public Inspect inspect( InspectionGraph graph );
}
