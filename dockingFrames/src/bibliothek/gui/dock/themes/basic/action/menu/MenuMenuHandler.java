/**
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
import bibliothek.gui.dock.action.view.ViewItem;
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
    private Listener listener = new Listener();
    
    /**
     * Creates a new handler
     * @param action the observed action
     * @param dockable the dockable for which items are inserted into the menu
     */
    public MenuMenuHandler( MenuDockAction action, Dockable dockable ){
        super( action, dockable, new JMenu());
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
    
    @Override
    public void bind() {
        super.bind();
        source.addDockActionSourceListener( listener );
        
        for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
            DockAction action = source.getDockAction( i );
            ActionItem item = new ActionItem();
            item.action = action;
            actions.add( item );
            ViewItem<JComponent> handler = handlerFor( action );
            if( handler != null ){
            	item.handler = handler;
            	item.action.bind( dockable );
	            handler.bind();
	            menu.add( handler.getItem() );
            }
        }
    }
    
    /**
     * Creates a new {@link AbstractMenuHandler} for <code>action</code>.
     * @param action an action
     * @return a handler ready to work with <code>action</code>.
     */
    private ViewItem<JComponent> handlerFor( DockAction action ){
    	Dockable dockable = getDockable();
    	return dockable.getController().getActionViewConverter().createView( action, ViewTarget.MENU, dockable );
    }
    
    @Override
    public void unbind() {
        super.unbind();
        source.removeDockActionSourceListener( listener );
        menu.removeAll();
        
        for( ActionItem item : actions ){
        	if( item.handler != null )
        		item.handler.unbind();
        	item.action.unbind( dockable );
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
         * easely between the items.
         */
        private void reput(){
            menu.removeAll();
            for( ActionItem item : actions ){
            	if( item.handler != null )
            		menu.add( item.handler.getItem() );
            }
        }
        
        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i<=lastIndex; i++ ){
            	DockAction action = source.getDockAction( i );
            	ActionItem item = new ActionItem();
            	item.action = action;
            	actions.add( i, item );
            	ViewItem<JComponent> handler = handlerFor( action );
            	if( handler != null ){
            		action.bind( dockable );
            		handler.bind();
            		item.handler = handler;
            	}
            }
            
            reput();
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- ){
            	ActionItem item = actions.remove( i );
            	if( item.handler != null ){
            		item.handler.unbind();
            		item.action.unbind( dockable );
            	}
            }
        }
    }
    
    /**
     * An item of the menu.
     * @author Benjamin Sigg
     */
    private class ActionItem{
    	/** the action this item represents */
    	public DockAction action;
    	/** the handler of the visualization, might be <code>null</code> */
    	public ViewItem<JComponent> handler;
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
     * A wrapper JMenu to Menu.
     * @author Benjamin Sigg
     */
    private class JMenuWrapper implements Menu{
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
     * A Wrapper JPopupMenu to Menu.
     * @author Benjamin Sigg
     *
     */
    private class JPopupMenuWrapper implements Menu{
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
