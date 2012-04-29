/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
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
 * This {@link AbstractGroupedExpandAction} is used for items that can switch only between two
 * of the {@link ExpandedState}s.
 * 
 * @author Benjamin Sigg
 */
public class SwitchExpandAction extends AbstractGroupedExpandAction{
	public SwitchExpandAction( DockController controller ){
		super(controller, Action.SMALLER, Action.SMALLEST, Action.LARGER,
				Action.LARGEST);

		setGenerator(new GroupKeyGenerator<Action>(){
			@Override
			public Action generateKey( Dockable dockable ){
				final boolean shrunk = getStrategy().isEnabled(dockable,
						ExpandedState.SHRUNK);
				final boolean expanded = getStrategy().isEnabled(dockable,
						ExpandedState.EXPANDED);

				switch (getStrategy().getState(dockable)) {
				case EXPANDED:
					if (shrunk){
						return Action.SMALLEST;
					} else{
						return Action.SMALLER;
					}
				case SHRUNK:
					if (expanded){
						return Action.LARGEST;
					} else{
						return Action.LARGER;
					}
				case STRETCHED:
					if (shrunk){
						return Action.SMALLER;
					} else{
						return Action.LARGER;
					}
				default:
					return null;
				}
			}
		});
	}

	@Override
	public void action( Dockable dockable ){
		final boolean shrunk = getStrategy().isEnabled(dockable,
				ExpandedState.SHRUNK);
		final boolean expanded = getStrategy().isEnabled(dockable,
				ExpandedState.EXPANDED);

		switch (getStrategy().getState(dockable)) {
		case EXPANDED:
			if (shrunk){
				getStrategy().setState(dockable, ExpandedState.SHRUNK);
			} else{
				getStrategy().setState(dockable, ExpandedState.STRETCHED);
			}
			break;
		case SHRUNK:
			if (expanded){
				getStrategy().setState(dockable, ExpandedState.EXPANDED);
			} else{
				getStrategy().setState(dockable, ExpandedState.STRETCHED);
			}
			break;
		case STRETCHED:
			if (shrunk){
				getStrategy().setState(dockable, ExpandedState.SHRUNK);
			} else if (expanded){
				getStrategy().setState(dockable, ExpandedState.EXPANDED);
			}
			break;
		}
	}
}
