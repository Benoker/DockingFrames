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
package bibliothek.extension.gui.dock.theme.eclipse.stack;

import java.awt.Component;

import bibliothek.extension.gui.dock.theme.eclipse.RoundRectButton;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.stack.menu.AbstractCombinedMenu;
import bibliothek.gui.dock.themes.basic.action.BasicTrigger;

/**
 * A menu used by the {@link EclipseTabPane} to select {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class EclipseMenu extends AbstractCombinedMenu{
	private EclipseTabPane pane;
	
	/**
	 * Creates a new menu.
	 * @param pane the owner of this menu
	 */
	public EclipseMenu( EclipseTabPane pane ){
		super( pane, pane.getMenuVisibilityHandler() );
		this.pane = pane;
	}
	
	@Override
	protected Component createComponent(){
		BasicTrigger trigger = new BasicTrigger(){
        	public void triggered(){
        		open();
        	}
        };
        RoundRectButton button = new RoundRectButton( trigger );
        //button.getModel().setIcon( icon );
        
        return button;
	}

	@Override
	protected void selected( Dockable dockable ){
		pane.setSelectedDockable( dockable );		
	}
}
