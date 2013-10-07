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
import java.awt.EventQueue;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JMenu;

import bibliothek.gui.dock.support.menu.MenuPiece;
import bibliothek.gui.dock.support.menu.MenuPieceListener;

/**
 * The root of a tree of {@link MenuPiece}s. This {@link MenuPiece} is a direct representation
 * of a {@link JMenu}
 * @author Benjamin Sigg
 */
public class RootMenuPiece extends NodeMenuPiece {
	/** the menu into which this piece will insert its content */
	private JMenu menu;
	
	/** disable {@link #menu} when there are no items */
	private boolean disableWhenEmpty;
	
	/** whether the {@link #menu} should be enabled */
	private boolean enabled = true;
	
	
	/**
	 * Creates a new root-piece, using a normal {@link JMenu} to inserts
	 * its content.
	 */
	public RootMenuPiece(){
		this( new JMenu() );
	}
	
	/**
	 * Creates a new root-piece, using a normal {@link JMenu}.
	 * @param text the text of the menu
	 * @param disableWhenEmpty whether to disable the menu when it is empty
	 * @param pieces the elements of this piece
	 */
	public RootMenuPiece( String text, boolean disableWhenEmpty, MenuPiece... pieces ){
	    this( new JMenu( text ));
	    setDisableWhenEmpty( disableWhenEmpty );
	    for( MenuPiece piece : pieces )
	        add( piece );
	}
	
	/**
	 * Creates a new root-piece.
	 * @param menu the menu into which this piece will insert its content
	 */
	public RootMenuPiece( JMenu menu ){
		if( menu == null )
			throw new NullPointerException( "menu must not be null" );
		this.menu = menu;

        menu.addHierarchyListener( new HierarchyListener(){
			public void hierarchyChanged( HierarchyEvent e ){
				if( (e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0 ){
					EventQueue.invokeLater( new Runnable(){
						public void run(){
							checkVisibility();	
						}
					} );
				}
			}
		});
        
		checkVisibility();
		
		addListener( new MenuPieceListener(){
			public void insert( MenuPiece child, int index, Component... items ){
				JMenu menu = getMenu();
				for( int i = 0; i < items.length; i++ )
					menu.add( items[i], i+index );
			
				updateEnabled();
			}
			public void remove( MenuPiece child, int index, int length ){
				JMenu menu = getMenu();
				for( int i = index+length-1; i >= index; i-- )
					menu.remove( i );

				updateEnabled();
			}
		});
	}
	
	/**
	 * Calls {@link JMenu#setEnabled(boolean)} with a parameter that is calculated using
	 * {@link #isDisableWhenEmpty()}, {@link #getItemCount()} and 
	 */
	protected void updateEnabled(){
		if( isEnabled() ){
			if( isDisableWhenEmpty() ){
				menu.setEnabled( getItemCount() > 0 );
			}
			else{
				menu.setEnabled( true );
			}
		}
		else{
			menu.setEnabled( false );
		}
	}
	
	private void checkVisibility(){
		boolean showing;
		
		if( getParent() == null ){
			showing = menu.isShowing();
		}
		else{
			showing = getParent().isBound();
		}
		
		if( showing && !isBound() ){
			bind();
		}
		else if( !showing && isBound() ){
			unbind();
		}
	}
	
	/**
	 * Disables the menu if there are no items in the menu.
	 * @param disableWhenEmpty <code>true</code> if the menu should be
	 * disabled when empty
	 */
	public void setDisableWhenEmpty( boolean disableWhenEmpty ) {
		this.disableWhenEmpty = disableWhenEmpty;
        updateEnabled();
    }
	
	/**
	 * Whether to disable the menu when it is empty or not.
	 * @return <code>true</code> if the menu gets disabled
	 * @see #setEnabled(boolean)
	 */
	public boolean isDisableWhenEmpty() {
        return disableWhenEmpty;
    }
	
	/**
	 * Enables or disables the menu. If the argument is <code>false</code>, then the
	 * menu is disabled in any case. Otherwise the menu may be enabled or disabled
	 * depending on the value of {@link #isDisableWhenEmpty()}.
	 * @param enabled wether the menu should be enabled
	 * @see #setEnabled(boolean)
	 */
	public void setEnabled( boolean enabled ) {
		this.enabled = enabled;
		updateEnabled();
	}
	
	/**
	 * Tells whether the menu can be enabled or not.
	 * @return <code>true</code> if the menu can be enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	@Override
	public JMenu getMenu(){
		return menu;
	}
	
	@Override
	public void setParent( MenuPiece parent ){
		super.setParent( parent );
		checkVisibility();
	}
}
