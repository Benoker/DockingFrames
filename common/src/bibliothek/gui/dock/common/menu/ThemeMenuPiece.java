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
package bibliothek.gui.dock.common.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JRadioButtonMenuItem;

import bibliothek.gui.DockController;
import bibliothek.gui.DockUI;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.gui.dock.themes.ThemeFactory;

/**
 * A {@link MenuPiece} that can change the {@link DockTheme}.
 * @author Benjamin Sigg
 */
public class ThemeMenuPiece extends BaseMenuPiece {
    /** the controller whose theme might be changed */
    private DockController controller;
    
    /** ensures that only one item is selected */
    private ButtonGroup group = new ButtonGroup();
    
    /** the items shown by this piece */
    private List<Item> items = new ArrayList<Item>();
    
    /** The currently selected factory */
    private ThemeFactory selection;
    
    /**
     * Creates a new piece
     * @param controller the controller whose theme might be changed, can be <code>null</code>
     * @param defaultThemes whether the piece should be filled up with the
     * factories that can be obtained through the {@link DockUI}
     */
    public ThemeMenuPiece( DockController controller, boolean defaultThemes ) {
        setController( controller );
        if( defaultThemes )
            addDefaultThemes();
    }

    /**
     * Gets the controller whose theme might be changed by this piece.
     * @return the controller
     */
    public DockController getController() {
        return controller;
    }
    
    /**
     * Sets the controller whose theme might be changed by this piece. The
     * theme of the controller is changed if there is a selection on this
     * piece.
     * @param controller the new controller, can be <code>null</code>
     */
    public void setController( DockController controller ) {
        this.controller = controller;
        if( controller != null ){
            if( selection != null )
                controller.setTheme( selection.create() );
        }
    }
    
    /**
     * Adds the default themes.
     */
    private void addDefaultThemes(){
        for( ThemeFactory factory : DockUI.getDefaultDockUI().getThemes() )
            add( factory );
        
        setSelected( DockUI.getDefaultDockUI().getDefaultTheme() );
    }
    
    /**
     * Adds an item for <code>factory</code>.
     * @param factory the factory to be made available
     */
    public void add( ThemeFactory factory ){
        if( factory == null )
            throw new NullPointerException( "factory must not be null" );
        
        Item item = new Item( factory );
        items.add( item );
        group.add( item );
        add( item );
    }
    
    /**
     * Removes the item of <code>factory</code>. Please note that the {@link DockTheme}
     * created by <code>factory</code> might still be in use.
     * @param factory the factory to remove
     */
    public void remove( ThemeFactory factory ){
        Item item = null;
        for( Item check : items ){
            if( check.factory == factory ){
                item = check;
                break;
            }
        }
        
        items.remove( item );
        group.remove( item );
        remove( item );
        
        if( selection == factory )
            selection = null;
    }
    
    /**
     * Selects the item which represents <code>factory</code> and changes
     * the {@link DockTheme} when necessary.
     * @param factory the factory to select
     */
    public void setSelected( ThemeFactory factory ){
        for( Item item : items ){
            if( item.factory == factory ){
                if( !item.isSelected() ){
                    item.setSelected( true );
                }
                break;
            }
        }
        
        if( this.selection != factory ){
            this.selection = factory;
            if( controller != null )
                controller.setTheme( factory.create() );
        }
    }
    
    /**
     * Gets the factory which is currently selected.
     * @return the factory or <code>null</code> if nothing is selected
     */
    public ThemeFactory getSelected(){
        for( Item item : items ){
            if( item.isSelected() )
                return item.factory;
        }
        
        return null;
    }
    
    /**
     * Gets the number of factories that can be selected by the user.
     * @return the number of factories
     */
    public int getFactoryCount(){
        return items.size();
    }
    
    /**
     * Gets the index'th factory.
     * @param index the index of the factory
     * @return the factory
     */
    public ThemeFactory getFactory( int index ){
        return items.get( index ).factory;
    }
    
    /**
     * Gets the index of <code>factory</code>.
     * @param factory the factory to search
     * @return the index or -1
     */
    public int indexOf( ThemeFactory factory ){
        int index = 0;
        for( Item item : items ){
            if( item.factory == factory )
                return index;
            
            index++;
        }
        return -1;
    }
    
    /**
     * An item that changes the theme when selected.
     * @author Benjamin Sigg
     */
    private class Item extends JRadioButtonMenuItem implements ActionListener{
        /** the factory that creates the new theme */
        private ThemeFactory factory;
        
        /**
         * Creates a new item.
         * @param factory the factory used to create a theme
         */
        public Item( ThemeFactory factory ){
            this.factory = factory;
            setText( factory.getName() );
            setToolTipText( factory.getDescription() );
            addActionListener( this );
        }
        
        public void actionPerformed( ActionEvent e ) {
            ThemeMenuPiece.this.setSelected( factory );
        }
    }
}
