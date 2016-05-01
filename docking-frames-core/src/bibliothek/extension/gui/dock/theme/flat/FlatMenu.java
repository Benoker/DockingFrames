/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.flat;

import javax.swing.BorderFactory;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.menu.ButtonCombinedMenu;
import bibliothek.gui.dock.themes.basic.action.BasicButtonModel;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;
import bibliothek.gui.dock.themes.basic.action.buttons.BasicMiniButton;

/**
 * A menu that contains a list of {@link Dockable}s to select.
 * @author Benjamin Sigg
 */
public class FlatMenu extends ButtonCombinedMenu<BasicMiniButton>{
	/**
	 * Creates a new {@link FlatMenu}.
	 * @param parent the panel for which this menu is used
	 */
	public FlatMenu( FlatTabPane parent ){
		super( parent, parent.getMenuHandler() );
	}
	
	@Override
	protected BasicMiniButton createButton( BasicTrigger trigger ){
        BasicMiniButton button = new BasicMiniButton( trigger, null );
        button.setMouseOverBorder( BorderFactory.createEtchedBorder() );
        button.setNormalSelectedBorder( BorderFactory.createEtchedBorder() );
        return button;
	}
	
	@Override
	protected BasicButtonModel getModel( BasicMiniButton button ){
		return button.getModel();
	}
}
