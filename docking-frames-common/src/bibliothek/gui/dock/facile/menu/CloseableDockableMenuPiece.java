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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.support.menu.BaseMenuPiece;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A piece of a menu allowing to close or reopen some {@link Dockable}s that
 * are registered in a {@link DockFrontend}.
 * @author Benjamin Sigg
 *
 */
public class CloseableDockableMenuPiece extends BaseMenuPiece{
    /** the frontend that is currently observed */
    private DockFrontend frontend;
    
    /** a list of all items shown in the menu */
    private Map<Dockable, Item> items = new HashMap<Dockable, Item>();
    
    /** a listener collecting all new {@link Dockable}s of the {@link #frontend} */
    private DockableCollector collector = new DockableCollector();
    
    /** the comparator deciding how to sort the items */
    private Comparator<String> order = new Comparator<String>(){
    	private Collator collator = Collator.getInstance();

		public int compare( String o1, String o2 ){
			return collator.compare( o1, o2 );
		}
    };
    
    /** the identifier of the next {@link #order(int)} that will actually be executed */
    private AtomicInteger orderCommand = new AtomicInteger(0);
    
    /**
     * Creates a new piece
     */
    public CloseableDockableMenuPiece() {
    	// do nothing
    }
    
    /**
     * Creates a new piece
     * @param frontend the list of {@link Dockable}s, can be <code>null</code>
     */
    public CloseableDockableMenuPiece( DockFrontend frontend ) {
        setFrontend( frontend );
    }
    
    /**
     * Sets the order of the menu items. The default behavior is to order the
     * items alphabetically.
     * @param order the order, or <code>null</code>
     */
    public void setOrder( Comparator<String> order ){
		this.order = order;
		reorder();
	}
    
    /**
     * Gets the order of the menu items.
     * @return the order, can be <code>null</code>
     */
    public Comparator<String> getOrder(){
		return order;
	}
    
    /**
     * Asynchronously order the menu items
     */
    protected void reorder(){
    	final int identifier = orderCommand.incrementAndGet();
    	if( EventQueue.isDispatchThread() ){
	    	EventQueue.invokeLater( new Runnable(){
				public void run(){
					order( identifier );
				}
			} );
    	}
    	else{
    		order( identifier );
    	}
    }
    
    private void order( int identifier ){
    	if( identifier == orderCommand.get() && order != null ){
    		Item[] items = this.items.values().toArray( new Item[ this.items.size() ] );
    		Arrays.sort( items, new Comparator<Item>(){
    			public int compare( Item o1, Item o2 ){
    				return order.compare( o1.getText(), o2.getText() );
    			}
			});
    		
    		removeAll();
    		for( Item item : items ){
    			add( item );
    		}
    	}
    }
    
    /**
     * Gets the frontend which is observed by this piece.
     * @return the frontend, might be <code>null</code>
     * @see #setFrontend(DockFrontend)
     */
    public DockFrontend getFrontend() {
        return frontend;
    }
    
    /**
     * Sets the frontend which will be observed by this piece. Every
     * {@link Dockable} that is registered at the <code>frontend</code> will
     * get an item in the menu of this piece.
     * @param frontend the list of {@link Dockable}s, can be <code>null</code>
     */
    public void setFrontend( DockFrontend frontend ) {
        if( this.frontend != frontend ){
        	if( isBound() ){
	            if( this.frontend != null ){
	                this.frontend.removeFrontendListener( collector );
	                for( Item item : items.values() ){
	                    item.destroy();
	                    remove( item );
	                }
	                items.clear();
	            }
        	}
            
            this.frontend = frontend;
            
            if( isBound() ){
	            if( this.frontend != null ){
	                this.frontend.addFrontendListener( collector );
	                for( Dockable dockable : this.frontend.listDockables() ){
	                    collector.added( frontend, dockable );
	                }
	            }
            }
        }
    }
    
    @Override
    public void bind(){
	    if( !isBound() ){
	    	super.bind();
	    	
	    	if( frontend != null ){
                frontend.addFrontendListener( collector );
                for( Dockable dockable : this.frontend.listDockables() ){
                    collector.added( frontend, dockable );
                }
            }
	    }
    }
    
    @Override
    public void unbind(){
    	if( isBound() ){
    		super.unbind();
    		if( frontend != null ){
    			frontend.removeFrontendListener( collector );
    			for( Item item : items.values() ){
    				item.destroy();
    				remove( item );
    			}
    			items.clear();
    		}
    	}
    }
    
    /**
     * Creates a new item for the menu.
     * @param dockable the element which will be shown/hidden when the user
     * clicks onto the item.
     * @return the new item
     */
    protected Item create( Dockable dockable ){
        return new Item( dockable );
    }
    
