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
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.ButtonDockAction;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewTarget;

/**
 * A {@link GroupedDockAction} that provides the functionality of
 * a {@link ButtonDockAction}.
 * @author Benjamin Sigg
 * @param <K> the type of key used to distinguish groups
 */
public abstract class GroupedButtonDockAction<K> extends GroupedDropDownItemAction<K, SimpleButtonAction> implements ButtonDockAction{
	/**
	 * Creates a new action.
	 * @param generator the generator creating keys for {@link Dockable Dockables}
	 * which are not yet in a group 
	 */
	public GroupedButtonDockAction( GroupKeyGenerator<? extends K> generator ){
		super( generator );
	}

	@Override
	protected SimpleButtonAction createGroup( K key ){
		return new SimpleButtonAction( false ){
			@Override
			public void action( Dockable dockable ){
				super.action( dockable );
				GroupedButtonDockAction.this.action( dockable );
			}
		};
	}
	
	public <V> V createView( ViewTarget<V> target, ActionViewConverter converter, Dockable dockable ){
		return converter.createView( ActionType.BUTTON, this, target, dockable );
	}
}
