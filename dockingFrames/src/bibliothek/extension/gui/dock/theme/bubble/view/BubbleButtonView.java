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

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ButtonDockAction;

/**
 * A view for a {@link ButtonDockAction}.
 * @author Benjamin Sigg
 */
public class BubbleButtonView extends AbstractBubbleView{
	/** the action shown by this view */
	private ButtonDockAction action;
	
	/**
	 * Creates a new view.
	 * @param theme the theme to read colors from
	 * @param action the action to show
	 * @param dockable the Dockable for which the action is shown
	 */
	public BubbleButtonView(BubbleTheme theme, ButtonDockAction action, Dockable dockable){
		super( theme, action, dockable );
		this.action= action;
	}
	
	@Override
	protected void triggered( Dockable dockable ){
		action.action( dockable );
	}
}
