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
package bibliothek.gui.dock.station.flap;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;

/**
 * This {@link DockActionSource} is used by the {@link FlapDockStation} to show the hold-action
 * for a {@link Dockable}.
 * @author Benjamin Sigg
 */
public class FlapDockStationSource extends DefaultDockActionSource{
	/** the action to show */
	private DockAction holdAction;
	/** the owner of this source */
	private FlapDockStation station;
	/** the dockable for which this source is used */
	private Dockable dockable;
	
	/**
	 * Creates a new {@link FlapDockStationSource}. 
	 * @param station the owner of this source
	 * @param dockable the element for which this source is used
	 * @param holdAction the action to show, can be <code>null</code>
	 */
	public FlapDockStationSource( FlapDockStation station, Dockable dockable, DockAction holdAction ){
		super( new LocationHint( LocationHint.DIRECT_ACTION, LocationHint.LITTLE_LEFT ));
		
		this.station = station;
		this.dockable = dockable;
		this.holdAction = holdAction;
	}
	
	/**
	 * Changes the action that is shown in this source
	 * @param holdAction the new action to show, can be <code>null</code>
	 */
	public void setHoldAction( DockAction holdAction ){
		this.holdAction = holdAction;
		removeAll();
		updateHoldSwitchable();
	}
	
	/**
	 * Adds or removes the only action of this source depending on the result
	 * of {@link FlapLayoutManager#isHoldSwitchable(FlapDockStation, Dockable)}.
	 */
	public void updateHoldSwitchable(){
		if( station.getCurrentFlapLayoutManager().isHoldSwitchable( station, dockable ) ){
			if( getDockActionCount() == 0 && holdAction != null ){
				add( holdAction );
			}
		}
		else{
			removeAll();
		}
	}
}
