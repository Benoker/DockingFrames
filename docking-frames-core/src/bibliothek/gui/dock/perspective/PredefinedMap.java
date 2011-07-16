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
package bibliothek.gui.dock.perspective;


/**
 * A helper class that can tell for some {@link PerspectiveElement}s what their unique identifier is,
 * and that can convert the unique identifier to a {@link PerspectiveElement}.
 * @author Benjamin Sigg
 */
public interface PredefinedMap{
	/**
	 * Given <code>element</code>, tells what unique identifier <code>element</code> has.
	 * @param element the element whose identifier is searched
	 * @return the unique identifier or <code>null</code> if <code>element</code> is not known to this map
	 */
	public String get( PerspectiveElement element );
	
	/**
	 * Given a unique identifier <code>id</code>, tells what {@link PerspectiveElement} belongs to that id.
	 * @param id the unique identifier
	 * @return the matching element, can be <code>null</code> if <code>id</code> is unknown
	 */
	public PerspectiveElement get( String id );
}