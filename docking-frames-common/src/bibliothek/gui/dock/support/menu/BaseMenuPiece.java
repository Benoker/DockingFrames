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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPopupMenu;

/**
 * A very simple piece, subclasses can add or remove items from this piece
 * at any time.
 * @author Benjamin Sigg
 *
 */
public class BaseMenuPiece extends MenuPiece{
    /** the list of items shown by this piece */
    private List<Component> items = new ArrayList<Component>();
    
    @Override
    public void fill( List<Component> items ){
    	items.addAll( this.items );
    }
    @Override
    public int getItemCount(){
    	return items.size();
    }
    
    /**
     * Gets the index'th item of this piece.
     * @param index the location of the item
     * @return the item
     */
    protected Component getItem( int index ){
		return items.get( index );
	}
    
    /**
     * Adds a separator at the end of this piece.
     */
    protected void addSeparator(){
    	insertSeparator( getItemCount() );
    }
    
    /**
     * Inserts a separator into this piece.
     * @param index the location of the separator
     */
    protected void insertSeparator( int index ){
    	insert( index, new JPopupMenu.Separator() );
    }
    
    /**
     * Inserts a new item into the menu.
     * @param index the location of the new item, measured in the coordinate
     * space of this piece.
     * @param item the new item.
     */
    protected void insert( int index, Component item ){
        items.add( index, item );
        fireInsert( index, item );
    }
    

    /**
     * Adds <code>item</code> at the end of this piece.
     * @param item the item to add.
     */
    protected void add( Component item ){
        insert( getItemCount(), item );
    }
    
    /**
     * Removes <code>item</code> from the menu.
     * @param item the item to remove
     */
    protected void remove( Component item ){
        int index = items.indexOf( item );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the index'th item of this piece.
     * @param index the location of the item to remove.
     */
    protected void remove( int index ){
    	items.remove( index );
        fireRemove( index, 1 );
    }
    
    /**
     * Removes all items of this piece.
     */
    protected void removeAll(){
    	int count = items.size();
    	items.clear();
    	fireRemove( 0, count );
    }
}
