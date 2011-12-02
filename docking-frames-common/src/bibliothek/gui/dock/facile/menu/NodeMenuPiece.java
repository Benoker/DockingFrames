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
package bibliothek.gui.dock.facile.menu;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.support.menu.MenuPiece;
import bibliothek.gui.dock.support.menu.MenuPieceListener;

/**
 * A piece which uses a set of other pieces to create a composite.
 * @author Benjamin Sigg
 *
 */
public class NodeMenuPiece extends MenuPiece{
	/** the children of this piece */
	private List<MenuPiece> children = new ArrayList<MenuPiece>();
	
	/** listener for adding or removing items */
	private Listener listener = new Listener();
	
	/**
	 * Adds a new child to the end of this piece.
	 * @param piece the new child
	 */
	public void add( MenuPiece piece ){
		insert( children.size(), piece );
	}
	
	/**
	 * Inserts a new child in this piece.
	 * @param index the location of the child
	 * @param piece the new child
	 */
	public void insert( int index, MenuPiece piece ){
		if( piece.getParent() != null )
			throw new IllegalArgumentException( "piece already has a parent" );
		
		piece.setParent( this );
		children.add( index, piece );
		
		piece.addListener( listener );
		listener.insert( piece, 0, piece.items() );
		
		if( isBound() ){
			piece.bind();
		}
	}
	
	/**
	 * Removes a child from this piece.
	 * @param piece the child to remove
	 */
	public void remove( MenuPiece piece ){
		if( children.contains( piece )){
			listener.remove( piece, 0, piece.getItemCount() );
			piece.removeListener( listener );
			
			piece.setParent( null );
			children.remove( piece );
			
			if( isBound() ){
				piece.unbind();
			}
		}
	}
	
	/**
	 * Removes a child from this piece.
	 * @param index the index of the child
	 */
	public void remove( int index ){
		MenuPiece piece = children.get( index );
		listener.remove( piece, 0, piece.getItemCount() );
		piece.removeListener( listener );
		
		piece.setParent( null );
		children.remove( index );
		
		if( isBound() ){
			piece.unbind();
		}
	}
	
	/**
	 * Gets the number of children this piece has.
	 * @return the number of children
	 */
	public int getChildrenCount(){
		return children.size();
	}
	
	/**
	 * Gets the index'th child of this piece.
	 * @param index the location of the child
	 * @return the child
	 */
	public MenuPiece getChild( int index ){
		return children.get( index );
	}
	
	@Override
	public void fill( List<Component> items ){
		for( MenuPiece piece : children )
			piece.fill( items );
	}
	
	@Override
	public int getItemCount(){
		int count = 0;
		for( MenuPiece piece : children )
			count += piece.getItemCount();
		return count;
	}
	
	@Override
	public void bind(){
		if( !isBound() ){
			super.bind();
			for( MenuPiece child : children ){
				child.bind();
			}
		}
	}
	
	@Override
	public void unbind(){
		if( isBound() ){
			super.unbind();
			for( MenuPiece child : children ){
				child.unbind();
			}
		}
	}
	
	/**
	 * A listener to all children, forwarding any call of inserting or removing
	 * items.
	 * @author Benjamin Sigg
	 */
	private class Listener implements MenuPieceListener{
		public void insert( MenuPiece child, int index, Component... component ){
			for( MenuPiece piece : children ){
				if( piece == child )
					break;
				else
					index += piece.getItemCount();
			}
			fireInsert( index, component );
		}

		public void remove( MenuPiece child, int index, int length ){
			for( MenuPiece piece : children ){
				if( piece == child )
					break;
				else
					index += piece.getItemCount();
			}
			fireRemove( index, length );
		}		
	}
}
