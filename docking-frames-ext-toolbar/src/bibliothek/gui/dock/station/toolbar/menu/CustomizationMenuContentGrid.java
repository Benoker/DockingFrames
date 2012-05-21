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
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import bibliothek.gui.DockController;

/**
 * A {@link CustomizationMenuContent} using a {@link GridLayout} to show a set of other 
 * {@link CustomizationMenuContent}s.
 * @author Benjamin Sigg
 */
public class CustomizationMenuContentGrid implements CustomizationMenuContent{
	/** all the children of this grid */
	private List<CustomizationMenuContent> content = new ArrayList<CustomizationMenuContent>();
	
	/** the currently used view */
	private JPanel view;
	
	/** the width of the grid in the number of components to show */
	private int columns;
	
	/** the height of the grid in the number of components to show */
	private int rows;
	
	/** the controller in whose realm this grid is used */
	private DockController controller;
	
	/**
	 * Creates a new grid.
	 * @param columns the width of the grid in the number of components
	 * @param rows the height of the grid in the number of components
	 */
	public CustomizationMenuContentGrid( int columns, int rows ){
		this.columns = columns;
		this.rows = rows;
	}
	
	@Override
	public Component getView(){
		return view;
	}
	
	@Override
	public void setController( DockController controller ){
		this.controller = controller;
		for( CustomizationMenuContent item : content ){
			item.setController( controller );
		}
	}
	
	@Override
	public void bind( CustomizationMenuCallback callback ){
		view = new JPanel( new GridLayout( columns, rows ));
		for( CustomizationMenuContent item : content ){
			item.bind( callback );
			view.add( item.getView() );
		}
	}
	
	@Override
	public void unbind(){
		view.removeAll();
		view = null;
		
		for( CustomizationMenuContent item : content ){
			item.unbind();
		}
	}
	
	/**
	 * Adds <code>item</code> to this grid. It is the clients responsibility to ensure that <code>item</code>
	 * is not already used by another object. If the menu is currently visible, then calling this method
	 * has no immediate effect.
	 * @param item the item to add, not <code>null</code>
	 */
	public void add( CustomizationMenuContent item ){
		content.add( item );
		item.setController( controller );
	}

	/**
	 * Adds <code>item</code> to this grid. It is the clients responsibility to ensure that <code>item</code>
	 * is not already used by another object. If the menu is currently visible, then calling this method
	 * has no immediate effect.
	 * @param index the location where to insert <code>item</code>
	 * @param item the item to add, not <code>null</code>
	 */
	public void add( int index, CustomizationMenuContent item ){
		content.add( index, item );
		item.setController( controller );
	}
	
	/**
	 * Removes the <code>index</code>'th item from this grid. If the menu is currently visible, then
	 * calling this method has no immediate effect.
	 * @param index the index of the item to remove
	 */
	public void remove( int index ){
		CustomizationMenuContent item = content.remove( index );
		item.setController( null );
	}
	
	/**
	 * Removes <code>item</code> from this grid. If the menu is currently visible, then calling
	 * this method has no immediate effect.
	 * @param item the item to remove
	 */
	public void remove( CustomizationMenuContent item ){
		if( content.remove( item ) ){
			item.setController( null );
		}
	}
	
	/**
	 * Gets the number of items on this grid.
	 * @return the number of items
	 */
	public int getItemCount(){
		return content.size();
	}
	
	/**
	 * Gets the <code>index</code>'th item of this grid.
	 * @param index the index of the item
	 * @return the item, not <code>null</code>
	 */
	public CustomizationMenuContent getItem( int index ){
		return content.get( index );
	}
}
