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

import java.util.HashSet;
import java.util.Set;

/**
 * A group of {@link CRadioButton}s, only one button of the group is selected.
 * @author Benjamin Sigg
 */
public class CRadioGroup {
    /** the buttons in this group */
    private Set<CRadioButton> buttons = new HashSet<CRadioButton>();
    
    /**
     * Adds a new button to the group
     * @param button the new button
     */
    public void add( CRadioButton button ){
        if( button == null )
            throw new NullPointerException( "button must not be null" );
        buttons.add( button );
        button.setGroup( this );
        selected( button );
    }
    
    /**
     * Removes a button from this group
     * @param button the button to remove
     */
    public void remove( CRadioButton button ){
        if( buttons.remove( button )){
            button.setGroup( null );
        }
    }
    
    /**
     * Invoked by a {@link CRadioButton} which got selected.
     * @param button the newly selected button
     */
    void selected( CRadioButton button ){
        if( button.isSelected() ){
            for( CRadioButton b : buttons ){
                if( b != button && b.isSelected() )
                    b.setSelected( false );
            }
        }
    }
}
