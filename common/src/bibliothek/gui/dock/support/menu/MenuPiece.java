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
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

/**
 * Describes a part of a {@link JMenu}, can add or remove {@link JMenuItem}s
 * at any time to the menu.
 * @author Benjamin Sigg
 */
public abstract class MenuPiece {
	
    /** the menu before this piece */
    private MenuPiece parent;
    
    /**
     * Gets the menu into which this {@link MenuPiece} adds its items.
     * @return the menu
     */
    public JMenu getMenu() {
        if( parent == null )
        	return null;
        else
        	return parent.getMenu();
    }

    /**
     * Gets the parent of this piece.
     * @return the parent, can be <code>null</code> for the root or for a piece
     * which is not yet integrated.
     */
    public MenuPiece getParent(){
		return parent;
	}
    
    /**
     * Sets the parent of this piece.
     * @param parent the parent, might be <code>null</code>
     */
    protected void setParent( MenuPiece parent ){
		this.parent = parent;
	}
    
    /**
     * Gets the number of items this {@link MenuPiece} added to its {@link #getMenu() menu}
     * @return the number of items
     */
    protected abstract int getItemCount();
    
    /**
     * Inserts all items of this piece into <code>items</code>. 
     * @return the list which has to be filled in the same order as the items
     * will appear on the menu
     */
    protected abstract void fill( List<Component> items );
    
    /**
     * Gets all items that are shown by this piece.
     * @return all items
     */
    protected Component[] items(){
    	List<Component> items = new LinkedList<Component>();
    	fill( items );
    	return items.toArray( new Component[ items.size() ] );
    }
    
    /**
     * Adds an item to the menu.
     * @param child a {@link MenuPiece} whose parent is <code>this</code> and which
     * want's to add the item
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param items the new items
     */
    protected abstract void insert( MenuPiece child, int index, Component... items );
    
    /**
     * Removes an item from the menu.
     * @param child a {@link MenuPiece} whose parent is <code>this</code> and which
     * want's to remove the item
     * @param index the index of the item, measured relatively to all items of <code>owner</code>
     * @param length the number of items to remove
     */
    protected abstract void remove( MenuPiece child, int index, int length );
}
