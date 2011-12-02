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
package bibliothek.gui.dock.station.screen.magnet;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;

/**
 * A combination of several {@link AttractorStrategy}s into one strategy.
 * @author Benjamin Sigg
 */
public class MultiAttractorStrategy implements AttractorStrategy{
	/** all the strategies that are registered */
	private List<AttractorStrategy> strategies = new ArrayList<AttractorStrategy>();

	/**
	 * Adds <code>strategy</code> to the list of strategies that are used.
	 * @param strategy a new strategy, must not be <code>null</code>
	 */
	public void add( AttractorStrategy strategy ){
		if( strategy == null ){
			throw new IllegalArgumentException( "strategy must not be null" );
		}
		strategies.add( strategy );
	}
	
	/**
	 * Removes <code>strategy</code> from the list of strategies that are used.
	 * @param strategy the strategy to remove
	 */
	public void remove( AttractorStrategy strategy ){
		strategies.remove( strategy );
	}
	
	public Attraction attract( ScreenDockStation parent, Dockable moved, Dockable fixed ){
		Attraction attraction = Attraction.NEUTRAL;
		for( AttractorStrategy strategy : strategies ){
			Attraction next = strategy.attract( parent, moved, fixed );
			attraction = attraction.stronger( next );
		}
		return attraction;
	}

	public Attraction stick( ScreenDockStation parent, Dockable moved, Dockable fixed ){
		Attraction attraction = Attraction.NEUTRAL;
		for( AttractorStrategy strategy : strategies ){
			Attraction next = strategy.stick( parent, moved, fixed );
			attraction = attraction.stronger( next );
		}
		return attraction;
	}
}
