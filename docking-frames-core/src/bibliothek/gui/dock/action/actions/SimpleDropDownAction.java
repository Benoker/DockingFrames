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

package bibliothek.gui.dock.action.actions;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.DropDownAction;
import bibliothek.gui.dock.action.StandardDockAction;
import bibliothek.gui.dock.disable.DisablingStrategy;

/**
 * A dropdown action that has the same properties for all Dockables.
 * @author Benjamin Sigg
 */
public class SimpleDropDownAction extends AbstractSimpleDropDownAction implements DropDownAction, SharingDropDownDockAction {
	/** the menu */
	private DefaultDockActionSource actions = new DefaultDockActionSource();

	/**
	 * Creates a new action
	 */
	public SimpleDropDownAction(){
		this( true );
	}
	
	/**
	 * Creates a new action
	 * @param monitorDisabling whether the current {@link DisablingStrategy} should be monitored
	 */
	public SimpleDropDownAction( boolean monitorDisabling ){
		super( monitorDisabling );
	}
	
	public void add( DockAction action ){
		actions.add( action );
	}
	
	public void insert( int index, DockAction action ){
		actions.add( index, action );
	}
	
	public void insert( int index, DockAction... action ){
		actions.add( index, action );
	}
	
	public void remove( int index ){
		DockAction action = actions.getDockAction( index );
		actions.remove( index );
		
		if( getSelection() == action ){
			setSelection( (StandardDockAction)null );
		}
	}
	
	/**
	 * Gets the <code>index</code>'th action of this menu.
	 * @param index the index of an action
	 * @return the action at <code>index</code>
	 */
	public DockAction getDockAction( int index ){
		return actions.getDockAction( index );
	}
	
	public int size(){
		return actions.getDockActionCount();
	}
	
	public void remove( DockAction action ){
		actions.remove( action );
		
		if( getSelection() == action )
			setSelection( (Dockable)null, (StandardDockAction)null );
	}
	
	protected DockActionSource getSubActions(){
		return actions;
	}
	
	public DefaultDockActionSource getSubActions( Dockable dockable ){
		return (DefaultDockActionSource)super.getSubActions( dockable );
	}
}
