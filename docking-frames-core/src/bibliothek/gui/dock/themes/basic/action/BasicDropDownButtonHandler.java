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
import java.util.*;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ButtonContentFilter;
import bibliothek.gui.dock.action.ButtonContentFilterListener;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.dropdown.DropDownFilter;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DropDownActionListener;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownIcon;
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A handler connecting a {@link DropDownAction} with a {@link BasicDropDownButtonModel}
 * and its view. Clients should call the method {@link #setModel(BasicButtonModel)} to
 * connect model and handler.
 * @author Benjamin Sigg
 */
public class BasicDropDownButtonHandler extends AbstractBasicHandler<DropDownAction, BasicDropDownButtonModel> implements BasicDropDownButtonTrigger, BasicTitleViewItem<JComponent> {
    /** the current source of child-actions */
    private DockActionSource source;
    /** a listener to the model */
    private Listener listener = new Listener();
    
    /** the currently selected item, can be <code>null</code> */
    private DropDownItemHandle selection;
    /** the currently known actions */
    private List<DockAction> actions = new ArrayList<DockAction>();
    /** the views for the items of {@link #actions}. Not all actions have a view. */
    private Map<DockAction, DropDownItemHandle> items = new HashMap<DockAction, DropDownItemHandle>();
    
    /** the menu to show when the button is clicked */
    private JPopupMenu menu = new JPopupMenu();
    
    /** connection between current selection and filter */
    private SelectionView selectionView = new SelectionView();
    /** connection between filter and button */
    private ButtonView buttonView = new ButtonView();
    /** filters the properties of the action and its selection */
    private DropDownFilter filter;
    
    /** the icon that indicates the button for opening the popup menu */
    private DropDownIcon dropDownIcon;

    /** a filter telling whether the text of the action should be forwarded */
    private PropertyValue<ButtonContentFilter> buttonContentFilter = new PropertyValue<ButtonContentFilter>( DockAction.BUTTON_CONTENT_FILTER ){
		@Override
		protected void valueChanged( ButtonContentFilter oldValue, ButtonContentFilter newValue ){
			if( isBound() ){
				if( oldValue != null ){
					oldValue.removeListener( buttonContentFilterListener );
					oldValue.uninstall( getDockable(), getAction() );
				}
				if( newValue != null ){
					newValue.addListener( buttonContentFilterListener );
					newValue.install( getDockable(), getAction() );
				}
			}
			
			buttonView.updateText();
		}
	};
    
	/** this listener is added to the current {@link #buttonContentFilter} */
	private ButtonContentFilterListener buttonContentFilterListener = new ButtonContentFilterListener(){
		public void showTextChanged( ButtonContentFilter filter, Dockable dockable, DockAction action ){
			if( (action == null || action == getAction()) && (dockable == null || dockable == getDockable())){
				buttonView.updateText();
			}
		}
	};
	
    /**
     * Creates a new handler.
     * @param action the action to observe
     * @param dockable the element for which the <code>action</code> is shown
     */
    public BasicDropDownButtonHandler( DropDownAction action, Dockable dockable ){
    	super( action, dockable );
    }
    
    public void bind(){
    	DropDownAction action = getAction();
    	Dockable dockable = getDockable();
    	
    	if( dropDownIcon != null ){
    		if( !dropDownIcon.isInitialized() ){
    			dropDownIcon.init(  getDockable(), getAction(), this );
    		}
    		dropDownIcon.setController( dockable.getController() );
    	}
    	
        action.bind( dockable );
        filter = action.getFilter( dockable ).createView( action, dockable, buttonView );
        filter.bind();
        
        source = action.getSubActions( dockable );
        
        for( int i = 0, n = source.getDockActionCount(); i<n; i++ ){
            DockAction sub = source.getDockAction( i );
            add( i, sub );
        }
        
        reset();
        selection = items.get( action.getSelection( dockable ) );
        if( selection != null )
            selection.getView().setView( selectionView );
        
        action.addDropDownActionListener( listener );
        action.addDockActionListener( listener );
        source.addDockActionSourceListener( listener );
        
        getModel().setEnabled( action.isEnabled( dockable ) );
        buttonContentFilter.setProperties( dockable.getController() );
        
        super.bind();
        
        buttonContentFilter.getValue().addListener( buttonContentFilterListener );
    }
    
    public void unbind(){
    	DropDownAction action = getAction();
    	Dockable dockable = getDockable();
    	
    	if( dropDownIcon != null ){
    		dropDownIcon.setController( null );
    	}
    	
        action.removeDockActionListener( listener );
        action.removeDropDownActionListener( listener );
        source.removeDockActionSourceListener( listener );
        
        for( int i = actions.size()-1; i >= 0; i-- )
            remove( i );
        
        menu.removeAll();
        
        if( selection != null ){
            selection.getView().setView( null );
        }
        
        filter.unbind();
        filter = null;
        action.unbind( dockable );
        
        source = null;
        selection = null;
        items.clear();
        actions.clear();
        
        buttonContentFilter.getValue().removeListener( buttonContentFilterListener );
        
        super.unbind();
        
        buttonContentFilter.setProperties( (DockController)null );
        getModel().setDockableRepresentative( null );
    }

    /**
     * Gets an icon that can be used to represent an area that opens the popup menu when clicked.
     * @return an icon, not <code>null</code>
     */
    public Icon getDropDownIcon(){
    	ensureDropDownIcon();
		return dropDownIcon;
	}
    
    /**
     * Gets a disabled version of {@link #getDropDownIcon()}.
     * @return the icon, not <code>null</code>
     */
    public Icon getDisabledDropDownIcon(){
    	ensureDropDownIcon();
    	return dropDownIcon.getDisabledIcon();
    }
    
    private void ensureDropDownIcon(){
    	if( dropDownIcon == null ){
    		dropDownIcon = new DropDownIcon();
    		if( isBound() ){
    			dropDownIcon.init(  getDockable(), getAction(), this );
    			dropDownIcon.setController( getDockable().getController() );
    		}
    	}
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
    
    public void setOrientation( Orientation orientation ){
    	getModel().setOrientation( orientation );
    }
    
    /**
     * Adds an action into the list of all known actions.
     * @param index the location of the action
     * @param action the new action
     */
    private void add( int index, DockAction action ){
    	Dockable dockable = getDockable();
    	actions.add( index, action );
        DropDownViewItem item = action.createView( ViewTarget.DROP_DOWN, dockable.getController().getActionViewConverter(), dockable );
        if( item != null ){
            DropDownItemHandle entry = new DropDownItemHandle( action, item, dockable, getAction() );
            entry.bind();
            items.put( action, entry );
            if( item.getItem() != null ){
            	menu.add( item.getItem() );
            }
        }
    }
    
    /**
     * Removes an action from the list of all known actions.
     * @param index the location of the action
     */
    private void remove( int index ){
        DockAction action = actions.remove( index );
        DropDownItemHandle item = items.remove( action );
        if( item != null ){
        	if( item.getView().getItem() != null ){
        		menu.remove( item.getView().getItem() );
        	}
        	item.unbind();
        }
    }
    
    public JComponent getItem(){
        return getModel().getOwner();
    }
    
    public void triggered(){
    	BasicDropDownButtonModel model = getModel();
    	
        if( model.isMouseOverDropDown() )
            popupTriggered();
        else{
            if( selection == null || !model.isSelectionEnabled() || !selection.getView().isTriggerable(  true ) )
                popupTriggered();
            else{
                if( selection.getView().isTriggerable( true ) ){
                    selection.getView().triggered();
                }
            }
        }
    }
    
    /**
     * Shows the popup menu
     */
    public void popupTriggered(){
    	BasicDropDownButtonModel model = getModel();
    	JComponent button = model.getOwner();
        
        if( model.getOrientation().isHorizontal() ){
            menu.show( button, 0, button.getHeight() );
        }
        else{
            menu.show( button, button.getWidth(), 0 );
        }
    }
    
    /**
     * Update the look and feel of the menu
     */
    public void updateUI(){
        if( menu != null )
            SwingUtilities.updateComponentTreeUI( menu );
    }
    
    /**
     * Gets the view which contains information about the currently selected
     * action.
     * @return the information
     */
    protected ButtonView getButtonView(){
        return buttonView;
    }
    
    /**
     * Sets all values of the {@link #filter} to <code>null</code>.
     */
    protected void reset(){
        getModel().setSelectionEnabled( false );
        if( filter != null ){
        	filter.clearIcons();
            filter.setEnabled( true );
            filter.setSelected( false );
            filter.setText( null );
            filter.setTooltip( null );
            filter.setDockableRepresentation( null );
        }
        update();
    }
    
    /**
     * Updates the {@link #filter}. This might change some contents of the button.
     */
    protected void update(){
        if( filter != null )
            filter.update( selection == null ? null : selection.getView() );
    }
    
    /**
     * A set of properties which can be set by the selected action.
     * @author Benjamin Sigg
     */
    protected class SelectionView implements DropDownView{
        public void setEnabled( boolean enabled ){
            getModel().setSelectionEnabled( enabled );
            filter.setEnabled( enabled );
            update();
        }
        
        public ActionContentModifier[] getIconContexts(){
        	return filter.getIconContexts();
        }
        
        public void clearIcons(){
        	filter.clearIcons();
        	update();
        }
        
        public void setIcon( ActionContentModifier modifier, Icon icon ){
        	filter.setIcon( modifier, icon );
        	update();
        }
        
        public void setSelected( boolean selected ){
            filter.setSelected( selected );
            update();
        }

        public void setText( String text ){
            filter.setText( text );
            update();
        }

        public void setTooltip( String tooltip ){
            filter.setTooltip( tooltip );
            update();
        }
        
        public void setDockableRepresentation( Dockable dockable ){
        	filter.setDockableRepresentation( dockable );
        	update();
        }
    }
    
    /**
     * A view that sends all values directly to the button.
     * @author Benjamin Sigg
     */
    protected class ButtonView implements DropDownView{
    	private String text;
    	
    	public void setIcon( ActionContentModifier modifier, Icon icon ){
    		getModel().setIcon( modifier, icon );
    	}
    	
    	public ActionContentModifier[] getIconContexts(){
    		return getModel().getIconContexts();
    	}
    	
    	public void clearIcons(){
    		getModel().clearIcons();
    	}

        public void setEnabled( boolean enabled ){
        	getModel().setSelectionEnabled( enabled );
        }

        public void setSelected( boolean selected ){
        	getModel().setSelected( selected );
        }

        public void setText( String text ){
        	this.text = text;
        	updateText();
        }
        
        /**
         * Updates the text that is shown by the button, respects the {@link BasicDropDownButtonHandler#buttonContentFilter}.
         */
        public void updateText(){
        	if( buttonContentFilter.getValue().showText( getDockable(), getAction() )){
        		getModel().setText( text );
        	}
        	else{
        		getModel().setText( null );
        	}
        }

        public void setTooltip( String tooltip ){
        	getModel().setToolTipText( tooltip );
        }
        
        public void setDockableRepresentation( Dockable dockable ){
        	if( isBound() ){
        		getModel().setDockableRepresentative( dockable );
        	}
        }
    }
    
    /**
     * A listener to the action that is handled by this handler
     * @author Benjamin Sigg
     */
    private class Listener implements StandardDockActionListener, DropDownActionListener, DockActionSourceListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
        	Dockable dockable = getDockable();
            if( dockables.contains( dockable ))
            	getModel().setEnabled( action.isEnabled( dockable ) );
        }
        
        public void actionRepresentativeChanged( StandardDockAction action, Set<Dockable> dockables ){
	        if( dockables.contains( getDockable() ))
	        	update();
        }
        
        public void actionIconChanged( StandardDockAction action, ActionContentModifier modifier, Set<Dockable> dockables ){
        	if( dockables.contains( getDockable() ))
                update();
        }
        
        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( getDockable() ))
                update();
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( getDockable() ))
                update();
        }
        
        public void selectionChanged( DropDownAction action, Set<Dockable> dockables, DockAction newSelection ){
            if( selection != null )
                selection.getView().setView( null );
            
            reset();
            selection = items.get( newSelection );
            
            if( selection != null )
                selection.getView().setView( selectionView );
            
            getModel().changed();
        }

        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
            for( int i = firstIndex; i <= lastIndex; i++ )
                add( i, source.getDockAction( i ));
        }

        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
            for( int i = lastIndex; i >= firstIndex; i-- )
                remove( i );
        }
    }
}
