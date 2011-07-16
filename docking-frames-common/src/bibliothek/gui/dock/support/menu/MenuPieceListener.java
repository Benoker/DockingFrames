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
package bibliothek.gui.dock.support.menu;

import java.awt.Component;

/**
 * This listener is added to a {@link MenuPiece} and gets informed when the content of the
 * piece changes.
 * @author Benjamin Sigg
 */
public interface MenuPieceListener {
	/**
     * Called if some items have been added to <code>piece</code>.
     * @param piece a {@link MenuPiece} whose content changed
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param items the new items
     */
    public abstract void insert( MenuPiece piece, int index, Component... items );
    
    /**
     * Called if some items have been removed from <code>piece</code>
     * @param piece the {@link MenuPiece} whose content changed
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param length the number of items to remove
     */
    public abstract void remove( MenuPiece piece, int index, int length );
}
