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
package bibliothek.gui.dock.common.intern;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.event.CControlListener;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;

/**
 * This strategy assigns a unique identifier to all {@link CDockable}s that
 * are registered at a {@link CControl}.
 * @author Benjamin Sigg
 */
public class CPlaceholderStrategy implements PlaceholderStrategy {
	/** the owner of this strategy */
	private CControl control;
	
	/** all the listeners that are registered */
	private List<PlaceholderStrategyListener> listeners = new ArrayList<PlaceholderStrategyListener>();
	
	private CControlListener listener = new CControlListener() {
		public void removed( CControl control, CDockable dockable ){
			if( dockable instanceof SingleCDockable ){
				String id = ((SingleCDockable)dockable).getUniqueId();
				Path check = new Path( "dock.single." + id );
				if( !isValidPlaceholder( check )){
					fireInvalidated( check );
				}
			}
		}
		
		public void opened( CControl control, CDockable dockable ){
			// ignore
		}
		
		public void closed( CControl control, CDockable dockable ){
			// ignore
		}
		
		public void added( CControl control, CDockable dockable ){
			// ignore
		}
	};
	
	/**
	 * Creates a new strategy
	 * @param control the control in whose realm this strategy operates
	 */
	public CPlaceholderStrategy( CControl control ){
		this.control = control;
	}
	
	public void addListener( PlaceholderStrategyListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			control.addControlListener( this.listener );
		}
		listeners.add( listener );
	}

	public void removeListener( PlaceholderStrategyListener listener ){
		listeners.remove( listener );
		if( listeners.isEmpty() ){
			control.removeControlListener( this.listener );
		}
	}
	
	protected void fireInvalidated( Path placeholder ){
		Set<Path> placeholders = new HashSet<Path>();
		placeholders.add( placeholder );
		placeholders = Collections.unmodifiableSet( placeholders );
		for( PlaceholderStrategyListener listener : listeners.toArray( new PlaceholderStrategyListener[ listeners.size() ] )){
			listener.placeholderInvalidated( placeholders );
		}
	}
	
	public Path getPlaceholderFor( Dockable dockable ){
		if( !(dockable instanceof CommonDockable) ){
			return null;
		}
		
		CDockable cdockable = ((CommonDockable)dockable).getDockable();
		CControlAccess controlAccess = cdockable.getControl();
		if( controlAccess == null || controlAccess.getOwner() != control ){
			return null;
		}
		
		if( cdockable instanceof SingleCDockable ){
			String id = ((SingleCDockable)cdockable).getUniqueId();
			return new Path( "dock", "single", id );
		}
		return null;
	}
	
	public boolean isValidPlaceholder( Path placeholder ){
		if( placeholder.getSegmentCount() != 4 ){
			return false;
		}
		if( !placeholder.getSegment( 0 ).equals( "dock" ) || !placeholder.getSegment( 1 ).equals( "single" )){
			return false;
		}
		
		String id = placeholder.getSegment( 2 );
		
		// if the element is registered, then it is available...
		if( control.getSingleDockable( id ) != null ){
			return true;
		}
		
		// if there is a backup factory, then the client expects this element to exist
		if( control.getSingleBackupFactory( id ) != null ){
			return true;
		}
		
		// maybe the client installed a strategy for handling missing dockables
		if( control.getMissingStrategy().shouldStoreSingle( id ) ){
			return true;
		}
		
		// the client does not want to store information about this element
		return false;
	}
	
	public void install( DockStation station ){
		// ignore
	}
	
	public void uninstall( DockStation station ){
		// ignore
	}
}
