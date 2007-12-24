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

import java.util.*;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import bibliothek.gui.Dockable;
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
import bibliothek.gui.dock.themes.basic.action.dropdown.DropDownViewItem;
import bibliothek.gui.dock.title.DockTitle.Orientation;

/**
 * A handler connecting a {@link DropDownAction} with a {@link BasicDropDownButtonModel}
 * and its view. Clients should call the method {@link #setModel(BasicDropDownButtonModel)} to
 * connect model and handler.
 * @author Benjamin Sigg
 */
public class BasicDropDownButtonHandler implements BasicTrigger, BasicTitleViewItem<JComponent> {
    /** the action to observe and to trigger */
    private DropDownAction action;
    /** the current source of child-actions */
    private DockActionSource source;
    /** the element for which the action is shown */
    private Dockable dockable;
    /** the model that links to the view */
    private BasicDropDownButtonModel model;
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
    
    /**
     * Creates a new handler.
     * @param action the action to observe
     * @param dockable the element for which the <code>action</code> is shown
     */
    public BasicDropDownButtonHandler( DropDownAction action, Dockable dockable ){
        this.action = action;
        this.dockable = dockable;
    }
    
    /**
     * Sets the model to which this handler sends all properties
     * of the {@link #getAction() action}.  
     * @param model the model to inform about changes
     */
    public void setModel( BasicDropDownButtonModel model ) {
        this.model = model;
    }
    
    public void setOrientation( Orientation orientation ){
        model.setOrientation( orientation );
    }
    
    public void bind(){
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
        
        model.setEnabled( action.isEnabled( dockable ) );
    }
    
    public void unbind(){
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
    }

    /**
     * Adds an action into the list of all known actions.
     * @param index the location of the action
     * @param action the new action
     */
    private void add( int index, DockAction action ){
        actions.add( action );
        DropDownViewItem item = action.createView( ViewTarget.DROP_DOWN, dockable.getController().getActionViewConverter(), dockable );
        if( item != null ){
            DropDownItemHandle entry = new DropDownItemHandle( action, item, dockable, this.action );
            entry.bind();
            items.put( action, entry );
            menu.add( item.getItem() );
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
            item.unbind();
            menu.remove( item.getView().getItem() );
        }
    }
    
    public DropDownAction getAction(){
        return action;
    }
    
    public Dockable getDockable(){
        return dockable;
    }
    
    public JComponent getItem(){
        return model.getOwner();
    }
    
    public void triggered(){
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
        model.setSelectionEnabled( false );
        if( filter != null ){
            filter.setDisabledIcon( null );
            filter.setEnabled( true );
            filter.setIcon( null );
            filter.setSelected( false );
            filter.setText( null );
            filter.setTooltip( null );
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
            model.setSelectionEnabled( enabled );
            filter.setEnabled( enabled );
            update();
        }

        public void setIcon( Icon icon ){
            filter.setIcon( icon );
            update();
        }

        public void setDisabledIcon( Icon icon ){
            filter.setDisabledIcon( icon );
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
    }
    
    /**
     * A view that sends all values directly to the button.
     * @author Benjamin Sigg
     */
    protected class ButtonView implements DropDownView{
        public void setDisabledIcon( Icon icon ){
            model.setDisabledIcon( icon );
        }

        public void setEnabled( boolean enabled ){
            model.setSelectionEnabled( enabled );
        }

        public void setIcon( Icon icon ){
            model.setIcon( icon );
        }

        public void setSelected( boolean selected ){
            model.setSelected( selected );
        }

        public void setText( String text ){
            // ignore
        }

        public void setTooltip( String tooltip ){
            model.setToolTipText( tooltip );
        }
    }
    
    /**
     * A listener to the action that is handled by this handler
     * @author Benjamin Sigg
     */
    private class Listener implements StandardDockActionListener, DropDownActionListener, DockActionSourceListener{
        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( dockable ))
                model.setEnabled( action.isEnabled( dockable ) );
        }

        public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( dockable ))
                update();
        }
        
        public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( dockable ))
                update();
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( dockable ))
                update();
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ){
            if( dockables.contains( dockable ))
                update();
        }
        
        public void selectionChanged( DropDownAction action, Set<Dockable> dockables, DockAction newSelection ){
            if( selection != null )
                selection.getView().setView( null );
            
            reset();
            selection = items.get( newSelection );
            
            if( selection != null )
                selection.getView().setView( selectionView );
            
            model.changed();
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
