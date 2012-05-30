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

package bibliothek.gui.dock.toolbar.measurement;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JButton;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupDropInfo;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeader;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupHeaderFactory;

public class ToolbarGroupDockStationSample implements DropSample {
	private ToolbarGroupDockStation station;
	private Dockable dockable = new ToolbarDockStation();

	public ToolbarGroupDockStationSample(){
		station = new ToolbarGroupDockStation();
		station.setOrientation( Orientation.VERTICAL );
		DockController controller = new DockController();
		controller.add( station );
		
		controller.getProperties().set( ToolbarGroupDockStation.HEADER_FACTORY, new ToolbarGroupHeaderFactory(){
			@Override
			public ToolbarGroupHeader create( ToolbarGroupDockStation station ){
				return new ToolbarGroupHeader(){
					private JButton button = new JButton( "+" );
					
					@Override
					public void setOrientation( Orientation orientation ){
						// ignore
					}
					
					@Override
					public Component getComponent(){
						return button;
					}
					
					@Override
					public void destroy(){
						
					}
				};
			}
		} );
	}

	@Override
	public ToolbarGroupDockStation getStation(){
		return station;
	}

	@Override
	public Component getComponent(){
		return station.getComponent();
	}

	@Override
	public Color dropAt( int mouseX, int mouseY ){
		StationDropItem item = new StationDropItem( mouseX, mouseY, mouseX, mouseY, dockable );
		StationDropOperation operation = station.prepareDrop( item );
		if( operation == null ) {
			return Color.BLACK;
		}
		ToolbarGroupDropInfo info = (ToolbarGroupDropInfo)operation;
		
		int column = info.getColumn();
		int line = info.getLine();
		
		if( line == -1 ){
			if( column % 2 == 0 ){
				return Color.RED;
			}
			else{
				return Color.ORANGE;
			}
		}
		else{
			if( line % 2 == 0 ){
				return Color.GREEN;
			}
			else{
				return Color.CYAN;
			}
		}
	}
}
