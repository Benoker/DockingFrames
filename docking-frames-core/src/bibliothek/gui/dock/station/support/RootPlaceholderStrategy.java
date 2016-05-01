/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderStrategy} that wraps around another strategy or around no
 * strategy at all.
 * @author Benjamin Sigg
 */
public class RootPlaceholderStrategy implements PlaceholderStrategy {
	/** the owner of this strategy */
	private DockStation station;
	/** the listener which use filtered events */
	private List<PlaceholderStrategyListener> listeners;
	
	/** delegate of this strategy*/
	private PlaceholderStrategy strategy;
	
	/** the placeholders that are currently in use */
	private Set<Path> placeholders = new HashSet<Path>();
	
	/** a listener for the current delegate */
	private PlaceholderStrategyListener listener = new PlaceholderStrategyListener() {
		public void placeholderInvalidated( Set<Path> placeholder ){
			placeholders.removeAll( placeholder );
		}
	};
	
	/**
	 * Creates a new strategy.
	 * @param station the station for which this strategy is used
	 */
	public RootPlaceholderStrategy( DockStation station ){
		this.station = station;
	}
	
	/**
	 * Sets the delegate of this strategy.
	 * @param strategy the new delegate, can be <code>null</code>
	 */
	public void setStrategy( PlaceholderStrategy strategy ){
		if( this.strategy != strategy ){
			if( this.strategy != null ){
				this.strategy.removeListener( listener );
				this.strategy.uninstall( station );
			}
			
			this.strategy = strategy;
			
			if( this.strategy == null ){
				if( !placeholders.isEmpty() ){
					fireRemoved( Collections.unmodifiableSet( placeholders ) );
					placeholders.clear();
				}
			}
			else{
				this.strategy.install( station );
				this.strategy.addListener( listener );
				Set<Path> removed = new HashSet<Path>();
				Iterator<Path> iter = placeholders.iterator();
				while( iter.hasNext() ){
					Path next = iter.next();
					if( !this.strategy.isValidPlaceholder( next )){
						iter.remove();
						removed.add( next );
					}
				}
				if( !removed.isEmpty() ){
					fireRemoved( removed );
				}
			}
		}
	}
	
	private void fireRemoved( Set<Path> placeholders ){
		for( PlaceholderStrategyListener listener : listeners.toArray( new PlaceholderStrategyListener[ listeners.size() ] )){
			listener.placeholderInvalidated( placeholders );
		}
	}
	
	/**
	 * Gets the strategy that is the current delegate.
	 * @return the delegate, can be <code>null</code>
	 */
	public PlaceholderStrategy getStrategy(){
		return strategy;
	}
	
	public void addListener( PlaceholderStrategyListener listener ){
		if( listeners == null ){
			listeners = new ArrayList<PlaceholderStrategyListener>();
		}
		listeners.add( listener );
		if( strategy != null ){
			strategy.addListener( listener );
		}
	}

	public void removeListener( PlaceholderStrategyListener listener ){
		if( listeners != null ){
			listeners.remove( listener );
		}
		if( strategy != null ){
			strategy.removeListener( listener );
		}
	}

	
	public Path getPlaceholderFor( Dockable dockable ){
		if( strategy == null )
			return null;
		Path result = strategy.getPlaceholderFor( dockable );
		if( result != null ){
			placeholders.add( result );
		}
		return result;
	}

	public void install( DockStation station ){
		throw new IllegalStateException( "this strategy must not be installed" );
	}

	public boolean isValidPlaceholder( Path placeholder ){
		if( strategy == null )
			return false;
		boolean result = strategy.isValidPlaceholder( placeholder );
		if( result ){
			placeholders.add( placeholder );
		}
		return result;
	}

	public void uninstall( DockStation station ){
		throw new IllegalStateException( "this strategy must not be uninstalled" );
	}
}
