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

import javax.swing.JMenuItem;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A handler building a connection between a {@link JMenuItem} and a {@link ButtonDockAction}.
 * @author Benjamin Sigg
 *
 */
public class ButtonMenuHandler extends AbstractMenuHandler<JMenuItem, ButtonDockAction> {
	
	/**
     * Creates a new handler.
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     */
    public ButtonMenuHandler( ButtonDockAction action, Dockable dockable ){
    	super( action, dockable, new JMenuItem() );
    	
        item.addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                ButtonMenuHandler.this.action.action( ButtonMenuHandler.this.dockable );
            }
        });
    }
    
    public void addActionListener( ActionListener listener ){
    	item.addActionListener( listener );
    }
    
    public void removeActionListener( ActionListener listener ){
    	item.removeActionListener( listener );
    }
}
