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

import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionType;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A connection between a {@link SelectableDockAction} and a {@link MiniButton}.
 * The handler ensures that the selected-state of the action and of the button
 * are always the same.
 * @author Benjamin Sigg
 *
 */
public abstract class SelectableMiniButtonHandler extends AbstractMiniButtonHandler<SelectableDockAction, MiniButton> {
	/**
	 * A class handling a {@link SelectableDockAction} with a behavior
	 * of {@link ActionType#RADIO}: the action can only be selected, not deselected
	 * by this handler.
	 * @author Benjamin Sigg
	 */
	public static class Radio extends SelectableMiniButtonHandler{
		/**
		 * Creates a new radio-handler
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 * @param button the button to manage
		 */
		public Radio( SelectableDockAction action, Dockable dockable, MiniButton button ){
			super( action, dockable, button );
		}

		public void triggered(){
			getAction().setSelected( getDockable(), true );
		}
	};
	
	/**
	 * A class handling a {@link SelectableDockAction} with a behavior of
	 * {@link ActionType#CHECK}: the action can be selected and deselected
	 * by this handler.
	 * @author Benjamin Sigg
	 */
	public static class Check extends SelectableMiniButtonHandler{
		/**
		 * Creates a new check-handler.
		 * @param action the action to handle
		 * @param dockable the owner of the action
		 * @param button the button to manage
		 */
		public Check( SelectableDockAction action, Dockable dockable, MiniButton button ){
			super( action, dockable, button );
		}

		public void triggered(){
			boolean current = getAction().isSelected( getDockable() );
			getAction().setSelected( getDockable(), !current );
		}
	}
	
	/** A listener added to the action of this handler */
	private SelectableDockActionListener listener = new SelectableDockActionListener(){
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			if( dockables.contains( getDockable() )){
				boolean value = action.isSelected( getDockable() );
				getButton().setSelected( value );
			}
		}
	};
	
	/**
	 * Creates a new handler.
	 * @param action the action to handle
	 * @param dockable the owner of the action
	 * @param button the button to manage
	 */
	public SelectableMiniButtonHandler( SelectableDockAction action, Dockable dockable, MiniButton button ){
		super( action, dockable, button );
	}
	
	@Override
	public void bind(){
		super.bind();
		getAction().addSelectableListener( listener );
		getButton().setSelected( getAction().isSelected( getDockable() ) );
	}

	@Override
	public void unbind(){
		super.unbind();
		getAction().removeSelectableListener( listener );
	}
}
