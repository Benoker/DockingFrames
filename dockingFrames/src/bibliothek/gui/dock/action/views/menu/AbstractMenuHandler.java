/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.action.views.menu;

import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JMenuItem;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.event.StandardDockActionListener;

/**
 * A handler that connects a {@link JMenuItem} with a {@link DockAction}.
 * @param <I> Item used by this handler
 * @param <D> Action used by this handler
 * @author Benjamin Sigg
 */
public abstract class AbstractMenuHandler<I extends JMenuItem, D extends StandardDockAction> implements MenuViewItem<JComponent>{
    /** the visual representation of the action, may be <code>null</code> */
    protected I item;
    
    /** the Dockable for which actions are dispatched */
    protected Dockable dockable;
    
    /** the action shown by the item of this handler, may be <code>null</code> */
    protected D action;
    
    /** a listener to the action, changes text, icon, etc.. of the item */
    private Listener listener;
    
    /**
     * Creates a new handler with predefined item.
     * @param action the action to observe
     * @param dockable the dockable for which actions are dispatched
     * @param item the item whose values have to be updated
     */
    public AbstractMenuHandler( D action, Dockable dockable, I item ){
        this.action = action;
        this.dockable = dockable;
        this.item = item;
    }
    
    /**
     * Gets the element for which actions are dispatched.
     * @return the element
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    /**
     * Gets the action that is observed by this handler.
     * @return the action, may be <code>null</code>
     */
    public D getAction() {
        return action;
    }
    
    /**
     * Gets the item whose values are updated by this handler.
     * @return the item, may be <code>null</code>
     */
    public JMenuItem getItem(){
        return item;
    }
    
    /**
     * Connects this handler to its action.
     */
    public void bind(){
        if( action != null ){
            if( listener == null ){
                action.bind( dockable );
                
                listener = new Listener();
                action.addDockActionListener( listener );
                
                item.setEnabled( action.isEnabled( dockable ));
                item.setIcon( action.getIcon( dockable ));
                item.setDisabledIcon( action.getDisabledIcon( dockable ) );
                item.setText( action.getText( dockable ));
                item.setToolTipText( action.getTooltipText( dockable ));
            }
            else
                throw new IllegalStateException( "Handler is already binded" );
        }
    }
    
    /**
     * Disconnects this handler from its action
     */
    public void unbind(){
        if( action != null ){
            if( listener != null ){
                action.unbind( dockable );
                action.removeDockActionListener( listener );
                listener = null;
            }
            else
                throw new IllegalStateException( "Handler is already unbinded" );
        }
    }
    
    /**
     * A listener to an action, changes the values of the item of the
     * enclosing handler.
     * @author Benjamin Sigg
     */
    private class Listener implements StandardDockActionListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
            item.setEnabled( action.isEnabled( dockable ));
        }

        public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            item.setIcon( action.getIcon( dockable ));
        }
        
        public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
        	item.setDisabledIcon( action.getDisabledIcon( dockable ));
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            item.setText( action.getText( dockable ));
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            item.setToolTipText( action.getTooltipText( dockable ));
        }        
    }
}
