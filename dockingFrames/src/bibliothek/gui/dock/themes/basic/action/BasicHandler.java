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
package bibliothek.gui.dock.themes.basic.action;

import java.awt.Color;
import java.awt.Component;
import java.util.Set;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A class connecting a {@link DockAction} with a {@link BasicButtonModel}. The
 * handler observes the action and reports all changed properties to the model.<br>
 * Clients should call the method {@link #setModel(BasicButtonModel)} to connect
 * the handler with a model.<br>
 * This class implements {@link BasicTitleViewItem} in order to allow clients
 * to use instances as results of some {@link ViewGenerator ViewGenerators}.
 * @author Benjamin Sigg
 *
 * @param <D> The type of action observed by this model
 */
public abstract class BasicHandler<D extends StandardDockAction> implements BasicTrigger, BasicTitleViewItem<JComponent> {
    /** the action which is observed */
    private D action;
    /** the model to which all readings from the action are sent */
    private BasicButtonModel model;
    /** the dockable for which the action is displayed */
    private Dockable dockable;
    
    /** a listener to the action */
    private Listener listener;
    
    /**
     * Creates a new handler.
     * @param action the action which will be observed.
     * @param dockable the dockable for which the action is shown
     */
    public BasicHandler( D action, Dockable dockable ){
        if( action == null )
            throw new IllegalArgumentException( "Action must not be null" );
        
        this.dockable = dockable;
        this.action = action;
    }
    
    /**
     * Sets the model to which all properties of the {@link #getAction() action}
     * are transfered.
     * @param model the model
     */
    public void setModel( BasicButtonModel model ) {
        this.model = model;
    }
    
    /**
     * Gets the model of this handler.
     * @return the model
     * @see #setModel(BasicButtonModel)
     */
    public BasicButtonModel getModel() {
        return model;
    }
    
    /**
     * Gest the action which is monitored by this handler.
     * @return the observed action
     */
    public D getAction() {
        return action;
    }
    
    /**
     * Gets the {@link Dockable} for which the {@link #getAction() action}
     * is shown.
     * @return the dockable
     */
    public Dockable getDockable() {
        return dockable;
    }
    
    public JComponent getItem() {
        return model.getOwner();
    }
    
    
    public void setBackground( Color background ) {
        Component item = getItem();
        if( item != null )
            item.setBackground( background );
    }
    
    public void setForeground( Color foreground ) {
        Component item = getItem();
        if( item != null )
            item.setForeground( foreground );
    }
    
    public void setOrientation( Orientation orientation ) {
        model.setOrientation( orientation );
    }
    
    public abstract void triggered();
    
    public void bind(){
        if( listener == null )
            listener = createListener();
        
        listener.updateTooltip();
        
        model.setIcon( action.getIcon( dockable ) );
        model.setDisabledIcon( action.getDisabledIcon( dockable ) );
        model.setEnabled( action.isEnabled( dockable ) );
        
        action.addDockActionListener( listener );
    }
    
    public void unbind(){
        action.removeDockActionListener( listener );
    }
    
    protected Listener createListener(){
        return new Listener();
    }
    
    /**
     * A listener to the action of the enclosing handler. This listener forwards
     * every change in the action to the model.
     * @author Benjamin Sigg
     */
    protected class Listener implements StandardDockActionListener{
        public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                model.setDisabledIcon( action.getDisabledIcon( dockable ) );
        }

        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                model.setEnabled( action.isEnabled( dockable ) );
        }

        public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                model.setIcon( action.getIcon( dockable ));
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                updateTooltip();
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                updateTooltip();
        }
        
        /**
         * Changes the tooltip of the model.
         */
        private void updateTooltip(){
            String tooltip = action.getTooltipText( dockable );
            if( tooltip == null || tooltip.length() == 0 )
                tooltip = action.getText( dockable );
            
            model.setToolTipText( tooltip );
        }
    }
}
