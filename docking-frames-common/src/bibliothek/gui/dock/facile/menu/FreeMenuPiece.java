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

import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.gui.dock.support.menu.MenuPiece;
import bibliothek.util.ClientOnly;

/**
 * A {@link MenuPiece} that does not add any children by itself. All items
 * have to be added or removed directly by the client.
 * @author Benjamin Sigg
 */
@ClientOnly
public class FreeMenuPiece extends BaseMenuPiece{
    
    @Override
    public int getItemCount() {
        return super.getItemCount();
    }
    
    @Override
    public Component getItem( int index ) {
        return super.getItem( index );
    }
    
    @Override
    public void add( Component item ) {
        super.add( item );
    }
    
    @Override
    public void addSeparator(){
    	super.addSeparator();
    }
    
    @Override
    public void insert( int index, Component item ) {
        super.insert( index, item );
    }
    
    @Override
    public void insertSeparator( int index ){
    	super.insertSeparator( index );
    }
    
    @Override
    public void remove( int index ) {
        super.remove( index );
    }
    
    @Override
    public void remove( Component item ) {
        super.remove( item );
    }
}
