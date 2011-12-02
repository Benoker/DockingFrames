/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.util.text;

import javax.swing.Action;

import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A text that is used by an {@link Action}.
 * @author Benjamin Sigg
 */
public class SwingActionText extends TextValue{
	/** the kind of value this {@link UIValue} is */
	public static final Path KIND_SWING_ACTION = KIND_TEXT.append( "swing_action" );

	/** the action using this icon */
	private Action action;
	
	/** The key to be used for {@link Action#putValue(String, Object)} */
	private String key;
	
	/**
	 * Creates a new {@link DockActionIcon}.
	 * @param id the unique identifier of the icon
	 * @param key the key to be used for {@link Action#putValue(String, Object)}
	 * @param action the action using the icon
	 */
	public SwingActionText( String id, String key, Action action ){
		this( id, key, action, KIND_SWING_ACTION );
	}
	
	/**
	 * Creates a new {@link DockActionIcon}.
	 * @param id the unique identifier of the icon
	 * @param action the action using the icon
	 * @param key the key to be used for {@link Action#putValue(String, Object)}
	 * @param kind what kind of {@link UIValue} this is
	 */
	public SwingActionText( String id, String key, Action action, Path kind ){
		super( id, kind );
		this.key = key;
		this.action = action;
	}
	
	/**
	 * Gets the action which is using the icon.
	 * @return the action
	 */
	public Action getAction(){
		return action;
	}
	
	@Override
	protected void changed( String oldValue, String newValue ){
		action.putValue( key, newValue );
	}
}
