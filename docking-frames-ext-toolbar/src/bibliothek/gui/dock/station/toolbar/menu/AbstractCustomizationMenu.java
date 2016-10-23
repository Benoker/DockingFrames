/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.station.toolbar.menu;

import java.awt.Component;

import bibliothek.gui.DockController;

/**
 * The abstract implementation of {@link CustomizationMenu} offers fields to store standard data required
 * by all menus.
 * @author Benjamin Sigg
 */
public abstract class AbstractCustomizationMenu implements CustomizationMenu{
	private int x;
	private int y;
	private CustomizationMenuContent content;
	private CustomizationMenuCallback callback;
	private DockController controller;
	
	@Override
	public CustomizationMenuContent getContent(){
		return content;
	}

	@Override
	public void setContent( CustomizationMenuContent content ){
		if( this.content != content ){
			if( isOpen() ){
				CustomizationMenuCallback callback = getCallback();
				close();
				if( this.content != null ){
					this.content.setController( null );
				}
				this.content = content;
				if( content != null ){
					content.setController( controller );
					open( x, y, callback );
				}
			}
			else{
				if( this.content != null ){
					this.content.setController( null );
				}
				this.content = content;
				if( content != null ){
					content.setController( controller );
				}
			}
		}
	}
	
	@Override
	public void setController( DockController controller ){
		this.controller = controller;
		if( content != null ){
			content.setController( controller );
		}
	}
	
	/**
	 * Gets the controller in whose realm this menu is used.
	 * @return the controller, can be <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	/**
	 * Tells whether this menu currently is visible.
	 * @return whether the menu is open
	 */
	public boolean isOpen(){
		return callback != null;
	}
	
	/**
	 * Gets the callback for retrieving more information about the station that opened this menu.
	 * @return the callback, will be <code>null</code> if this menu is {@link #isOpen() not open}.
	 */
	public CustomizationMenuCallback getCallback(){
		return callback;
	}

	@Override
	public void open( int x, int y, CustomizationMenuCallback callback ){
		if( callback == null ){
			throw new IllegalArgumentException();
		}
		if( content == null ){
			throw new IllegalStateException( "the menu has no content and cannot be opened" );
		}
		
		if( this.callback != null ){
			close();
		}
		
		this.callback = callback;
		content.bind( callback );
		
		this.x = x;
		this.y = y;
		doOpen( x, y, content.getView() );
	}

	@Override
	public void close(){
		doClose();
		closed();
	}
	
	protected void closed(){
		if( callback != null ){
			callback.closed();
			callback = null;
			if( content != null ){
				content.unbind();
			}
		}
	}
	
	/**
	 * Shows this menu. This method is only called if the menu is not yet open. Note that the 
	 * {@link Component} <code>content</code> will never be replaced while the menu is open.
	 * @param x the preferred x coordinate of the menu
	 * @param y the preferred y coordinate of the menu
	 * @param content the content of the menu, not <code>null</code>
	 */
	protected abstract void doOpen( int x, int y, Component content );
	
	/**
	 * Closes this menu. There are no calls to the {@link CustomizationMenuCallback} required. Subclasses should
	 * not call this method directly, instead they should always call {@link #close()}
	 */
	protected abstract void doClose();
}
