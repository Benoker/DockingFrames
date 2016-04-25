/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

/**
 * A handler to change and read the visibility state and the z order of some item.
 * This handler is attached to some other component, the visibility state and the
 * z order depend not only on the item put also on internal states of the other items.
 * @author Benjamin Sigg
 *
 * @param <T> the kind of item whose visibility is changed
 */
public interface CombinedHandler<T> {
	/**
	 * Sets the visibility of <code>item</code>.
	 * @param item some item
	 * @param visible its new visibility state
	 * @throws IllegalArgumentException if <code>item</code> does not belong
	 * to the component this handler is attached to
	 */
	public void setVisible( T item, boolean visible );
	
	/**
	 * Tells whether <code>item</code> is visible or not.
	 * @param item some item
	 * @return <code>true</code> if visible, <code>false</code> otherwise
	 * @throws IllegalArgumentException if <code>item</code> does not belong
	 * to the component this handler is attached to
	 */
	public boolean isVisible( T item );
	
	/**
	 * Sets the z order of <code>item</code>, items with lower z order
	 * are painted first.
	 * @param item some item
	 * @param order its z order
	 */
	public void setZOrder( T item, int order );
	
	/**
	 * Gets the z order of <code>item</code>.
	 * @param item the item
	 * @return the z order
	 * @throws IllegalArgumentException if <code>item</code> does not
	 * belong to the component this handler is attached to
	 */
	public int getZOrder( T item );
}
