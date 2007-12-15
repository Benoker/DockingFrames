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
import java.util.List;

import javax.swing.JMenu;

/**
 * The root of a tree of {@link MenuPiece}s.
 * @author Benjamin Sigg
 */
public class RootMenuPiece extends NodeMenuPiece {
	/** the menu into which this piece will insert its content */
	private JMenu menu;
	
	/**
	 * Creates a new root-piece, using a normal {@link JMenu} to inserts
	 * its content.
	 */
	public RootMenuPiece(){
		this( new JMenu() );
	}
	
	/**
	 * Creates a new root-piece.
	 * @param menu the menu into which this piece will insert its content
	 */
	public RootMenuPiece( JMenu menu ){
		if( menu == null )
			throw new NullPointerException( "menu must not be null" );
		this.menu = menu;
		super.setParent( new Root() );
	}
	
	@Override
	public JMenu getMenu(){
		return menu;
	}
	
	@Override
	protected final void setParent( MenuPiece parent ){
		throw new IllegalStateException( "A root can't have any parent" );
	}
	
	/**
	 * The real root, just adds or removes items to or from the
	 * {@link RootMenuPiece#menu menu}.
	 * @author Benjamin Sigg
	 */
	private class Root extends MenuPiece{
		@Override
		protected void fill( List<Component> items ){
			RootMenuPiece.this.fill( items );
		}

		@Override
		protected int getItemCount(){
			return RootMenuPiece.this.getItemCount();
		}

		@Override
		protected void insert( MenuPiece child, int index, Component... items ){
			for( int i = 0; i < items.length; i++ )
				menu.add( items[i], i+index );
		}

		@Override
		protected void remove( MenuPiece child, int index, int length ){
			for( int i = index+length-1; i >= index; i-- )
				menu.remove( i );
		}
	}
}
