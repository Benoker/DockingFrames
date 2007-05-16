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

package bibliothek.gui.dock.action.views.buttons;

import javax.swing.JComponent;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.StandardDockAction;

/**
 * A connection between a {@link StandardDockAction} and a {@link MiniButton}.
 * @author Benjamin Sigg
 *
 * @param <D> the type of action supported by this handler
 * @param <T> the type of button supported by this handler
 */
public interface MiniButtonHandler<D extends DockAction, T extends MiniButton> extends TitleViewItem<JComponent> {
	/**
	 * Called by <code>button</code> when the mouse is released.
	 */
	public abstract void triggered();
	
	/**
	 * Gets the button which shows the contents of this model.
	 * @return the button
	 */
	public T getButton();
	
	/**
	 * Gets the Dockable which owns the action of this model.
	 * @return the owner of {@link #getAction() the action}
	 */
	public Dockable getDockable();
	
	/**
	 * Gets the action that is observed by this model.
	 * @return the action
	 */
	public D getAction();
}
