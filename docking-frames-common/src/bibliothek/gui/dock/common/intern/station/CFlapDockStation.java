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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.station.flap.layer.FlapOverrideDropLayer;
import bibliothek.gui.dock.station.flap.layer.FlapSideDropLayer;
import bibliothek.gui.dock.station.flap.layer.WindowDropLayer;
import bibliothek.gui.dock.station.layer.DefaultDropLayer;
import bibliothek.gui.dock.station.layer.DockStationDropLayer;
import bibliothek.gui.dock.title.DockTitleRequest;

/**
 * An implementation of {@link FlapDockStation} that also satisfies the
 * {@link CommonDockStation} interface.
 * @author Benjamin Sigg
 */
public class CFlapDockStation extends FlapDockStation implements CommonDockStation<FlapDockStation, CFlapDockStation>{
	private CommonStationDelegate<CFlapDockStation> delegate;
	private Component expansion;
	private CHoldActionHandler actionHandler = new CHoldActionHandler();
	
	/**
	 * Creates a new station.
	 * @param expansion the {@link Component} which should define the size and location of
	 * this station, can be <code>null</code>
	 * @param delegate offers additional methods required by this station
	 */
	public CFlapDockStation( Component expansion, CommonStationDelegate<CFlapDockStation> delegate ){
		this.expansion = expansion;
		this.delegate = delegate;
		
		addDockStationListener( new HoldActionHandler() );
	}
	
	@Override
	public String getFactoryID(){
		return CommonDockStationFactory.FACTORY_ID;
	}
	
	public String getConverterID(){
		return super.getFactoryID();
	}
	
	public CDockable getDockable(){
		return delegate.getDockable();
	}

	public DockActionSource[] getSources(){
		return delegate.getSources();
	}

	public CStation<CFlapDockStation> getStation(){
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
	public DockStationDropLayer[] getLayers(){
		DockStationDropLayer[] layers;
		if( getDockableCount() == 0 ){
			layers = new DockStationDropLayer[4];
		}
		else{
			layers = new DockStationDropLayer[3];
		}
		
		layers[0] = new DefaultDropLayer( this ){
			@Override
			public Component getComponent(){
				return CFlapDockStation.this.getComponent();
			}
		};
		layers[1] =	new FlapOverrideDropLayer( this ){
			@Override
			public Component getComponent(){
				return CFlapDockStation.this.getComponent();
			}
		};
		layers[2] = new WindowDropLayer( this );
		if( getDockableCount() == 0 ){
			layers[3] = new FlapSideDropLayer( this ){
				@Override
				public Component getComponent(){
					return CFlapDockStation.this.getComponent();
				}
			};
		}
		return layers;
	}
	
	@Override
	protected DockableHandle createHandle( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			DockableHandle handle = new DockableHandle( dockable, true );
			update( handle, ((CommonDockable)dockable).getDockable() );
			return handle;
		}
		else{
			return super.createHandle( dockable );
		}
	}
	
	private void update( DockableHandle handle, CDockable dockable ){
		CAction action = dockable.getAction( CDockable.ACTION_KEY_MINIMIZE_HOLD );
		if( action == null ){
			handle.resetHoldAction();
		}
		else{
			handle.getActions().setHoldAction( action.intern() );
		}
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
	
	/**
	 * This listener is added to this {@link CFlapDockStation} and keeps track of the current
	 * {@link Dockable}s. This listener is responsible for reading the action {@link CDockable#ACTION_KEY_MINIMIZE_HOLD}.
	 * @author Benjamin Sigg
	 */
	private class HoldActionHandler extends DockStationAdapter{
		@Override
		public void dockableAdded( DockStation station, Dockable dockable ){
			if( dockable instanceof CommonDockable ){
				((CommonDockable)dockable).getDockable().addCDockablePropertyListener( actionHandler );
			}
		}
		
		@Override
		public void dockableRemoved( DockStation station, Dockable dockable ){
			if( dockable instanceof CommonDockable ){
				((CommonDockable)dockable).getDockable().removeCDockablePropertyListener( actionHandler );
			}
		}
	}
	
	private class CHoldActionHandler extends CDockableAdapter{
		@Override
		public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ){
			if( key.equals( CDockable.ACTION_KEY_MINIMIZE_HOLD )){
				DockableHandle handle = getHandle( dockable.intern() );
				if( handle != null ){
					update( handle, dockable );
				}
			}
		}
	}
}
