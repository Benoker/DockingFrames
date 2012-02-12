/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.themes.font;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.util.font.AbstractDockFont;
import bibliothek.gui.dock.util.font.FontModifier;
import bibliothek.util.Path;

/**
 * A font that is used by a button showing a {@link DockAction}.
 * @author Benjamin Sigg
 */
public abstract class ButtonFont extends AbstractDockFont{
	/** this kind describes a font that is used by a button */
	public static final Path KIND_BUTTON_FONT = KIND_DOCK_FONT.append( "button" );
	
	/** the action which is shown on the button */
	private DockAction action;
	
	/** the dockable for which the font is used */
	private Dockable dockable;

	/**
	 * Creates a new font
	 * @param id the unique id of this font
	 * @param dockable the element which is associated with <code>action</code>
	 * @param action the action for which a button is shown
	 * @param backup the backup value used of no other value is available
	 */
	public ButtonFont( String id, Dockable dockable, DockAction action, FontModifier backup ){
		this( id, dockable, action, KIND_BUTTON_FONT, backup );
	}
	
	/**
	 * Creates a new font
	 * @param id the unique id of this font
	 * @param dockable the element which is associated with <code>action</code>
	 * @param action the action for which a button is shown
	 * @param kind what kind of font this is
	 * @param backup the backup value used of no other value is available
	 */
	public ButtonFont( String id, Dockable dockable, DockAction action, Path kind, FontModifier backup ){
		super( id, kind, backup );
		this.dockable = dockable;
		this.action = action;
	}
	
	/**
	 * Gets the action for which this font is used.
	 * @return the action, may be <code>null</code>
	 */
	public DockAction getAction(){
		return action;
	}
	
	/**
	 * Gets the dockable for with which {@link #getAction() the action} is associated. 
	 * @return the dockable, may be <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}
}
