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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab;

import java.awt.Component;
import java.awt.Container;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.focus.SimplifiedFocusTraversalPolicy;
import bibliothek.gui.dock.themes.basic.action.buttons.ButtonPanel;

/**
 * Focus traversal policy used by a {@link BaseTabComponent} to go through its child {@link Component}
 * which shows the buttons (if there are any).
 * @author Benjamin Sigg
 */
public class BaseTabFocusTraversalPolicy implements SimplifiedFocusTraversalPolicy{
	private ButtonPanel buttons;
	
	public BaseTabFocusTraversalPolicy( ButtonPanel buttons ){
		this.buttons = buttons;
	}
	
	public Component getAfter( Container container, Component component ){
		if( component == buttons || SwingUtilities.isDescendingFrom( component, buttons )){
			return null;
		}
		return buttons;
	}

	public Component getBefore( Container container, Component component ){
		if( component == buttons || SwingUtilities.isDescendingFrom( component, buttons )){
			return null;
		}
		return buttons;
	}

	public Component getFirst( Container container ){
		return buttons;
	}

	public Component getLast( Container container ){
		return buttons;
	}

	public Component getDefault( Container container ){
		return getFirst( container );
	}
}
