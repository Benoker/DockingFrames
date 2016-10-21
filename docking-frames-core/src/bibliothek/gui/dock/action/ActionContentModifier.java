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
	/** no modifier at all. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier NONE = new ActionContentModifier( "dock.none", true );
	/** no modifier at all, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier NONE_VERTICAL = new ActionContentModifier( "dock.none.vertical", true, false, true, NONE );
	/** no modifier at all, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier NONE_HORIZONTAL = new ActionContentModifier( "dock.none.horizontal", true, true, false, NONE );
	
	/** the mouse is somehow hovering over the action. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier NONE_HOVER = new ActionContentModifier( "dock.none.hover", true, NONE );
	/** the mouse is somehow hovering over the action, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier NONE_HOVER_VERTICAL = new ActionContentModifier( "dock.none.hover.vertical", true, false, true, NONE_HOVER, NONE_VERTICAL );
	/** the mouse is somehow hovering over the action, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier NONE_HOVER_HORIZONTAL = new ActionContentModifier( "dock.none.hover.horizontal", true, true, false, NONE_HOVER, NONE_HORIZONTAL );
	
	/** the mouse was pressed over the action. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier NONE_PRESSED = new ActionContentModifier( "dock.none.pressed", true, NONE_HOVER );
	/** the mouse was pressed over the action, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier NONE_PRESSED_VERTICAL = new ActionContentModifier( "dock.none.pressed.vertical", true, false, true, NONE_PRESSED, NONE_VERTICAL );
	/** the mouse was pressed over the action, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier NONE_PRESSED_HORIZONTAL = new ActionContentModifier( "dock.none.pressed.horizontal", true, true, false, NONE_PRESSED, NONE_HORIZONTAL );
	
	/** the action is disabled. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier DISABLED = new ActionContentModifier( "dock.disabled", false, NONE );
	/** the action is disabled, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier DISABLED_VERTICAL = new ActionContentModifier( "dock.disabled.vertical", false, false, true, DISABLED, NONE_VERTICAL );
	/** the action is disabled, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier DISABLED_HORIZONTAL = new ActionContentModifier( "dock.disabled.horizontal", false, true, false, DISABLED, NONE_HORIZONTAL );
	
	/** the action is disabled and the mouse is hovering over the action. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier DISABLED_HOVER = new ActionContentModifier( "dock.disabled.hover", false, DISABLED );
	/** the action is disabled and the mouse is hovering over the action, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier DISABLED_HOVER_VERTICAL = new ActionContentModifier( "dock.disabled.hover.vertical", false, false, true, DISABLED_HOVER, DISABLED_VERTICAL );
	/** the action is disabled and the mouse is hovering over the action, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier DISABLED_HOVER_HORIZONTAL = new ActionContentModifier( "dock.disabled.hover.horizontal", false, true, false, DISABLED_HOVER, DISABLED_HORIZONTAL );
	
	/** the action is disabled and the mouse is pressed over the action. The framework never uses this modifier directly, instead one of vertical or horizontal versions of this modifier is used. */
	public static final ActionContentModifier DISABLED_PRESSED = new ActionContentModifier( "dock.disabled.pressed", false, DISABLED_HOVER );
	/** the action is disabled and the mouse is pressed over the action, the action is guaranteed to be shown vertically */
	public static final ActionContentModifier DISABLED_PRESSED_VERTICAL = new ActionContentModifier( "dock.disabled.pressed.vertical", false, false, true, DISABLED_PRESSED, DISABLED_VERTICAL );
	/** the action is disabled and the mouse is pressed over the action, the action is guaranteed to be shown horizontally */
	public static final ActionContentModifier DISABLED_PRESSED_HORIZONTAL = new ActionContentModifier( "dock.disabled.pressed.horizontal", false, true, false, DISABLED_PRESSED, DISABLED_HORIZONTAL );
	
	/** unique identifier of this modifier */
	private String id;

	/** whether this modifier describes an action that is enabled or not */
	private boolean enabled;
	
	/** whether this modifier describes an action that is shown horizontally */
	private boolean horizontal;
	
	/** whether this modifier describes an action that is shown vertically */
	private boolean vertical;
	
	/** backup modifiers if <code>this</code> is not available */
	private ActionContentModifier[] backup;
	
	/**
	 * Creates a new modifier.
	 * @param id the new modifier
	 * @param enabled whether this modifier describes an action which is enabled
	 * @param backup the modifier that applies if <code>this</code> is not defined. It is the callers
	 * responsibility to ensure, that no cycle of modifiers is built. This argument can be <code>null</code>.
	 */
	public ActionContentModifier( String id, boolean enabled, ActionContentModifier ... backup ){
		this( id, enabled, false, false, backup );
	}
	
	/**
	 * Creates a new modifier.
	 * @param id the new modifier
	 * @param enabled whether this modifier describes an action which is enabled
	 * @param horizontal whether the action is guaranteed to be shown horizontally
	 * @param vertical whether the action is guaranteed to be shown vertically 
	 * @param backup the modifier that applies if <code>this</code> is not defined. It is the callers
	 * responsibility to ensure, that no cycle of modifiers is built. This argument can be <code>null</code>.
	 */
	public ActionContentModifier( String id, boolean enabled, boolean horizontal, boolean vertical, ActionContentModifier ... backup ){
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		if( horizontal && vertical ){
			throw new IllegalArgumentException( "horizontal and vertical cannot be true at the same time" );
		}
		this.id = id;
		this.enabled = enabled;
		this.horizontal = horizontal;
		this.vertical = vertical;
		this.backup = backup;
	}
	
	/**
	 * Gets the modifiers which should be used if <code>this</code> is not available.
	 * @return the modifiers, must not be <code>null</code>
	 */
	public ActionContentModifier[] getBackup(){
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
	
	/**
	 * Tells whether this modifier represents an action that is guaranteed to be shown horizontally.
	 * @return <code>true</code> if the action is shown horizontally, <code>false</code> if the orientation is not known
	 * or not horizontal
	 */
	public boolean isHorizontal(){
		return horizontal;
	}

	/**
	 * Tells whether this modifier represents an action that is guaranteed to be shown vertically.
	 * @return <code>true</code> if the action is shown vertically, <code>false</code> if the orientation is not known
	 * or not vertical
	 */
	public boolean isVertical(){
		return vertical;
	}
	
	@Override
	public int hashCode(){
		return id.hashCode();
	}
	
	@Override
	public String toString(){
		return id;
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
