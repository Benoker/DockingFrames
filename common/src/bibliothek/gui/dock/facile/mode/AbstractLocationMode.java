/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.facile.mode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.support.mode.AffectedSet;
import bibliothek.gui.dock.support.mode.Mode;
import bibliothek.gui.dock.support.mode.ModeManager;
import bibliothek.gui.dock.support.mode.ModeManagerListener;

/**
 * {@link Mode} that manages a set of key-value pairs.
 * @author Benjamin Sigg
 * @param <A> the values managed by this mode
 */
public abstract class AbstractLocationMode<A extends ModeArea> implements Iterable<A>, LocationMode{
	/** The areas managed by this mode */
	private Map<String, A> areas = new HashMap<String, A>();
	
	/** The order in which the areas were added */
	private List<A> areaOrder = new LinkedList<A>();
	
	/** default location to use when a key is not found in {@link #areas} */
	private A defaultArea;
	
	/** the manager responsible for this mode */
	private LocationModeManager<?> manager;

	/** the list of known listeners */
	private List<LocationModeListener> listeners = new ArrayList<LocationModeListener>();
	
	/** the controller in whose realm this mode works */
	private DockController controller;
	
	/** added to all {@link ModeArea}s of this mode */
	private AreaListener modeAreaListener = new AreaListener();
	
	/** listener to register new dockables and remove old ones */
	private ModeManagerListener<Location, LocationMode> managerListener = new ManagerListener();
	
	/** temporary information associated with the currently registered Dockables */
	private Map<Dockable, DockableHandle> handles = new HashMap<Dockable, DockableHandle>();
	
	/** provides actions for the {@link Dockable}s known to this mode */
	private LocationModeActionProvider actionProvider = new DefaultLocationModeActionProvider();
	
	/**
	 * Sets the {@link LocationModeActionProvider} for this mode.
	 * @param actionProvider the provider, not <code>null</code>
	 * @throws IllegalArgumentException if <code>actionProvider</code> is <code>null</code>
	 * @throws IllegalStateException if there are already {@link Dockable}s which show actions
	 * that are provided by the current {@link LocationModeActionProvider}
	 */
	public void setActionProvider( LocationModeActionProvider actionProvider ){
		if( actionProvider == null )
			throw new IllegalArgumentException( "actionProvider must not be null" );
		
		if( !handles.isEmpty() )
			throw new IllegalStateException( "can only set actionProvider if no Dockables are currently showing actions of the old provider" );
		
		this.actionProvider = actionProvider;
	}
	
	public void setManager( LocationModeManager<?> manager ){
		if( this.manager != null ){
			for( A area : areas.values() ){
				area.removeModeAreaListener( modeAreaListener );
			}
		}
		
		this.manager = manager;
		
		if( this.manager != null ){
			for( A area : areas.values() ){
				area.addModeAreaListener( modeAreaListener );
			}
			this.manager.addModeManagerListener( managerListener );
		}
	}
	
	/**
	 * Gets the owner of this mode.
	 * @return the owner, not <code>null</code>
	 */
	public LocationModeManager<?> getManager(){
		return manager;
	}
	
	public void setController( DockController controller ){
		this.controller = controller;
		for( A area : areas.values() ){	
			area.setController( controller );
		}
	}
	
	/**
	 * Gets the controller in whose realm this mode works.
	 * @return the controller or <code>null</code>
	 */
	public DockController getController(){
		return controller;
	}
	
	public void addLocationModeListener( LocationModeListener listener ){
		if( listener == null )
			throw new IllegalArgumentException( "listener must not be null" );
		listeners.add( listener );
	}
	
