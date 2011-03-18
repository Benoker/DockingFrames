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

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.SwingUtilities;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * An implementation of {@link FlapDockStation} that also satisfies the
 * {@link CommonDockStation} interface.
 * @author Benjamin Sigg
 */
public class CFlapDockStation extends FlapDockStation implements CommonDockStation<FlapDockStation, CFlapDockStation>{
	private CommonStationDelegate<FlapDockStation> delegate;
	private Component expansion;
	
	/**
	 * Creates a new station.
	 * @param expansion the {@link Component} which should define the size and location of
	 * this station, can be <code>null</code>
	 * @param delegate offers additional methods required by this station
	 */
	public CFlapDockStation( Component expansion, CommonStationDelegate<FlapDockStation> delegate ){
		this.expansion = expansion;
		this.delegate = delegate;
	}
	
	public CDockable getDockable(){
		return delegate.getDockable();
	}

	public DockActionSource[] getSources(){
		return delegate.getSources();
	}

	public CStation<FlapDockStation> getStation(){
		return delegate.getStation();
	}
	
	public FlapDockStation getDockStation(){
		return this;
	}
	
	@Override
	public CFlapDockStation asDockStation(){
		return this;
	}
	
	@Override
	public CommonDockable asDockable(){
		return null;
	}
	
	@Override
	public Rectangle getExpansionBounds() {
		if( expansion == null ){
			return super.getExpansionBounds();
		}
		Point point = new Point( 0, 0 );
		point = SwingUtilities.convertPoint( this.getComponent(), point, expansion );
		return new Rectangle( -point.x, -point.y, expansion.getWidth(), expansion.getHeight() );
	}
	
	@Override
	public void requestDockTitle( DockTitleRequest request ){
		if( delegate.isTitleDisplayed( request.getVersion() )){
			super.requestDockTitle( request );
		}
		else{
			request.answer( null );
		}
	}
}
