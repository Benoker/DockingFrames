package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.action.dropdown.DefaultDropDownFilter;
import bibliothek.gui.dock.action.dropdown.DropDownFilterFactory;
import bibliothek.gui.dock.action.views.ActionViewConverter;
import bibliothek.gui.dock.action.views.ViewTarget;
import bibliothek.gui.dock.event.DropDownActionListener;

/**
 * A dropdown action that has the same properties for all Dockables.
 * @author Benjamin Sigg
 */
public class SimpleDropDownAction extends SimpleDockAction implements DropDownAction {
	/** the currently selected action */
	private DockAction selection;

	/** the listeners that were added to this action */
	private List<DropDownActionListener> listeners = 
		new ArrayList<DropDownActionListener>();
	
	/** the menu */
	private DefaultDockActionSource actions = new DefaultDockActionSource();
	
	/** the factory used to create new filter */
	private DropDownFilterFactory filter = DefaultDropDownFilter.FACTORY;
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.DROP_DOWN, this, target, dockable );
	}
	
	public DockAction getSelection( Dockable dockable ){
		return selection;
	}
	
	public void setSelection( Dockable dockable, DockAction selection ){
		if( this.selection != selection ){
			this.selection = selection;
			fireSelectionChanged();
		}
	}
	
	/**
	 * Sets the filter that will be used to filter text, icon, tooltips, etc.
	 * if a view has to decide, which elements of this action, or its selected
	 * action have to be shown.
	 * @param filter the filter, not <code>null</code>
	 */
	public void setFilter( DropDownFilterFactory filter ){
		if( filter == null )
			throw new IllegalArgumentException( "Filter must not be null" );
		this.filter = filter;
	}
	
	public DropDownFilterFactory getFilter( Dockable dockable ){
		return filter;
	}
	
	/**
	 * Adds an action to the menu.
	 * @param action the action to add
	 */
	public void add( DockAction action ){
		actions.add( action );
	}
	
	/**
	 * Inserts an action into the menu.
	 * @param index the location of the action
	 * @param action the new action
	 */
	public void insert( int index, DockAction action ){
		actions.add( index, action );
	}
	
	/**
	 * Inserts a list of actions into the menu.
	 * @param index the location of the first action
	 * @param action the actions to add
	 */
	public void insert( int index, DockAction... action ){
		actions.add( index, action );
	}
	
	/**
	 * Removes an action from the menu.
	 * @param index the location of the action
	 */
	public void remove( int index ){
		DockAction action = actions.getDockAction( index );
		actions.remove( index );
		
		if( selection == action )
			setSelection( (Dockable)null, (StandardDockAction)null );		
	}
	
	/**
	 * Removes <code>action</code> from the menu.
	 * @param action the action to remove
	 */
	public void remove( DockAction action ){
		actions.remove( action );
		
		if( selection == action )
			setSelection( (Dockable)null, (StandardDockAction)null );
	}
	
	public DefaultDockActionSource getSubActions( Dockable dockable ){
		return actions;
	}
		
	public void addDropDownActionListener( DropDownActionListener listener ){
		listeners.add( listener );
	}
	
	public void removeDropDownActionListener( DropDownActionListener listener ){
		listeners.remove( listener );
	}

	/**
	 * Gets an array of all listeners that are registered to this action.
	 * @return the array of listeners
	 */
	@SuppressWarnings( "unchecked" )
	protected DropDownActionListener[] getListeners(){
		return listeners.toArray( new DropDownActionListener[ listeners.size() ] );
	}
	
	/**
	 * Informs all listeners that the selection has changed.
	 */
	protected void fireSelectionChanged(){
		Set<Dockable> dockables = getBindeds();
		for( DropDownActionListener listener : getListeners() )
			listener.selectionChanged( this, dockables, selection );
	}
}
