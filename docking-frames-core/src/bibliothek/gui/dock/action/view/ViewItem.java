/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.action.view;

import bibliothek.gui.dock.action.DockAction;

/**
 * A wrapper for an item that will be shown somewhere.
 * @author Benjamin Sigg
 *
 * @param <A> the type of item that is wrapped
 */
public interface ViewItem<A> {
	/**
	 * Binds this item to its action
	 */
	public void bind();
	
	/**
	 * Unbinds this item from its action
	 */
	public void unbind();
	
	/**
	 * Gets this item as component.
	 * @return this item, depending on the subclass this may or may not be <code>null</code>
	 */
	public A getItem();
	
	/**
	 * Gets the action that is represented by this target.
	 * @return the action, might be <code>null</code>
	 */
	public DockAction getAction();
}
