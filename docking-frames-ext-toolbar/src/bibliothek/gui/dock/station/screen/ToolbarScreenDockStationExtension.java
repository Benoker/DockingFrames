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

package bibliothek.gui.dock.station.screen;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;

/**
 * Modifies the behavior of {@link ScreenDockStation} such that dropping a toolbar item results in
 * the creation of additional, new {@link DockStation}s.
 * @author Benjamin Sigg
 */
public class ToolbarScreenDockStationExtension implements ScreenDockStationExtension {
	private DockController controller;
	
	private Dockable pending;
	
	public ToolbarScreenDockStationExtension( DockController controller ){
		this.controller = controller;
	}
	
	@Override
	public boolean canReplace( ScreenDockStation station, Dockable old, Dockable next ) {
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		return strategy.isToolbarGroupPartParent( station, next, true );
	}

	@Override
	public void drop( ScreenDockStation station, DropArguments arguments ){
		if( arguments.getWindow() == null ){
			ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
			Dockable dockable = arguments.getDockable();
			
			if( strategy.isToolbarPart( dockable ) ){
				Dockable replacement = strategy.ensureToolbarLayer( station, dockable );
				if( replacement != dockable ){					
					pending = dockable;
				}
				else{
					pending = null;
				}
				arguments.setDockable( replacement );
			}
		}
		else{
			pending = null;
		}
	}

	@Override
	public void dropped( ScreenDockStation station, DropArguments arguments, boolean successfull ){
		if( pending != null && successfull ){
			DockStation child = arguments.getDockable().asDockStation();
			DockableProperty successor = arguments.getProperty().getSuccessor();
			if( successor == null || !child.drop( pending, successor )){
				child.drop( pending );
			}
		}
		pending = null;
	}
}
