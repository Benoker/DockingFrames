/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.common.action;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.predefined.CCloseAction;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * This factory is responsible for creating a {@link CAction} that is shown on a {@link CDockable}
 * which is {@link CDockable#isCloseable()}. The action should call {@link CDockable#setVisible(boolean)}
 * when invoked (to close the dockable).
 * @author Benjamin Sigg
 */
public interface CloseActionFactory {
	/**
	 * A default implementation of {@link CloseActionFactory}, always returns a new
	 * {@link CCloseAction}.
	 */
	public static final CloseActionFactory DEFAULT = new CloseActionFactory(){
		public CAction create( CControl control, CDockable dockable ){
			return new CCloseAction( control );
		}
	};
	
	/**
	 * Creates a new action.
	 * @param control the control in whose realm the action is used
	 * @param dockable the item which is going to show the action
	 * @return the action, must not be <code>null</code>, but may be an action that is already used
	 * at other places
	 */
	public CAction create( CControl control, CDockable dockable );
}