    /**
     * Adds an earlier created item into the menu, subclasses might override
     * this method to add the item at a different location.
     * @param item the item to insert
     * @see #setOrder(Comparator)
     */
    protected void insert( Item item ){
        add( item );
        reorder();
    }
    
    /**
     * Ensures that <code>dockable</code> is visible.
     * @param dockable the element to show
     */
    protected void show( Dockable dockable ){
        frontend.show( dockable );
    }
    
    /**
     * Ensures that <code>dockable</code> is not visible.
     * @param dockable the element to hide
     */
    protected void hide( Dockable dockable ){
        frontend.hide( dockable );
    }
    
    /**
     * Tells whether an item should be inserted into the menu for the given
     * <code>dockable</code> or not.
     * @param dockable the element to check
     * @return <code>true</code> if there should be an item added to the menu
     */
    protected boolean include( Dockable dockable ){
        return frontend.isHideable( dockable );
    }
    
    /**
     * Ensures that <code>dockable</code> has an item if it is {@link #include(Dockable) included},
     * and does not have otherwise.
     * @param dockable the element whose item is to be checked
     */
    public void check( Dockable dockable ){
        if( include( dockable ) ){
            if( !items.containsKey( dockable ))
                collector.added( frontend, dockable );
        }
        else{
            if( items.containsKey( dockable )){
                collector.removed( frontend, dockable );
            }
        }
    }
    
    /**
     * A class that collects {@link Dockable}s and manages the states of the
     * {@link Item}s of the enclosing {@link CloseableDockableMenuPiece}.
     * @author Benjamin Sigg
     *
     */
    private class DockableCollector extends DockFrontendAdapter{
        @Override
        public void hidden( DockFrontend fronend, Dockable dockable ) {
            Item item = items.get( dockable );
            if( item != null ){
                item.setDockableState( false );
            }
        }
        
        @Override
        public void shown( DockFrontend frontend, Dockable dockable ) {
            Item item = items.get( dockable );
            if( item != null ){
                item.setDockableState( true );
            }
        }
        
        @Override
        public void added( DockFrontend frontend, Dockable dockable ) {
            if( include( dockable )){
                Item item = create( dockable );
                item.setDockableState( frontend.isShown( dockable ) );
                items.put( dockable, item );
                insert( item );
            }
        }
        
        @Override
        public void removed( DockFrontend frontend, Dockable dockable ) {
            Item item = items.remove( dockable );
            if( item != null ){
                item.destroy();
                remove( item );
            }
        }
        
        @Override
        public void hideable( DockFrontend frontend, Dockable dockable, boolean hideable ) {
            check( dockable );
        }
    }
    
    /**
     * An item showing the visibility state of one <code>Dockable</code>.
     * @author Benjamin Sigg
     */
    protected class Item extends JCheckBoxMenuItem implements DockableListener, ActionListener{
        /** the element that might be shown or hidden by this item */
        private Dockable dockable;
        
        /** whether the properties of this item are currently changing */
        private boolean onChange = false;
        
        /**
         * Creates a new item.
         * @param dockable the element that will be shown or hidden when the
         * user clicks onto this item
         */
        public Item( Dockable dockable ){
            this.dockable = dockable;
            dockable.addDockableListener( this );
            addActionListener( this );
            
            setIcon( dockable.getTitleIcon() );
            setText( dockable.getTitleText() );
        }
        
        /**
         * Sets whether the <code>Dockable</code> of this item is currently
         * visible or not.
         * @param visible the new state
         */
        public void setDockableState( boolean visible ){
            try{
                onChange = true;
                setSelected( visible );
            }
            finally{
                onChange = false;
            }
        }
        
        /**
         * Removes all listeners, frees as many resources as possible
         */
        public void destroy(){
            dockable.removeDockableListener( this );
            removeActionListener( this );
            setIcon( null );
            setText( "" );
        }

        public void titleBound( Dockable dockable, DockTitle title ) {
            // ignore
        }

        public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
            setIcon( newIcon );
        }

        public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ) {
            setText( newTitle );
            reorder();
        }

        public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip ) {
            // ignore
        }
        
        public void titleUnbound( Dockable dockable, DockTitle title ) {
            // ignore
        }
        
        public void titleExchanged( Dockable dockable, DockTitle title ) {
            // ignore
        }

        public void actionPerformed( ActionEvent e ) {
            if( !onChange ){
                if( isSelected() )
                    CloseableDockableMenuPiece.this.show( dockable );
                else
                    CloseableDockableMenuPiece.this.hide( dockable );
            }
        }
    }
}
