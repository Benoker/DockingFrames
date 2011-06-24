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

import java.util.HashSet;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;

/**
 * A separator represents a space between actions. A separator has no state,
 * he is purely graphical.
 * @author Benjamin Sigg
 */
public class SeparatorAction implements DockAction {
	/**
	 * A separator that is visible on menus and on titles
	 */
	public static final SeparatorAction SEPARATOR = 
		new SeparatorAction( ViewTarget.MENU, ViewTarget.TITLE, ViewTarget.DROP_DOWN );
	
	/**
	 * A separator that is only visible on menus
	 */
	public static final SeparatorAction MENU_SEPARATOR =
		new SeparatorAction( ViewTarget.MENU, ViewTarget.DROP_DOWN );
	
	/**
	 * A separator which is only visible on titles
	 */
	public static final SeparatorAction TITLE_SEPARATOR = 
		new SeparatorAction( ViewTarget.TITLE );
	
	/**
	 * The targets on which this separator should be shown
	 */
	private Set<ViewTarget<?>> targets = new HashSet<ViewTarget<?>>();
	
	/**
	 * Creates a new separator.
	 * @param targets the targets on which this separator should be visible
	 */
	public SeparatorAction( ViewTarget<?>... targets ){
		for( ViewTarget<?> target : targets )
			this.targets.add( target );
	}
	
	/**
	 * Tells whether the separator should be shown or not.
	 * @param target the target on which the separator might be made visible
	 * @return <code>true</code> if the separator should be shown, <code>false</code>
	 * otherwise.
	 */
	public boolean shouldDisplay( ViewTarget<?> target ){
		return targets.contains( target );
	}
	
	public void bind( Dockable dockable ){
		// do nothing
	}

	public void unbind( Dockable dockable ){
		// do nothing
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.SEPARATOR, this, target, dockable );
	}
	
	public boolean trigger( Dockable dockable ) {
	    // can't do anything
	    return false;
	}
}
