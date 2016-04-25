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

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ButtonContentFilter;
import bibliothek.gui.dock.action.ButtonContentFilterListener;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.PropertyValue;

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
public abstract class BasicHandler<D extends StandardDockAction> extends AbstractBasicHandler<D, BasicButtonModel> implements BasicTrigger, BasicTitleViewItem<JComponent> {
    
    /** a listener to the action */
    private Listener listener;
    
    /** a filter telling whether the text of the action should be forwarded */
    private PropertyValue<ButtonContentFilter> filter = new PropertyValue<ButtonContentFilter>( DockAction.BUTTON_CONTENT_FILTER ){
		@Override
		protected void valueChanged( ButtonContentFilter oldValue, ButtonContentFilter newValue ){
			if( isBound() ){
				if( oldValue != null ){
					oldValue.removeListener( filterListener );
					oldValue.uninstall( getDockable(), getAction() );
				}
				if( newValue != null ){
					newValue.addListener( filterListener );
					newValue.install( getDockable(), getAction() );
				}
			}
			
			updateText();
		}
	};
    
	/** this listener is added to the current {@link #filter} */
	private ButtonContentFilterListener filterListener = new ButtonContentFilterListener(){
		public void showTextChanged( ButtonContentFilter filter, Dockable dockable, DockAction action ){
			if( (action == null || action == getAction()) && (dockable == null || dockable == getDockable())){
				updateText();
			}
		}
	};
	
    /**
     * Creates a new handler.
     * @param action the action which will be observed.
     * @param dockable the dockable for which the action is shown
     */
    public BasicHandler( D action, Dockable dockable ){
        super( action, dockable );
    }
    
    public JComponent getItem() {
        return getModel().getOwner();
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
        getModel().setOrientation( orientation );
    }
    
    public abstract void triggered();
    
    public void bind(){
    	if( listener == null )
    		listener = createListener();

    	filter.setProperties( getDockable().getController() );
    	
    	updateTooltip();
    	
    	BasicButtonModel model = getModel();
    	StandardDockAction action = getAction();
    	Dockable dockable = getDockable();
    	
    	for( ActionContentModifier modifier : action.getIconContexts( dockable )){
    		model.setIcon( modifier, action.getIcon( dockable, modifier ) );
    	}
    	
    	updateText();
    	model.setEnabled( action.isEnabled( dockable ) );
    	model.setDockableRepresentative( action.getDockableRepresentation( dockable ) );
    	
    	action.addDockActionListener( listener );
    	
    	super.bind();
    	
    	filter.getValue().addListener( filterListener );
    	filter.getValue().install( getDockable(), getAction() );
    }
    
    public void unbind(){
    	filter.getValue().removeListener( filterListener );
    	filter.getValue().uninstall( getDockable(), getAction() );
    	
    	super.unbind();
    	
    	filter.setProperties( (DockController)null );
    	getModel().setDockableRepresentative( null );
    	getAction().removeDockActionListener( listener );
    }
    
    /**
     * Creates a listener which forwards changes in the action to the model.
     * @return the new listener
     */
    protected Listener createListener(){
        return new Listener();
    }
    
    /**
     * Updates the text of the model.
     */
    private void updateText(){
    	Dockable dockable = getDockable();
    	StandardDockAction action = getAction();
    	if( filter.getValue().showText( dockable, action )){
    		getModel().setText( action.getText( dockable ) );
    	}
    	else{
    		getModel().setText( null );
    	}
    }
    
    /**
     * Changes the tooltip of the model.
     */
    private void updateTooltip(){
    	Dockable dockable = getDockable();
    	StandardDockAction action = getAction();
        String tooltip = action.getTooltipText( dockable );
        if( tooltip == null || tooltip.length() == 0 )
            tooltip = action.getText( dockable );
        
        getModel().setToolTipText( tooltip );
    }
    
    /**
     * A listener to the action of the enclosing handler. This listener forwards
     * every change in the action to the model.
     * @author Benjamin Sigg
     */
    protected class Listener implements StandardDockActionListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	Dockable dockable = getDockable();
            if( dockables.contains( dockable ))
            	getModel().setEnabled( action.isEnabled( dockable ) );
        }

        public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
        	Dockable dockable = getDockable();
            if( dockables.contains( dockable )){
            	BasicButtonModel model = getModel();
            	
            	if( modifier == null ){	
            		model.clearIcons();
            		for( ActionContentModifier index : action.getIconContexts( dockable )){
            			model.setIcon( index, action.getIcon( dockable, index ) );
            		}
	        	}
	        	else{
	        		model.setIcon( modifier, action.getIcon( dockable, modifier ));	
	        	}
            }
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	Dockable dockable = getDockable();
            if( dockables.contains( dockable )){
            	updateText();
                updateTooltip();
            }
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
        	Dockable dockable = getDockable();
            if( dockables.contains( dockable ))
                updateTooltip();
        }
        
        public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
	        Dockable dockable = getDockable();
	        if( dockables.contains( dockable ))
	        	getModel().setDockableRepresentative( action.getDockableRepresentation( dockable ) );
        }
    }

}
