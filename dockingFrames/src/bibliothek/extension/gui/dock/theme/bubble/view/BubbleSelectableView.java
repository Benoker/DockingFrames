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
package bibliothek.extension.gui.dock.theme.bubble.view;

import java.util.Set;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.SelectableDockAction;
import bibliothek.gui.dock.event.SelectableDockActionListener;

/**
 * A view to handle check- and radio-actions.
 * @author Benjamin Sigg
 */
public abstract class BubbleSelectableView extends AbstractBubbleView {
	/** the action shown in this view */
	protected SelectableDockAction action;
	/** a listener changing the view if the action changes */
	private Listener listener = new Listener();
	
	/**
	 * Creates a new handler.
	 * @param theme the theme to read colors from
	 * @param action the action to show
	 * @param dockable the Dockable for which the action is shown
	 */
	public BubbleSelectableView( BubbleTheme theme, SelectableDockAction action, Dockable dockable ){
		super( theme, action, dockable );
		this.action = action;
	}
	
	@Override
	public void bind(){
		super.bind();
		action.addSelectableListener( listener );
		getButton().setSelected( action.isSelected( getDockable() ) );
	}
	
	@Override
	public void unbind(){
		super.unbind();
		action.removeSelectableListener( listener );
	}
	
	/**
	 * A listener changing the selected-state of the view whenever the 
	 * action changes the state.
	 * @author Benjamin Sigg
	 */
	private class Listener implements SelectableDockActionListener{
		public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
			Dockable dockable = getDockable();
			if( dockables.contains( dockable ))
				getButton().setSelected( action.isSelected( dockable ) );
		}
	}

	/**
	 * A handle for radio-actions.
	 * @author Benjamin Sigg
	 */
	public static class Radio extends BubbleSelectableView{
		/**
		 * Creates a new handler.
		 * @param theme the theme to read colors from
		 * @param action the action to show
		 * @param dockable the Dockable for which the action is shown
		 */
		public Radio( BubbleTheme theme, SelectableDockAction action, Dockable dockable ){
			super( theme, action, dockable );
		}

		@Override
		protected void triggered( Dockable dockable ){
			action.setSelected( dockable, true );
		}
	}
	
	/**
	 * A handle for check-actions.
	 * @author Benjamin Sigg
	 */
	public static class Check extends BubbleSelectableView{
		/**
		 * Creates a new handler.
		 * @param theme the theme to read colors from
		 * @param action the action to show
		 * @param dockable the Dockable for which the action is shown
		 */
		public Check( BubbleTheme theme, SelectableDockAction action, Dockable dockable ){
			super( theme, action, dockable );
		}

		@Override
		protected void triggered( Dockable dockable ){
			action.setSelected( dockable, !action.isSelected( dockable ) );
		}
	}
}
