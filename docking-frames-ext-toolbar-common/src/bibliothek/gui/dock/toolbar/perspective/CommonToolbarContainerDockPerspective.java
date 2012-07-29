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
package bibliothek.gui.dock.toolbar.perspective;

import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.common.perspective.CElementPerspective;
import bibliothek.gui.dock.common.perspective.CommonDockStationPerspective;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockPerspective;

/**
 * The type of object that is used by {@link CToolbarAreaPerspective} as intern representation.
 * @author Benjamin Sigg
 */
public class CommonToolbarContainerDockPerspective extends ToolbarContainerDockPerspective implements CommonDockStationPerspective{
	private CToolbarAreaPerspective perspective;
	
	/**
	 * Creates the new perspective
	 * @param perspective the common part
	 */
	public CommonToolbarContainerDockPerspective( CToolbarAreaPerspective perspective ){
		this.perspective = perspective;
	}
	
	@Override
	public CElementPerspective getElement(){
		return perspective;
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
