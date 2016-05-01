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
package bibliothek.gui.dock.themes.basic.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;

/**
 * Represents an action and a view which are children of a {@link DropDownAction}
 * @author Benjamin Sigg
 */
public class DropDownItemHandle implements ActionListener{
	/** the action */
	private DockAction item;
	/** the view of {@link #item} */
	private DropDownViewItem view;
	/** the dockable for which this item is used */
    private Dockable dockable;
    /** the menu which is the owner of the action */
    private DropDownAction action;
    
	/**
	 * Creates a new item.
	 * @param item the action
	 * @param view the view of <code>item</code>
     * @param dockable the dockable for which the item is used
     * @param action the owner of the item
	 */
	public DropDownItemHandle( DockAction item, DropDownViewItem view, Dockable dockable, DropDownAction action ){
		this.item = item;
		this.view = view;
        this.dockable = dockable;
        this.action = action;
	}
	
    /**
     * Gets the view of the action.
     * @return the view
     */
    public DropDownViewItem getView() {
        return view;
    }
    
	/**
	 * Connects the view.
	 */
	public void bind(){
		view.bind();
		view.addActionListener( this );
	}
	
	/**
	 * Disconnects the view
	 */
	public void unbind(){
		view.removeActionListener( this );
		view.unbind();
	}
	
	public void actionPerformed( ActionEvent e ){
		if( view.isSelectable() )
			action.setSelection( dockable, item );
	}
}