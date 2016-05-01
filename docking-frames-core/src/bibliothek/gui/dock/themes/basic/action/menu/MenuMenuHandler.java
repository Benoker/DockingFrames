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
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.MenuDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A handler that manages a menu. The menu can either be toplevel 
 * (like a popup-menu) or a submenu of another menu.
 * @author Benjamin Sigg
 */
public class MenuMenuHandler extends AbstractMenuHandler<JMenu, MenuDockAction> {
    /** the observed source */
    private DockActionSource source;
    
    /** the menu to add or remove children */
    private Menu menu;
    
    /** the current actions of the menu */
    private List<ActionItem> actions = new ArrayList<ActionItem>();
    
    /** a listener to the source */
    private Listener sourceListener = new Listener();
    
    /** all the listeners that were added to this handler */
    private List<ActionListener> listeners = new ArrayList<ActionListener>();
    
    /** what kind of items to show */
    private ViewTarget<? extends MenuViewItem<JComponent>> target = ViewTarget.MENU;
    
    /**
     * Creates a new handler
     * @param action the observed action
     * @param dockable the dockable for which items are inserted into the menu
     */
    public MenuMenuHandler( MenuDockAction action, Dockable dockable ){
    	this( action, dockable, ViewTarget.MENU );
    }
    
    /**
     * Creates a new handler
     * @param action the observed action
     * @param dockable the dockable for which items are inserted into the menu
     * @param target what kind of view to use on the menu
     */
    public MenuMenuHandler( MenuDockAction action, Dockable dockable, ViewTarget<? extends MenuViewItem<JComponent>> target ){
        super( action, dockable, new JMenu());
        if( target == null ){
        	throw new IllegalArgumentException( "target must not be null" );
        }
        this.target = target;
        setup( action.getMenu( dockable ), new JMenuWrapper( item ) );
    }
    
    /**
     * Creates a new handler
     * @param source the observed source
     * @param dockable the dockable for which actions are dispatched
     * @param menu the menu where items will be inserted
     */
    public MenuMenuHandler( DockActionSource source, Dockable dockable, JPopupMenu menu ){
    	super( null, dockable, null );
        setup( source, new JPopupMenuWrapper( menu ));
    }
    
    /**
     * Sets up this handler
     * @param source the observed source
     * @param menu the menu whose values will be changed
     */
    private void setup( DockActionSource source, Menu menu ){
        this.source = source;
        this.menu = menu;
    }
    
    public void addActionListener( ActionListener listener ){
    	// ignore
    }
    
    public void removeActionListener( ActionListener listener ){
    	// ignore
    }
    
    /**
     * Adds a listener to this handler, the listener will be invoked if one of the children
     * of this handler fires an action. The source of the {@link ActionEvent} will be the
     * {@link DockAction} which fired the event. 
     * @param listener a new listener
     */
    public void addChildrenActionListener( ActionListener listener ){
    	listeners.add( listener );
    }
    
    /**
     * Removes <code>listener</code> from this handler.
     * @param listener the listener to remove
     * @see #addChildrenActionListener(ActionListener)
     */
    public void removeChildrenActionListener( ActionListener listener ){
    	listeners.remove( listener );
    }
    
    /**
     * Fires <code>event</code> to all {@link ActionListener}s currently known to this handler.
     * @param event the event to fire
     */
    protected void fireActionEvent( ActionEvent event ){
    	for( ActionListener listener : listeners.toArray( new ActionListener[ listeners.size() ] )){
    		listener.actionPerformed( event );
    	}
    }
    
