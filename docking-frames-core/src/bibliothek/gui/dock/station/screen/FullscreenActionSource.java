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
package bibliothek.gui.dock.station.screen;

import java.util.Iterator;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A {@link DockActionSource} that adds or removes a {@link DockAction} depending on the result
 * of the {@link ScreenDockFullscreenFilter}s that are currently registered at a 
 * {@link ScreenDockStation}.
 * @author Benjamin Sigg
 */
public abstract class FullscreenActionSource extends AbstractDockActionSource {
	private DockAction action;
	private LocationHint hint;

	private boolean showing = false;
	
	/**
	 * Creates a new source.
	 * @param action the action to show
	 * @param hint tells where to show this source
	 */
	public FullscreenActionSource( DockAction action, LocationHint hint ){
		this.action = action;
		this.hint = hint;
	}

	public LocationHint getLocationHint(){
		return hint;
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( !hasListeners() ){
			listen( true );
			update();
		}
		super.addDockActionSourceListener( listener );
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( !hasListeners() ){
			listen( false );
		}
	}

	/**
	 * Checks whether the action should be shown or not, and fires
	 * events if the value changed since the last update.
	 */
	public void update(){
		boolean enabled = isFullscreenEnabled();
		if( showing != enabled ){
			showing = enabled;
			if( showing ){
				fireAdded( 0, 0 );
			}
			else{
				fireRemoved( 0, 0 );
			}
		}
	}
	
	public int getDockActionCount(){
		if( hasListeners() ){
			if( showing ){
				return 1;
			}
			else{
				return 0;
			}
		}
		else if( isFullscreenEnabled() ){
			showing = true;
			return 1;
		}
		else{
			showing = false;
			return 0;
		}
	}

	public DockAction getDockAction( int index ){
		if( index < 0 || index >= getDockActionCount() ){
			throw new IllegalArgumentException( "index out of bounds" );
		}
		return action;
	}

	public Iterator<DockAction> iterator(){
		return new Iterator<DockAction>(){
			private int index = 0;
			
			public boolean hasNext(){
				return index < getDockActionCount();
			}
			
			public DockAction next(){
				return getDockAction( index++ );
			}
			
			public void remove(){
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Tells whether the action should be shown or not. 
	 * @return <code>true</code> if the action should be shown
	 */
	protected abstract boolean isFullscreenEnabled();

	/**
	 * Tells whether this {@link DockActionSource} has listeners or not.
	 * @param listening whether there are listeners or not
	 */
	protected abstract void listen( boolean listening );

}
