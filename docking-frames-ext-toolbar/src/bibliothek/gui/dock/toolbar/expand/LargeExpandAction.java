/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Hervé Guillaume, Benjamin Sigg
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
 * Hervé Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.GroupKeyGenerator;

/**
 * An {@link AbstractGroupedExpandAction} used for items that can switch between all
 * {@link ExpandedState}s. This action makes items as large as possible.
 * 
 * @author Benjamin Sigg
 */
public class LargeExpandAction extends AbstractGroupedExpandAction{
	public LargeExpandAction( DockController controller ){
		super(controller, Action.LARGER, Action.LARGEST, Action.SMALLER);

		setGenerator(new GroupKeyGenerator<Action>(){
			@Override
			public Action generateKey( Dockable dockable ){
				switch (getStrategy().getState(dockable)) {
				case EXPANDED:
					return Action.SMALLER;
				case SHRUNK:
					return Action.LARGEST;
				case STRETCHED:
					return Action.LARGER;
				default:
					return null;
				}
			}
		});
	}

	@Override
	public void action( Dockable dockable ){
		switch (getStrategy().getState(dockable)) {
		case EXPANDED:
			getStrategy().setState(dockable, ExpandedState.STRETCHED);
			return;
		default:
			getStrategy().setState(dockable, ExpandedState.EXPANDED);
			return;
		}
	}
}