	public void removeLocationModeListener( LocationModeListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all the listeners that are currently registered at this mode.
	 * @return all the listeners
	 */
	protected LocationModeListener[] listeners(){
		return listeners.toArray( new LocationModeListener[ listeners.size() ] );
	}
	
	/**
	 * Adds an area to this mode.
	 * @param area the new area, not <code>null</code>
	 */
	public void add( A area ){
		if( area == null )
			throw new IllegalArgumentException( "area must not be null" );
		
		String key = area.getUniqueId();
		if( areas.containsKey( key ))
			throw new IllegalArgumentException( "key '" + key + "' already in use" );
		
		area.setController( getController() );
		areas.put( key, area );
		areaOrder.add( area );
		
		if( getManager() != null ){
			area.addModeAreaListener( modeAreaListener );
		}
	}
	
	/**
	 * Removes the area with identifier <code>key</code> from this
	 * mode.
	 * @param key the identifier of the area
	 * @return the removed area or <code>null</code>
	 */
	public A remove( String key ){
		A area = areas.remove( key );
		if( defaultArea == area ){
			defaultArea = null;
		}
		if( area != null ){
			area.setController( null );
			area.removeModeAreaListener( modeAreaListener );
			areaOrder.remove( area );
		}
		return area;
	}
	
	public Iterator<A> iterator(){
		return areas.values().iterator();
	}
	
	/**
	 * Sets the default area of this mode. The default area is used when
	 * {@link #get(Dockable)} returns <code>null</code> for some key.
	 * @param defaultArea the default area, can be <code>null</code>. Must be
	 * registered using {@link #add(ModeArea)} first.
	 */
	public void setDefaultArea( A defaultArea ){
		if( defaultArea != null ){
			if( !areas.containsKey( defaultArea.getUniqueId() ))
				throw new IllegalArgumentException( "default area must be registered, call 'add' first" );
		}
		this.defaultArea = defaultArea;
	}
	
	/**
	 * Gets the default area of this mode, can be <code>null</code>. The default area
	 * is the oldest area that was added to this mode or the one area set
	 * through {@link #setDefaultArea(ModeArea)}.
	 * @return the default area
	 */
	public A getDefaultArea(){
		if( defaultArea == null && !areaOrder.isEmpty() ){
			return areaOrder.get( 0 );
		}
		
		return defaultArea;
	}
	
	/**
	 * Gets the area with the specified id.
	 * @param key the name of the area
	 * @return the area or <code>null</code>
	 */
	public A get( String key ){
		return areas.get( key );
	}
	
	public DockStation getRepresentation( String uniqueId ){
		A area = get( uniqueId );
		if( area == null )
			return null;
		return area.getStation();
	}
	
	/**
	 * Recursively searches through all stations of <code>dockable</code>
	 * until a station is found that is registered at this mode.
	 * @param dockable the element whose root is searched
	 * @return the root or <code>null</code>, never <code>dockable</code> itself
	 */
	public A get( Dockable dockable ){
		while( dockable != null ){
			for( A area : areas.values() ){
				if( area.isChild( dockable ) ){
					return area;
				}
			}
			DockStation station = dockable.getDockParent();
			if( station == null )
				return null;
			dockable = station.asDockable();
		}
		return null;
	}

	public DockActionSource getActionsFor( Dockable dockable, Mode<Location> mode ){
		if( mode == this ){
			return null;
		}
		if( !isModeAvailable( dockable )){
			return null;
		}
		DockableHandle handle = handles.get( dockable );
		if( handle == null )
			return null;
		else
			return handle.getActions( mode );
	}
	
	/**
	 * Tells whether this mode is available for <code>dockable</code>.
	 * @param dockable some element to check
	 * @return <code>true</code> if this mode is available
	 */
	protected boolean isModeAvailable( Dockable dockable ){
		LocationModeManager<?> manager = getManager();
		if( manager == null )
			return false;
		return manager.isModeAvailable( dockable, getExtendedMode() );
	}
	
	public boolean isRepresenting( DockStation station ){
		for( A area : areas.values() ){
			if( area.getStation() == station ){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean respectWorkingAreas( DockStation station ){
		// search area
		while( station != null ){
			for( A area : areas.values() ){
				if( area.getStation() == station ){
					return area.respectWorkingAreas();
				}
			}
			Dockable dockable = station.asDockable();
			station = dockable == null ? null : dockable.getDockParent();
		}
		
		return true;
	}
	
	public void apply( Dockable dockable, Location history, AffectedSet set ){
		LocationModeEvent event = new LocationModeEvent( this, history, dockable, set );
		for( LocationModeListener listener : listeners() ){
			listener.applyStarting( event );
		}
		if( !event.isDone() ){
			runApply( dockable, history, set );
			event.done();
		}
		for( LocationModeListener listener : listeners() ){
			listener.applyDone( event );
		}
	}
	
	/**
	 * Called by {@link #apply(Dockable, Location, AffectedSet)} after the {@link LocationModeListener}s
	 * are informed. Applies this mode to <code>dockable</code>.
	 * @param dockable the element whose mode becomes <code>this</code>
	 * @param history history information that was returned by this mode when calling
	 * {@link #current(Dockable)} the last time.
	 * @param set this method has to store all {@link Dockable}s which might have changed their
	 * mode in the set.
	 */
	protected abstract void runApply( Dockable dockable, Location history, AffectedSet set );
	
	/**
	 * Creates a new handle for <code>dockable</code>.
	 * @param dockable the newly registered element
	 * @return the new handle
	 */
	protected DockableHandle createHandle( Dockable dockable ){
		return new DockableHandle( dockable );
	}
	
	/**
	 * A listener added to all {@link ModeArea}s.
	 * @author Benjamin Sigg
	 */
	private class AreaListener implements ModeAreaListener{
		public void internalLocationChange( ModeArea source, Set<Dockable> dockables ){
			LocationModeManager<?> manager = getManager();
			if( manager != null ){
				if( manager.isOnTransaction() ){
					manager.addAffected( dockables );
				}
				else{
					for( Dockable dockable : dockables ){
						manager.refresh( dockable, true );
					}
				}
			}
		}
	}
	
	/**
	 * Listener added to register new and removed {@link Dockable}s.
	 * @author Benjamin Sigg
	 */
	private class ManagerListener implements ModeManagerListener<Location, LocationMode>{
		public void dockableAdded( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			if( !handles.containsKey( dockable )){
				DockableHandle handle = createHandle( dockable );
				handles.put( dockable, handle );
			}
		}
		
		public void dockableRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable ){
			DockableHandle handle = handles.remove( dockable );
			if( handle != null ){
				handle.destroy();
			}
		}
		
		public void modeAdded( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			// ignore	
		}
		
		public void modeChanged( ModeManager<? extends Location, ? extends LocationMode> manager, Dockable dockable, LocationMode oldMode, LocationMode newMode ){
			// ignore
		}
		
		public void modeRemoved( ModeManager<? extends Location, ? extends LocationMode> manager, LocationMode mode ){
			// ignore	
		}
	}
	
	/**
	 * Meta information about a currently registered Dockable.
	 * @author Benjamin Sigg
	 */
	protected class DockableHandle{
		/** the dockable which is handled by this handle */
		private Dockable dockable;
		
		/** the current actions added by this mode to {@link #dockable} */
		private DockActionSource source;
		
		/**
		 * Creates a new handle.
		 * @param dockable the new element, not <code>null</code>
		 */
		public DockableHandle( Dockable dockable ){
			this.dockable = dockable;
		}
		
		/**
		 * Gets the element of this handle.
		 * @return the element, not <code>null</code>
		 */
		public Dockable getDockable(){
			return dockable;
		}
		
		/**
		 * Called when this handle is no longer of any use
		 */
		public void destroy(){
			actionProvider.destroy( dockable, source );
			dockable = null;
			source = null;
		}
		
		/**
		 * Called by {@link AbstractLocationMode#getActionsFor(Dockable, Mode)}
		 * to the actions related to this dockable. The default implementation
		 * is to return a source returned by the current {@link LocationModeActionProvider}.
		 * @param mode the current mode of this element
		 * @return the actions or <code>null</code>
		 */
		public DockActionSource getActions( Mode<Location> mode ){
			source = actionProvider.getActions( dockable, mode, source );
			return source;
		}
	}
}
