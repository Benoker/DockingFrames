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

package bibliothek.gui.dock.toolbar.expand;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * The default implementation of an {@link ExpandableToolbarItemStrategy}
 * searches for {@link ExpandableToolbarItem}s.
 * 
 * @author Benjamin Sigg
 */
public class DefaultExpandableToolbarItemStrategy implements ExpandableToolbarItemStrategy {
	/** All the listeners that are registered */
	private final List<ExpandableToolbarItemStrategyListener> listeners = new ArrayList<ExpandableToolbarItemStrategyListener>();

	/** the controller in whose realm this strategy is used */
	private DockController controller;

	/** this listener is used to detect new station that need to be observed */
	private final DockRegisterListener registerListener = new DockRegisterAdapter(){
		@Override
		public void dockStationRegistered( DockController controller, DockStation station ){
			handleAdd( station );
		}

		@Override
		public void dockableRegistered( DockController controller, Dockable dockable ){
			handleAdd( dockable );
		}
		
		@Override
		public void dockStationUnregistered( DockController controller, DockStation station ){
			handleRemove( station );
		}
		
		@Override
		public void dockableUnregistered( DockController controller, Dockable dockable ){
			handleRemove( dockable );
		}
	};

	/**
	 * A listener added to all {@link ExpandableToolbarItem}s.
	 */
	private final ExpandableToolbarItemListener expandableListener = new ExpandableToolbarItemListener(){
		@Override
		public void changed( ExpandableToolbarItem item, ExpandedState oldState, ExpandedState newState ){
			fire( item, newState );
		}
		
		@Override
		public void enablementChanged( ExpandableToolbarItem item, ExpandedState state, boolean enabled ){
			for( ExpandableToolbarItemStrategyListener listener : listeners() ){
				listener.enablementChanged( item, state, enabled );
			}
		}
	};
	
	protected void handleAdd( Dockable dockable ){
		if( dockable.asDockStation() == null && dockable instanceof ExpandableToolbarItem ){
			ExpandableToolbarItem item = (ExpandableToolbarItem)dockable;
			item.addExpandableListener( expandableListener );
			fire( item );
		}
	}

	protected void handleAdd( DockStation station ){
		if( station instanceof ExpandableToolbarItem ) {
			ExpandableToolbarItem item = (ExpandableToolbarItem)station;
			item.addExpandableListener( expandableListener );
			fire( item );
		}
	}
	
	private void fire( ExpandableToolbarItem item ){
		fire(  item, getState( item ) );
		for( ExpandedState state : ExpandedState.values() ){
			boolean enabled = isEnabled( item, state );
			for( ExpandableToolbarItemStrategyListener listener : listeners() ){
				listener.enablementChanged( item, state, enabled );
			}
		}
	}
	
	private void fire( ExpandableToolbarItem item, ExpandedState state ){
		switch( state ){
			case EXPANDED:
				for( final ExpandableToolbarItemStrategyListener listener : listeners() ) {
					listener.expanded( item );
				}
				break;
			case SHRUNK:
				for( final ExpandableToolbarItemStrategyListener listener : listeners() ) {
					listener.shrunk( item );
				}
				break;
			case STRETCHED:
				for( final ExpandableToolbarItemStrategyListener listener : listeners() ) {
					listener.stretched( item );
				}
				break;
		}
	}
	
	protected void handleRemove( Dockable dockable ){
		if( dockable.asDockStation() == null && dockable instanceof ExpandableToolbarItem ){
			((ExpandableToolbarItem) dockable).removeExpandableListener( expandableListener );
		}
	}

	protected void handleRemove( DockStation station ){
		if( station instanceof ExpandableToolbarItem ) {
			((ExpandableToolbarItem) station).addExpandableListener( expandableListener );
		}
	}

	@Override
	public void install( DockController controller ){
		if( this.controller != null ) {
			throw new IllegalStateException( "this strategy is already installed" );
		}
		this.controller = controller;
		final DockRegister register = controller.getRegister();

		register.addDockRegisterListener( registerListener );
		for( int i = 0, n = register.getStationCount(); i < n; i++ ) {
			handleAdd( register.getStation( i ) );
		}
	}

	@Override
	public void uninstall( DockController controller ){
		if( this.controller != controller ) {
			throw new IllegalStateException( "this strategy is not installed at '" + controller + "'" );
		}
		final DockRegister register = controller.getRegister();
		register.removeDockRegisterListener( registerListener );

		for( int i = 0, n = register.getStationCount(); i < n; i++ ) {
			handleRemove( register.getStation( i ) );
		}
	}

	@Override
	public boolean isEnabled( Dockable item, ExpandedState state ){
		if( item instanceof ExpandableToolbarItem ){
			return ((ExpandableToolbarItem)item).isEnabled( state );
		}
		return false;
	}

	@Override
	public ExpandedState getState( Dockable item ){
		if( item instanceof ExpandableToolbarItem ) {
			return ((ExpandableToolbarItem) item).getExpandedState();
		}
		return null;
	}

	@Override
	public void setState( Dockable item, ExpandedState state ){
		((ExpandableToolbarItem) item).setExpandedState( state );
	}

	@Override
	public void addExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		if( listener == null ) {
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	@Override
	public void removeExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		listeners.remove( listener );
	}

	/**
	 * Gets all the {@link ExpandableToolbarItemStrategyListener}s that are
	 * currently registered.
	 * 
	 * @return all the listeners
	 */
	protected ExpandableToolbarItemStrategyListener[] listeners(){
		return listeners.toArray( new ExpandableToolbarItemStrategyListener[listeners.size()] );
	}
}
