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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 * A {@link MenuPiece} that does not do anything from its own. All items
 * can be added or removed directly by the client.
 * @author Benjamin Sigg
 *
 */
public class FreeMenuPiece extends MenuPiece{
    /**
     * Creates a new {@link MenuPiece} that adds its items directly at
     * the top of <code>menu</code>.
     * @param menu the menu into which this piece will insert its items
     */
    public FreeMenuPiece( JMenu menu ) {
        super( menu );
    }

    /**
     * Creates a new {@link MenuPiece} that will add its items directly below
     * <code>predecessor</code>.
     * @param predecessor the piece directly before this piece
     */
    public FreeMenuPiece( MenuPiece predecessor ) {
        super( predecessor );
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
    
    @Override
    public JMenuItem getItem( int index ) {
        return super.getItem( index );
    }
    
    @Override
    public void add( JMenuItem item ) {
        super.add( item );
    }
    
    @Override
    public void insert( int index, JMenuItem item ) {
        super.insert( index, item );
    }
    
    @Override
    public void remove( int index ) {
        super.remove( index );
    }
    
    @Override
    public void remove( JMenuItem item ) {
        super.remove( item );
    }
}
