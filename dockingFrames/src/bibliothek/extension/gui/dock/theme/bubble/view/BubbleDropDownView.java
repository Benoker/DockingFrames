package bibliothek.extension.gui.dock.theme.bubble.view;

import java.util.*;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.dropdown.DropDownFilter;
import bibliothek.gui.dock.action.dropdown.DropDownView;
import bibliothek.gui.dock.action.views.ViewTarget;
import bibliothek.gui.dock.action.views.buttons.DropDownItemHandle;
import bibliothek.gui.dock.action.views.buttons.TitleViewItem;
import bibliothek.gui.dock.action.views.dropdown.DropDownViewItem;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DropDownActionListener;
import bibliothek.gui.dock.event.StandardDockActionListener;
import bibliothek.gui.dock.title.DockTitle.Orientation;

public class BubbleDropDownView implements TitleViewItem<JComponent> {
    private Dockable dockable;
    
    private DropDownAction action;
    private DropDownFilter filter;
    
    private RoundDropDownButton button;
    
    private Listener listener = new Listener();

    private SelectionView selectionView = new SelectionView();
    private DropDownItemHandle selection;
    
    private List<DockAction> actions = new ArrayList<DockAction>();
    private Map<DockAction, DropDownItemHandle> items = new HashMap<DockAction, DropDownItemHandle>();
    private DockActionSource source;
    private JPopupMenu popup = new JPopupMenu();
    
    public BubbleDropDownView( BubbleTheme theme, DropDownAction action, Dockable dockable ){
        button = new RoundDropDownButton( theme, this );
        this.action = action;
        this.dockable = dockable;
    }
    
    public void setOrientation( Orientation orientation ) {
        button.setOrientation( orientation );
    }

    public void bind() {
        filter = action.getFilter( dockable ).createView( action, dockable, new ButtonView() );
        filter.bind();
        
        source = action.getSubActions( dockable );
        source.addDockActionSourceListener( listener );
        
        for( int i = 0, n = source.getDockActionCount(); i<n; i++ )
            add( i, source.getDockAction( i ));
        
        reset();
        
        action.addDockActionListener( listener );
        action.addDropDownActionListener( listener );
        
        button.setEnabled( action.isEnabled( dockable ) );

        selection = items.get( action.getSelection( dockable ) );
        if( selection != null )
            selection.getView().setView( selectionView );
    }

    public void unbind() {
        action.removeDockActionListener( listener );
        filter.unbind();
        filter = null;
        
        source.removeDockActionSourceListener( listener );
        source = null;
        
        if( selection != null ){
            selection.getView().setView( null );
            selection = null;
        }
        
        for( int i = actions.size()-1; i >= 0; i-- )
            remove( i );
        
        popup.removeAll();
        actions.clear();
        items.clear();
    }
    
    /**
     * Sets all values of the {@link #filter} to <code>null</code>.
     */
    protected void reset(){
        button.setSelectionEnabled( false );
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
    

    public DropDownAction getAction() {
        return action;
    }

    public JComponent getItem() {
        return button;
    }
    
    /**
     * Triggers the action handled by this view.
     * @param dropdown whether the dropdown-menu should be opened or not.
     */
    public void trigger( boolean dropdown ){
        if( !dropdown ){
            if( selection == null || !selection.getView().isTriggerable( true ) || !button.isSelectionEnabled() )
                dropdown = true;
        }
        
        if( !dropdown ){
            selection.getView().triggered();
        }
        else{
            if( button.getOrientation().isHorizontal() ){
                popup.show( button, 0, button.getHeight() );
            }
            else{
                popup.show( button, button.getWidth(), 0 );
            }
        }
    }
    
    public void updateUI(){
        if( popup != null )
            SwingUtilities.updateComponentTreeUI( popup );
    }
    
    private void update(){
        filter.update( selection == null ? null : selection.getView() );
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
            popup.add( item.getItem() );
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
            popup.remove( item.getView().getItem() );
        }
    }
    
    private class ButtonView implements DropDownView{
        public void setDisabledIcon( Icon icon ) {
            button.setDisabledIcon( icon );
        }

        public void setEnabled( boolean enabled ) {
            button.setEnabled( enabled );
        }

        public void setIcon( Icon icon ) {
            button.setIcon( icon );
        }

        public void setSelected( boolean selected ) {
            button.setSelected( selected );
        }

        public void setText( String text ) {
            // ignore
        }

        public void setTooltip( String tooltip ) {
            button.setToolTipText( tooltip );
        }
    }
    
    private class SelectionView implements DropDownView{
        public void setEnabled( boolean enabled ){
            button.setSelectionEnabled( enabled );
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
    
    private class Listener implements StandardDockActionListener, DropDownActionListener, DockActionSourceListener{
        public void selectionChanged( DropDownAction action, Set<Dockable> dockables, DockAction selectedAction ) {
            if( dockables.contains( dockable )){
                if( selection != null ){
                    selection.getView().setView( null );
                    selection = null;
                }
                
                if( selectedAction != null )
                    selection = items.get( selectedAction );
                
                if( selection != null )
                    selection.getView().setView( selectionView );
                
                update();
            }
        }

        public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = firstIndex; i <= lastIndex; i++ )
                add( i, source.getDockAction( i ));
        }
        
        public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ) {
            for( int i = lastIndex; i >= firstIndex; i-- )
                remove( i );
        }
        
        public void actionDisabledIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                update();
        }

        public void actionEnabledChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                button.setEnabled( action.isEnabled( dockable ));
        }

        public void actionIconChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                update();
        }

        public void actionTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                update();
        }

        public void actionTooltipTextChanged( StandardDockAction action, Set<Dockable> dockables ) {
            if( dockables.contains( dockable ))
                update();
        }
    }
}
