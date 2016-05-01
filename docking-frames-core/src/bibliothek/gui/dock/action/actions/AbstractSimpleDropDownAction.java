/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.dropdown.DefaultDropDownFilter;
import bibliothek.gui.dock.action.dropdown.DropDownFilterFactory;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;
import bibliothek.gui.dock.disable.DisablingStrategy;
import bibliothek.gui.dock.event.DropDownActionListener;

/**
 * A dropdown action based on one {@link DockActionSource} which has to be specified
 * by a subclass.
 * @author Benjamin Sigg
 */
public abstract class AbstractSimpleDropDownAction extends SimpleDockAction implements DropDownAction {
	/** the currently selected action */
	private DockAction selection;

	/** the listeners that were added to this action */
	private List<DropDownActionListener> listeners = 
		new ArrayList<DropDownActionListener>();
	
	/** the factory used to create new filter */
	private DropDownFilterFactory filter = DefaultDropDownFilter.FACTORY;
	
	/**
	 * Creates a new action
	 * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
	 */
	public AbstractSimpleDropDownAction( boolean monitorDisabling ){
		super( monitorDisabling );
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.DROP_DOWN, this, target, dockable );
	}
	
	public DockAction getSelection( Dockable dockable ){
		return selection;
	}
	
	/**
	 * Returns the currently selected action.
	 * @return the selected action or <code>null</code>
	 */
	public DockAction getSelection(){
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
	 * Gets the {@link DockActionSource} which contains all the actions of this dropdown menu.
	 * @return all the actions, not <code>null</code>
	 */
	protected abstract DockActionSource getSubActions();
	
	public DockActionSource getSubActions( Dockable dockable ){
		return getSubActions();
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
	protected DropDownActionListener[] getListeners(){
		return listeners.toArray( new DropDownActionListener[ listeners.size() ] );
	}
	
	/**
	 * Informs all listeners that the selection has changed.
	 */
	protected void fireSelectionChanged(){
		Set<Dockable> dockables = getBoundDockables();
		for( DropDownActionListener listener : getListeners() )
			listener.selectionChanged( this, dockables, selection );
	}
	
	public boolean trigger( Dockable dockable ) {
	    if( !isEnabled( dockable ))
            return false;
        
        if( selection != null )
            return selection.trigger( dockable );
        
        return false;
	}
}
