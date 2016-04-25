/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.action.panel;

import javax.swing.JComponent;

import bibliothek.extension.gui.dock.theme.bubble.RoundButton;
import bibliothek.extension.gui.dock.theme.bubble.RoundButtonViewItem;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.view.ActionViewConverter;
import bibliothek.gui.dock.action.view.ViewGenerator;
import bibliothek.gui.dock.common.action.CPanelPopup;
import bibliothek.gui.dock.common.action.CPanelPopup.PanelPopup;
import bibliothek.gui.dock.themes.basic.action.BasicTitleViewItem;

/**
 * Basic handler for creating a button for a {@link CPanelPopup}.
 * @author Benjamin Sigg
 */
public class BubblePanelPopupGenerator implements ViewGenerator<PanelPopup, BasicTitleViewItem<JComponent>>{
	public BasicTitleViewItem<JComponent> create( ActionViewConverter converter, PanelPopup action, Dockable dockable ){
		BasicPanelPopupHandler handler = new BasicPanelPopupHandler( action, dockable );
		RoundButton button = new RoundButton( handler, handler, dockable, action );
		handler.setModel( button.getModel() );
		
		return new RoundButtonViewItem( dockable, handler, button );
	}
}
