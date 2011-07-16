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
package bibliothek.gui.dock.action;

import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * Represents a text that is used by a {@link DockAction}.
 * @author Benjamin Sigg
 */
public abstract class DockActionText extends TextValue{
	/** the kind of value this {@link UIValue} is */
	public static final Path KIND_DOCK_ACTION = KIND_TEXT.append( "action" );

	/** the action using this icon */
	private DockAction action;
	
	/**
	 * Creates a new {@link DockActionText}.
	 * @param id the unique identifier of the text
	 * @param action the action using the text
	 */
	public DockActionText( String id, DockAction action ){
		this( id, action, KIND_DOCK_ACTION );
	}
	
	/**
	 * Creates a new {@link DockActionText}.
	 * @param id the unique identifier of the text
	 * @param action the action using the text
	 * @param kind what kind of {@link UIValue} this is
	 */
	public DockActionText( String id, DockAction action, Path kind ){
		super( id, kind );
		this.action = action;
	}
	
	/**
	 * Gets the action which is using the icon.
	 * @return the action
	 */
	public DockAction getAction(){
		return action;
	}
}
