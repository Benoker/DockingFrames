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

package bibliothek.gui.dock.station;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.StackDockStation;

/**
 * A {@link StackDockStation} modified such that it can show toolbar items.
 * 
 * @author Benjamin Sigg
 */
public class ToolbarTabDockStation extends StackDockStation implements OrientedDockStation{
	private List<OrientingDockStationListener> listeners = new ArrayList<OrientingDockStationListener>( 5 );
	private Orientation orientation = Orientation.VERTICAL;
	
	public ToolbarTabDockStation(){
		setSmallMinimumSize(false);
		setTitleIcon(null);
		
		new OrientationObserver( this ){
			@Override
			protected void orientationChanged( Orientation current ){
				if( current != null ){	
					setOrientation( current );
				}
			}
		};
	}

	@Override
	public String getFactoryID(){
		return ToolbarTabDockStationFactory.FACTORY_ID;
	}

	@Override
	public Orientation getOrientationOf( Dockable child ){
		return orientation;
	}

	@Override
	public void addOrientingDockStationListener( OrientingDockStationListener listener ){
		listeners.add( listener );
	}

	@Override
	public void removeOrientingDockStationListener( OrientingDockStationListener listener ){
		listeners.remove( listener );
	}

	@Override
	public void setOrientation( Orientation orientation ){
		if( this.orientation != orientation ){
			this.orientation = orientation;
			OrientingDockStationEvent event = new OrientingDockStationEvent( this );
			for( OrientingDockStationListener listener : listeners.toArray( new OrientingDockStationListener[ listeners.size() ] )){
				listener.changed( event );
			}
		}
	}

	@Override
	public Orientation getOrientation(){
		return orientation;
	}
}
