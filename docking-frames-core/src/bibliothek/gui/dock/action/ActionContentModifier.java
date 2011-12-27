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
package bibliothek.gui.dock.action;

/**
 * An {@link ActionContentModifier} tells what modifications may be applied to the contents
 * (e.g. the icon) of a {@link StandardDockAction}. All {@link ActionContentModifier} that are used
 * by this framework will always be declared as constant in this class. Clients are however free
 * to add additional modifiers.
 * @author Benjamin Sigg
 */
public class ActionContentModifier {
	/** no modifier at all */
	public static final ActionContentModifier NONE = new ActionContentModifier( "dock.none", true, null );
	/** the mouse is somehow hovering over the action */
	public static final ActionContentModifier NONE_HOVER = new ActionContentModifier( "dock.none.hover", true, NONE );
	/** the mouse was pressed over the action */
	public static final ActionContentModifier NONE_PRESSED = new ActionContentModifier( "dock.none.pressed", true, NONE_HOVER );
	
	/** the action is disabled */
	public static final ActionContentModifier DISABLED = new ActionContentModifier( "dock.disabled", false, NONE );
	/** the action is disabled and the mouse is hovering over the action */
	public static final ActionContentModifier DISABLED_HOVER = new ActionContentModifier( "dock.disabled.hover", false, DISABLED );
	/** the action is disabled and the mouse is pressed over the action */
	public static final ActionContentModifier DISABLED_PRESSED = new ActionContentModifier( "dock.disabled.pressed", false, DISABLED_HOVER );
	
	/** unique identifier of this modifier */
	private String id;

	/** whether this modifier describes an action that is enabled or not */
	private boolean enabled;
	
	/** backup modifier if <code>this</code> is not available */
	private ActionContentModifier backup;
	
	/**
	 * Creates a new modifier.
	 * @param id the new modifier
	 * @param enabled whether this modifier describes an action which is enabled
	 * @param backup the modifier that applies if <code>this</code> is not defined. It is the callers
	 * responsibility to ensure, that no cycle of modifiers is built. This argument can be <code>null</code>.
	 */
	public ActionContentModifier( String id, boolean enabled, ActionContentModifier backup ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		this.id = id;
		this.enabled = enabled;
		this.backup = backup;
	}
	
	/**
	 * Gets the modifier which should be used if <code>this</code> is not available.
	 * @return the modifier, can be <code>null</code>
	 */
	public ActionContentModifier getBackup(){
		return backup;
	}
	
	/**
	 * Tells whether the {@link DockAction} is supposed to be {@link StandardDockAction#isEnabled(bibliothek.gui.Dockable) enabled}
	 * if this modifier is used or not.
	 * @return whether the action is supposed to be enabled
	 */
	public boolean isEnabled(){
		return enabled;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}

	@Override
	public boolean equals( Object obj ){
		if( this == obj )
			return true;
		if( obj == null )
			return false;
		if( getClass() != obj.getClass() )
			return false;
		ActionContentModifier other = (ActionContentModifier) obj;
		return id.equals( other.id );
	}
}
