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

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * Describes a part of a {@link JMenu}, can add or remove {@link JMenuItem}s
 * or other {@link JComponent}s at any time to the menu.
 * @author Benjamin Sigg
 */
public abstract class MenuPiece {
    /** the menu before this piece */
    private MenuPiece parent;
    
    /** the listeners of this piece */
    private List<MenuPieceListener> listeners = new ArrayList<MenuPieceListener>();
    
    /** whether this piece may be visible to the user */
    private boolean bound = false;
    
    /**
     * Adds a listener to this piece, the listener will get informed when the 
     * children of this piece change.
     * @param listener the new listener
     */
    public void addListener( MenuPieceListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this piece.
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
     * This method is called if there is a possibility for the user to see this menu. The menu
     * should register listeners, update its text etc. to be ready for that event. This method 
     * must never be called twice in a row.
     * @see #unbind()
     */
    public void bind(){
    	bound = true;
    }
    
    /**
     * This method is called if the user has no longer any possibility to see this menu. The
     * menu should unregister its listeners. This method must never be called twice in a row.
     * @see #bind()
     */
    public void unbind(){
    	bound = false;
    }
    
    /**
     * Whether {@link #bind()} was called but {@link #unbind()} not yet.
     * @return whether this menu piece may be visible to the user
     */
    public boolean isBound(){
		return bound;
	}
    
    /**
     * Gets the number of items this {@link MenuPiece} added to its {@link #getMenu() menu}.
     * @return the number of items
     */
    public abstract int getItemCount();
    
    /**
     * Inserts all items of this piece into <code>items</code>. 
     * @param items the list which has to be filled in the same order as the items
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
