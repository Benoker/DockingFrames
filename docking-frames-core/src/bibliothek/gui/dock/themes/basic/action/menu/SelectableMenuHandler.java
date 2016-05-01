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

package bibliothek.gui.dock.themes.basic.action.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A handler which ensures that the selected-state of a {@link JMenuItem} and
 * a {@link SelectableDockAction} are always the same.
 * @author Benjamin Sigg
 *
 */
public class SelectableMenuHandler extends AbstractMenuHandler<JMenuItem, SelectableDockAction> {
	/** a listener intended to ensure the selection-state on the view and in the action are the same */
	private Listener listener = new Listener();
	
	/**
     * Creates a new handler
     * @param action the action to observe
     * @param dockable the Dockable for which actions are dispatched
     * @param item the item to manage
     */
    public SelectableMenuHandler( final SelectableDockAction action, final Dockable dockable, JMenuItem item ) {
    	super( action, dockable, item );
    	addActionListener( listener );
    }
    
    public void addActionListener( ActionListener listener ){
    	item.addActionListener( listener );
    }
    
    public void removeActionListener( ActionListener listener ){
    	item.removeActionListener( listener );
    }
    
    @Override
    public void bind(){
    	super.bind();
    	item.setSelected( getAction().isSelected( getDockable() ) );
    	action.addSelectableListener( listener );
    }
    
    @Override
    public void unbind(){
    	super.unbind();
    	action.removeSelectableListener( listener );
    }
    
    /**
     * A listener added 
     * @author Benjamin Sigg
     */
    private class Listener implements ActionListener, SelectableDockActionListener{
    	public void actionPerformed( ActionEvent e ){
    		boolean old = action.isSelected( dockable );
    		boolean current = item.isSelected();
    		if( old != current ){
    		    action.trigger( dockable );
    		    item.setSelected( action.isSelected( dockable ) );
    		}
    	}
    	public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
    		if( dockables.contains( dockable )){
    			boolean old = item.isSelected(); 
    			boolean selected = action.isSelected( dockable );
    			if( old != selected )
    				item.setSelected( selected );
    		}
    	}
    }
}
