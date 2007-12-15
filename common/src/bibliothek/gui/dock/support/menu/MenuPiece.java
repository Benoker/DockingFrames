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
    
    /** the listeners of this piece */
    private List<MenuPieceListener> listeners = new ArrayList<MenuPieceListener>();
    
    /**
     * Adds a listener to this piece, the listener will get informed when this
     * piece want's to add or remove items.
     * @param listener the new listener
     */
    public void addListener( MenuPieceListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes a listener to this piece.
     * @param listener the listener to remove
     */
    public void removeListener( MenuPieceListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Informs all listeners that new items were inserted.
     * @param index the index of the first new item
     * @param items the new items
     */
    protected void fireInsert( int index, Component... items ){
    	for( MenuPieceListener listener : listeners.toArray( new MenuPieceListener[ listeners.size() ] ) )
    		listener.insert( this, index, items );
    }
    
    /**
     * Informs all listeners that items were deleted.
     * @param index the index of the first deleted item
     * @param length the number of deleted items
     */
    protected void fireRemove( int index, int length ){
    	for( MenuPieceListener listener : listeners.toArray( new MenuPieceListener[ listeners.size() ] ) )
    		listener.remove( this, index, length );
    }
    
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
     * Sets the parent of this piece. Note that clients normally do not need
     * to invoke this method.
     * @param parent the parent, might be <code>null</code>
     */
    public void setParent( MenuPiece parent ){
		this.parent = parent;
	}
    
    /**
     * Gets the number of items this {@link MenuPiece} added to its {@link #getMenu() menu}
     * @return the number of items
     */
    public abstract int getItemCount();
    
    /**
     * Inserts all items of this piece into <code>items</code>. 
     * @return the list which has to be filled in the same order as the items
     * will appear on the menu
     */
    public abstract void fill( List<Component> items );
    
    /**
     * Gets all items that are shown by this piece.
     * @return all items
     */
    public Component[] items(){
    	List<Component> items = new LinkedList<Component>();
    	fill( items );
    	return items.toArray( new Component[ items.size() ] );
    }
}