    @Override
    public void bind() {
        super.bind();
        source.addDockActionSourceListener( sourceListener );
        
        for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
            DockAction action = source.getDockAction( i );
            ActionItem item = new ActionItem();
            item.action = action;
            actions.add( item );
            MenuViewItem<JComponent> handler = handlerFor( action );
            if( handler != null ){
            	item.handler = handler;
            	item.bind();
            	item.action.bind( dockable );
	            handler.bind();
	            if( handler.getItem() != null ){
	            	menu.add( handler.getItem() );
	            }
            }
        }
    }
    
    /**
     * Creates a new view for <code>action</code>. The default implementation
     * uses the {@link ActionViewConverter} and sets the {@link ViewTarget} to
     * {@link ViewTarget#MENU}.
     * @param action an action
     * @return a handler ready to work with <code>action</code>.
     */
    protected MenuViewItem<JComponent> handlerFor( DockAction action ){
    	Dockable dockable = getDockable();
    	MenuViewItem<JComponent> result = dockable.getController().getActionViewConverter().createView( action, target, dockable );
    	
    	return result;
    }
    
    /**
     * Searches for the first view which is used for <code>action</code>.
     * @param action some child of this handler
     * @return the view used for <code>action</code>, <code>null</code> if 
     * <code>action</code> is not found or if this handler is not bound
     */
    public MenuViewItem<JComponent> getViewFor( DockAction action ){
    	for( ActionItem  item : actions ){
    		if( item.action == action ){
    			return item.handler;
    		}
    	}
    	return null;
    }
    
    @Override
    public void unbind() {
        super.unbind();
        source.removeDockActionSourceListener( sourceListener );
        menu.removeAll();
        
        for( ActionItem item : actions ){
        	item.unbind();
        	if( item.handler != null ){
        		item.handler.unbind();
        		item.action.unbind( dockable );
        	}
        }
        
        actions.clear();
    }
    
    /**
     * A listener to the source of the enclosing handler.
     * @author Benjamin Sigg
     */
    private class Listener implements DockActionSourceListener{
        /**
         * Removes all items of the menu, and then adds them again. In this
         * way, all items have the correct order, and separators can be inserted
         * easily between the items.
         */
        private void reput(){
            menu.removeAll();
            for( ActionItem item : actions ){
            	if( item.handler != null && item.handler.getItem() != null ){
            		menu.add( item.handler.getItem() );
            	}
            }
        }
        
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i<=lastIndex; i++ ){
            	DockAction action = source.getDockAction( i );
            	ActionItem item = new ActionItem();
            	item.action = action;
            	actions.add( i, item );
            	MenuViewItem<JComponent> handler = handlerFor( action );
            	if( handler != null ){
            		action.bind( dockable );
            		handler.bind();
            		item.handler = handler;
            		item.bind();
            	}
            }
            
            reput();
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- ){
            	ActionItem item = actions.remove( i );
            	item.unbind();
            	if( item.handler != null ){
            		item.handler.unbind();
            		item.action.unbind( dockable );
            	}
            }
            
            reput();
        }
    }
    
    /**
     * An item of the menu.
     * @author Benjamin Sigg
     */
    private class ActionItem implements ActionListener{
    	/** the action this item represents */
    	public DockAction action;
    	/** the handler of the visualization, might be <code>null</code> */
    	public MenuViewItem<JComponent> handler;
    	
    	/**
    	 * Allows this {@link ActionItem} to add a listener to {@link #handler};
    	 */
    	public void bind(){
    		if( handler != null ){
    			handler.addActionListener( this );
    		}
    	}
    	
    	/**
    	 * Informs this {@link ActionItem} that it no longer is allowed to
    	 * add listeners to {@link #handler}
    	 */
    	public void unbind(){
    		if( handler != null ){
    			handler.removeActionListener( this );
    		}
    	}
    	
    	public void actionPerformed( ActionEvent e ){
    		fireActionEvent( new ActionEvent( action, e.getID(), e.getActionCommand(), e.getWhen(), e.getModifiers() ));
    	}
    }
    
    /**
     * A representation of a menu.
     * @author Benjamin Sigg
     */
    private interface Menu{
        /**
         * Adds an item to the menu.
         * @param item the new item
         */
        public void add( JComponent item );
        
        /**
         * Remove all elements from this menu
         */
        public void removeAll();
        
        /**
         * Get this menu as a JMenuItem.
         * @return the item or <code>null</code>
         */
        public JMenuItem getItem();
    }
    
    /**
     * A wrapper from {@link JMenu} to {@link Menu}.
     * @author Benjamin Sigg
     */
    private static class JMenuWrapper implements Menu{
    	/** the menu wrapped by this object */
        private JMenu menu;
        
        /**
         * Creates a new wrapper.
         * @param menu the menu to cover
         */
        public JMenuWrapper( JMenu menu ){
            this.menu = menu;
        }
        
        public void add( JComponent item ) {
            menu.add( item );
        }
        
        public JMenuItem getItem() {
            return menu;
        }
        public void removeAll() {
            menu.removeAll();
        }
    }
    
    /**
     * A wrapper from {@link JPopupMenu} to {@link Menu}.
     * @author Benjamin Sigg
     *
     */
    private static class JPopupMenuWrapper implements Menu{
    	/** the menu covered by this wrapper */
        private JPopupMenu menu;
        
        /**
         * Creates a new wrapper.
         * @param menu the menu to cover
         */
        public JPopupMenuWrapper( JPopupMenu menu ){
            this.menu = menu;
        }
        
        public void add( JComponent item ) {
            menu.add( item );
        }

        public JMenuItem getItem() {
            return null;
        }
        public void removeAll() {
            menu.removeAll();
        }
    }
}
