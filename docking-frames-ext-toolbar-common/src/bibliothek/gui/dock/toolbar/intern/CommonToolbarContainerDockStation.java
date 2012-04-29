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

package bibliothek.gui.dock.toolbar.intern;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.toolbar.CToolbarArea;

/**
 * A {@link ToolbarContainerDockStation} used as root station.
 * @author Benjamin Sigg
 */
public class CommonToolbarContainerDockStation extends ToolbarContainerDockStation implements CommonDockStation<ToolbarContainerDockStation, CommonToolbarContainerDockStation>{
	private CToolbarArea container;
	
	public CommonToolbarContainerDockStation( CToolbarArea container, Orientation orientation ){
		super( orientation );
		this.container = container;
	}
	
	@Override
	public CommonToolbarContainerDockStation asDockStation(){
		return this;
	}
	
	@Override
	public CommonDockable asDockable(){
		return null;
	}
	
	@Override
	public CommonToolbarContainerDockStation getDockStation(){
		return this;
	}

	@Override
	public CStation<CommonToolbarContainerDockStation> getStation(){
		return container;
	}

	@Override
	public String getFactoryID(){
		return CommonDockStationFactory.FACTORY_ID;
	}
	
	@Override
	public String getConverterID(){
		return super.getFactoryID();
	}
}
