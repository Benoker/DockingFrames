/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.common;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Describes one part of a {@link CDockable} that can be disabled.
 * @author Benjamin Sigg
 */
public enum EnableableItem {
	/** Allows to disabled any {@link DockTitle} */
	TITLES(1),
	/** Allows to disabled any {@link CAction} or {@link DockAction} */
	ACTIONS(2),
	/** Allows to disabled any kind of tab */
	TABS(4),
	/** Allows to disabled the {@link CDockable} or {@link CStation} itself */
	SELF(8),
	/** A combination of all {@link EnableableItem} that exist */
	ALL(15);
	
	/** a bitwise flag, can be used to easily store several {@link EnableableItem} */
	private int flag;
	
	private EnableableItem( int flag ){
		this.flag = flag;
	}
	
	/**
	 * Gets the flag of this item, the flag is used to store a combination of several items.
	 * @return the flag
	 */
	public int getFlag(){
		return flag;
	}
	
	/**
	 * Tells whether the <code>flags</code>, which was created by {@link #add(int, EnableableItem)} and
	 * {@link #remove(int, EnableableItem)}, contains <code>item</code>.
	 * @param flags the flags that are enabled
	 * @param item the item to test
	 * @return whether <code>item</code> is enabled
	 */
	public static boolean isEnabled( int flags, EnableableItem item ){
		return (flags & item.flag) == item.flag;
	}
	
	/**
	 * Adds <code>item</code> to the <code>flags</code> and returns a new flag.
	 * @param flags the enabled items
	 * @param item the item that should be enabled too
	 * @return the new flag including <code>flags</code> and <code>item</code>
	 */
	public static int add( int flags, EnableableItem item ){
		return flags | item.flag;
	}
	
	/**
	 * Removes <code>item</code> from <code>flags</code> and returns a new flag.
	 * @param flags the enabled items
	 * @param item the item that should no longer be enabled
	 * @return the new flag including <code>flags</code> except <code>item</code>
	 */
	public static int remove( int flags, EnableableItem item ){
		return flags & ~item.flag;
	}
}
