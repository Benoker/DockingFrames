/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen.layer;

import java.awt.Component;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.station.layer.LayerPriority;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;

/**
 * Describes a layer where one {@link ScreenDockWindow} resides.
 * @author Benjamin Sigg
 */
public class ScreenWindowLayer extends DefaultDropLayer{
	private ScreenDockWindow window;
	
	public ScreenWindowLayer( ScreenDockStation station, ScreenDockWindow window ){
		super( station );
		this.window = window;
		setPriority( LayerPriority.FLOAT_FREE );
	}
	
	public boolean contains( int x, int y ){
		return window.contains( x, y );
	}
	
	@Override
	public Component getComponent(){
		return window.getDockableDisplayer().getComponent();
	}
	
	@Override
	public DockStationDropLayer modify( DockStationDropLayer child ){
		child.setPriority( getPriority().merge( child.getPriority() ));
		return child;
	}
}
