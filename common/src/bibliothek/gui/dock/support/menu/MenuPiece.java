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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 * Describes a part of a {@link JMenu}, can add or remove {@link JMenuItem}s
 * at any time to the menu.<br>
 * A <code>MenuPiece</code> adds its content at the very beginning of the menu,
 * but multiple pieces can be connected to create a chain of pieces. This
 * connection has to be made through the constructor.<br>
 * An example how to use <code>MenuPiece</code>:
 * <pre><code>JMenu menu = new JMenu( "File" );
 * FreeMenuPiece begin = new FreeMenuPiece( menu );
 * begin.add( new JMenuItem( "Open file" ));
 * MenuPiece recentlyOpened = new RecentlyOpenedFilesMenuPiece( begin );
 * FreeMenuPiece end = new FreeMenuPiece( recentlyOpened );
 * end.add( new JMenuItem( "Exit" ));
 * </code></pre>
 * @author Benjamin Sigg
 */
public abstract class MenuPiece {
    /** whether to make a separator above this menupiece or not */
    private boolean topSeparator = false;
    /** whether to make a separator below this menupiece or not */
    private boolean bottomSeparator = false;
    /** whether to show a separator when there are no items in this menupiece */
    private boolean emptySeparator = false;
    
    /** the menu managed by this menupiece */
    private JMenu menu;
    
    /** the menu before this piece */
    private MenuPiece predecessor;
    
    /** the list of items shown by this piece */
    private List<JMenuItem> items = new ArrayList<JMenuItem>();
    
    /**
     * Creates a new piece of the menu.
     * @param menu the menu to manage
     */
    public MenuPiece( JMenu menu ){
        if( menu == null )
            throw new NullPointerException( "menu must not be null" );
        
        this.menu = menu;
    }
    
    /**
     * Creates a new piece that will add its content below
     * <code>predecessor</code>.
     * @param predecessor the piece before this piece
     */
    public MenuPiece( MenuPiece predecessor ){
        menu = predecessor.getMenu();
        this.predecessor = predecessor;
        
        setTopSeparator( true );
        setEmptySeparator( true );
    }
    
    /**
     * Gets the menu into which this {@link MenuPiece} adds its items.
     * @return the menu
     */
    public JMenu getMenu() {
        return menu;
    }
    
    /**
     * Gets the {@link MenuPiece} that is directly above this <code>MenuPiece</code>.
     * @return the piece directly above
     */
    public MenuPiece getPredecessor() {
        return predecessor;
    }

    /**
     * Gets the number of items this {@link MenuPiece} added to its {@link #getMenu() menu}
     * @return the number of items, not counting any divider
     */
    protected int getItemCount(){
        return items.size();
    }
    
    /**
     * Gets the index'th item of this piece.
     * @param index the location of the item
     * @return an item
     */
    protected JMenuItem getItem( int index ){
        return items.get( index );
    }
    
    /**
     * Gets the index of the first item in the menu that comes after this
     * {@link MenuPiece}. 
     * @return the index of the next item below this piece
     */
    protected int next(){
        int count = getItemCount();
        if( count == 0 ){
            if( emptySeparator )
                count++;
        }
        else{
            if( topSeparator )
                count++;
            if( bottomSeparator )
                count++;
        }
        
        if( predecessor != null ){
            count += predecessor.next();
        }
        
        return count;
    }
    
    /**
     * Inserts a new item into the menu.
     * @param index the location of the new item, measured in the coordinate
     * space of this piece.
     * @param item the new item.
     */
    protected void insert( int index, JMenuItem item ){
        int first = 0;
        if( predecessor != null )
            first = predecessor.next();
        
        if( items.isEmpty() ){
            int should = (topSeparator ? 1 : 0) + (bottomSeparator ? 1 : 0);
            int now = emptySeparator ? 1 : 0;
            
            while( now > should ){
                menu.remove( first );
                now--;
            }
            while( now < should ){
                menu.add( new JPopupMenu.Separator(), first );
                now++;
            }
        }
        
        if( topSeparator )
            first++;
        
        menu.add( item, first+index );
        items.add( index, item );
    }
    

    /**
     * Adds <code>item</code> at the end of this piece.
     * @param item the item to add.
     */
    protected void add( JMenuItem item ){
        insert( getItemCount(), item );
    }
    
    /**
     * Removes <code>item</code> from the menu.
     * @param item the item to remove
     */
    protected void remove( JMenuItem item ){
        int index = items.indexOf( item );
        if( index >= 0 )
            remove( index );
    }
    
    /**
     * Removes the item at location <code>index</code>.
     * @param index the index of the item to remove
     */
    protected void remove( int index ){
        JMenuItem item = items.remove( index );
        menu.remove( item );
        if( items.isEmpty() ){
            int now = (topSeparator ? 1 : 0) + (bottomSeparator ? 1 : 0);
            int should = emptySeparator ? 1 : 0;
            
            int first = 0;
            if( predecessor != null )
                first = predecessor.next();
            
            while( now > should ){
                menu.remove( first );
                now--;
            }
            while( now < should ){
                menu.add( new JPopupMenu.Separator(), first );
                now++;
            }
        }
    }
    
    /**
     * Tells whether there is a separator below this piece.
     * @return <code>true</code> if there is a separator
     * @see #setBottomSeparator(boolean)
     */
    public boolean isBottomSeparator() {
        return bottomSeparator;
    }
    
    /**
     * Sets whether there should be a separator added to the menu after
     * the contents described in this piece. Note that there might not be
     * any separator if this piece is empty.
     * @param bottomSeparator <code>true</code> if there should be a separator
     */
    public void setBottomSeparator( boolean bottomSeparator ) {
        if( this.bottomSeparator != bottomSeparator ){
            this.bottomSeparator = bottomSeparator;
            if( !items.isEmpty() ){
                if( bottomSeparator ){
                    menu.add( new JPopupMenu.Separator(), next()-1 );
                }
                else{
                    menu.remove( next() );
                }
            }
        }
    }
    
    /**
     * Tells whether there should be a single separator shown when this
     * piece is empty.
     * @return <code>true</code> if there is a separator
     */
    public boolean isEmptySeparator() {
        return emptySeparator;
    }
    
    /**
     * Sets whether there should be a separator shown when this piece
     * is empty.
     * @param emptySeparator <code>true</code> if a separator should be
     * made visible
     */
    public void setEmptySeparator( boolean emptySeparator ) {
        if( this.emptySeparator != emptySeparator ){
            this.emptySeparator = emptySeparator;
            if( items.isEmpty() ){
                if( emptySeparator ){
                    menu.add( new JPopupMenu.Separator(), next()-1 );
                }
                else{
                    menu.remove( next() );
                }
            }
        }
    }
    
    /**
     * Tells whether there is a separator shown above the content of this
     * piece.
     * @return <code>true</code> if there is a separator
     */
    public boolean isTopSeparator() {
        return topSeparator;
    }
    
    /**
     * Sets whether there should be a separator shown above the content of
     * this piece. Note that there might not be any separator if this piece
     * is empty.
     * @param topSeparator <code>true</code> if the separator should be shown
     */
    public void setTopSeparator( boolean topSeparator ) {
        if( this.topSeparator != topSeparator ){
            this.topSeparator = topSeparator;
            if( !items.isEmpty() ){
                int first = 0;
                if( predecessor != null )
                    first = predecessor.next();
                if( topSeparator ){
                    menu.add( new JPopupMenu.Separator(), first );
                }
                else{
                    menu.remove( first );
                }
            }
        }
    }
}
