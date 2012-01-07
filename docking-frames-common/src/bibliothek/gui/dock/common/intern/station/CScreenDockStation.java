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
package bibliothek.gui.dock.common.intern.station;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.util.WindowProvider;

/**
 * A {@link ScreenDockStation} that also implements {@link CommonDockStation}.
 * @author Benjamin Sigg
 */
public class CScreenDockStation extends ScreenDockStation implements CommonDockStation<ScreenDockStation,CScreenDockStation>{
	private CommonStationDelegate<CScreenDockStation> delegate;
	
	/**
	 * Creates a new station.
	 * @param owner the window that is the owner of the windows of this station
	 * @param delegate additional methods required for this station
	 */
	public CScreenDockStation( WindowProvider owner, CommonStationDelegate<CScreenDockStation> delegate ){
		super( owner );
		this.delegate = delegate;
		setExpandOnDoubleClick( false );
	}
	
	@Override
	public String getFactoryID(){
		return CommonDockStationFactory.FACTORY_ID;
	}
	
	public String getConverterID(){
		return super.getFactoryID();
	}

	@Override
	protected ListeningDockAction createFullscreenAction(){
		return null;
	}

	public ScreenDockStation getDockStation(){
		return this;
	}
	
	@Override
	public CScreenDockStation asDockStation(){
		return this;
	}

	@Override
	public CommonDockable asDockable(){
		return null;
	}
	
	public CStation<CScreenDockStation> getStation(){
		return delegate.getStation();
	}
}
