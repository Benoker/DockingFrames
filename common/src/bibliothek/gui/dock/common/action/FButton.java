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
package bibliothek.gui.dock.common.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;

import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.common.intern.action.FDropDownItem;

/**
 * A simple button, the user clicks onto the button and {@link #action()} is called.
 * @author Benjamin Sigg
 */
public abstract class FButton extends FDropDownItem {
    /**
     * Creates the new button
     */
    public FButton(){
        super( new SimpleButtonAction() );
        ((SimpleButtonAction)intern()).addActionListener( new ActionListener(){
            public void actionPerformed( ActionEvent e ) {
                action();
            }
        });
    }
    
    /**
     * Creates a new button.
     * @param text the text of this button
     * @param icon the icon of this button
     */
    public FButton( String text, Icon icon ){
        this();
        setText( text );
        setIcon( icon );
    }
    
    /**
     * Invoked when the user clicks onto this button.
     */
    protected abstract void action();
}
