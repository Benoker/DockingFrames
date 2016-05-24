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

package bibliothek.gui.dock.action;

import bibliothek.gui.dock.action.actions.SeparatorAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;

/**
 * The ActionType manly defines, how a {@link DockAction} is to be used. The
 * ActionType is needed by the {@link ActionViewConverter} to create a view
 * for a particular {@link DockAction}. Client code may create new ActionTypes,
 * but must ensure that the {@link ActionViewConverter} knows these new types.
 * @param <D> the specialized type of {@link DockAction} that uses this type
 * @author Benjamin Sigg
 */
public class ActionType<D extends DockAction> {
    /** 
     * The action behaves like a button: it can be triggered, some
     * action happens, and the original state is reestablished.
     */
    public static final ActionType<ButtonDockAction> BUTTON =
    	new ActionType<ButtonDockAction>( "action type BUTTON" );
    /**
     * The action behaves like a checkbox: when it is triggered,
     * it changes it's state from selected to unselected, or vice versa.
     */
    public static final ActionType<SelectableDockAction> CHECK =
    	new ActionType<SelectableDockAction>( "action type CHECK" ); 
    /**
     * The action behaves like a radiobutton: when it is triggered,
     * it changes to the selected-state, but some other actions
     * may change to the unselected-state 
     */
    public static final ActionType<SelectableDockAction> RADIO =
    	new ActionType<SelectableDockAction>( "action type RADIO" );
    
    /**
     * The action is a group of other actions which are shown as soon
     * as someone triggers the action.
     */
    public static final ActionType<MenuDockAction> MENU =
    	new ActionType<MenuDockAction>( "action type MENU" );
	
    /**
     * Represents a separator.
     */
    public static final ActionType<SeparatorAction> SEPARATOR =
    	new ActionType<SeparatorAction>( "action type SEPARATOR" );
    
    /**
     * Represents a drop down action.
     */
    public static final ActionType<DropDownAction> DROP_DOWN =
    	new ActionType<DropDownAction>( "action type DROP DOWN" );
    
    /**
     * Internal identifier for this type
     */
	private String id;
	
	/**
	 * Creates a new ActionType.
	 * @param id a unique identifier
	 */
	public ActionType( String id ){
		if( id == null )
			throw new IllegalArgumentException( "Id must not be null" );
		this.id = id;
	}
	
	@Override
	public String toString(){
		return id;
	}

	@Override
	public int hashCode(){
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ){
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if( this.getClass() == obj.getClass() ) {
			return ((ActionType<?>)obj).id.equals( id );
		}

		return false;
		
	}
}
