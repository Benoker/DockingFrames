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
package bibliothek.gui.dock.util.extension;

import java.util.List;

/**
 * A {@link SharedExtension} is a set of extensions shared by many clients. Basically it is used for
 * optimization, as it allows to reuse objects. Clients should call {@link #bind()} before, and
 * {@link #unbind()} after using a {@link SharedExtension} object.
 * @author Benjamin Sigg
 * @param <T> the kind of object that is shared
 */
public interface SharedExtension<T> extends Iterable<T>{
	/**
	 * Needs to be called by clients to make sure that {@link #get()} actually returns something.
	 */
	public void bind();
	
	/**
	 * Can be called by clients to release no longer needed resources.
	 */
	public void unbind();
	
	/**
	 * Gets the name of the extension whose objects are shared.
	 * @return the name, never <code>null</code>
	 */
	public ExtensionName<T> getName();
	
	/**
	 * Gets the list of shared objects.
	 * @return the list of objects, may be empty but never <code>null</code>, is not modifiable
	 * @throws IllegalStateException if this {@link SharedExtension} is not {@link #bind() bound}
	 */
	public List<T> get();
}
