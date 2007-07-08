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

package bibliothek.gui.dock.action.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.*;
import bibliothek.gui.dock.action.dropdown.DefaultDropDownFilter;
import bibliothek.gui.dock.action.dropdown.DropDownFilterFactory;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
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
		setSelection( selection );
	}
	
	/**
	 * Changes the selection of this drop-down-action.
	 * @param selection the newly selected action
	 */
	public void setSelection( DockAction selection ){
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
	 * Gets the number of actions shown in the menu.
	 * @return the number of actions
	 */
	public int size(){
		return actions.getDockActionCount();
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
