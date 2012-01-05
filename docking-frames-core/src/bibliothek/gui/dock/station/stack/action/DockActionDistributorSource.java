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
package bibliothek.gui.dock.station.stack.action;

import java.util.Iterator;
import java.util.NoSuchElementException;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * A wrapper around a {@link DockActionSource}, this source can be used in combination with
 * {@link DockActionDistributor} to completely hide the existence of the {@link DockActionDistributor}.<br>
 * Clients can use {@link #setDockable(Dockable)} to enable or disable this source. 
 * @author Benjamin Sigg
 */
public class DockActionDistributorSource extends AbstractDockActionSource{
	private DockActionDistributor.Target target;
	
	/** the current actions */
	private DockActionSource source;
	
	/** the current dockable */
	private Dockable dockable;
	
	private boolean buildSourceCheck = false;
	
	/** finds out what the current {@link DockController} is */
	private DockHierarchyListener listener = new DockHierarchyListener(){
		public void hierarchyChanged( DockHierarchyEvent event ){
			// ignored
		}
		
		public void controllerChanged( DockHierarchyEvent event ){
			reset();
		}
	};
	
	/** The {@link DockActionDistributor} that should be used to create new {@link DockActionSource}s. */
	private PropertyValue<DockActionDistributor> distributor = new PropertyValue<DockActionDistributor>( new PropertyKey<DockActionDistributor>( "dock.empty" ) ){
		@Override
		protected void valueChanged( DockActionDistributor oldValue, DockActionDistributor newValue ){
			buildSource( false );
		}
	};
	
	/** listeners to {@link #source} and forwards events if necessary */
	private DockActionSourceListener actionListener = new DockActionSourceListener(){
		public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex ){
			fireRemoved( firstIndex, lastIndex );
		}
		
		public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex ){
			fireAdded( firstIndex, lastIndex );
		}
	};
	
	/**
	 * Creates a new action source
	 * @param target where this action source will be shown
	 * @param key the name of the property pointing to a {@link DockActionDistributor}
	 */
	public DockActionDistributorSource( DockActionDistributor.Target target, PropertyKey<DockActionDistributor> key ){
		this.target = target;
		distributor.setKey( key );
	}
	
	/**
	 * Creates a new action source
	 * @param target where this action source will be shown
	 * @param key the name of the property pointing to a {@link DockActionDistributor}
	 * @param dockable the item for which this source will be used
	 */
	public DockActionDistributorSource( DockActionDistributor.Target target, PropertyKey<DockActionDistributor> key, Dockable dockable ){
		this( target, key );
		setDockable( dockable );
	}
	
	private DockActionSource source(){
		if( dockable == null ){
			return null;
		}
		if( source != null ){
			return source;
		}
		DockController controller = dockable.getController();
		if( controller == null ){
			return null;
		}
		DockActionDistributor distributor = controller.getProperties().get( this.distributor.getKey() );
		return distributor.createSource( dockable, target );
	}
	
	public DockAction getDockAction( int index ){
		DockActionSource source = source();
		return source.getDockAction( index );
	}
	
	public int getDockActionCount(){
		DockActionSource source = source();
		if( source == null ){
			return 0;
		}
		else{
			return source.getDockActionCount();
		}
	}
	
	public LocationHint getLocationHint(){
		DockActionSource source = source();
		if( source == null ){
			return LocationHint.UNKNOWN;
		}
		else{
			return source.getLocationHint();
		}
	}
	
	public Iterator<DockAction> iterator(){
		DockActionSource source = source();
		if( source == null ){
			return new Iterator<DockAction>(){
				public boolean hasNext(){
					return false;
				}
				public DockAction next(){
					throw new NoSuchElementException();
				}
				public void remove(){
					// ignore
				}
			};
		}
		return source.iterator();
	}
	
	/**
	 * Sets the {@link Dockable} whose {@link DockActionSource} this should be.
	 * @param dockable the new owner, can be <code>null</code>
	 */
	public void setDockable( Dockable dockable ){
		if( this.dockable != dockable ){
			if( this.dockable != null ){
				this.dockable.removeDockHierarchyListener( listener );
				this.dockable = null;
				setSource( null );
			}
			this.dockable = dockable;
			if( this.dockable != null ){
				if( hasListeners() ){
					this.dockable.addDockHierarchyListener( listener );
				}
				reset();
			}
		}
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( !hasListeners() ){
			if( dockable != null ){
				dockable.addDockHierarchyListener( this.listener );
				distributor.setProperties( dockable.getController() );
				buildSource( true );
			}
		}
		super.addDockActionSourceListener( listener );
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( !hasListeners() ){
			if( dockable != null ){
				dockable.removeDockHierarchyListener( this.listener );
				distributor.setProperties( (DockProperties)null );
				setSource( null );
			}
		}
	}
	
	private void setSource( DockActionSource source ){
		if( this.source != source ){
			if( this.source != null ){
				int size = getDockActionCount();
				this.source.removeDockActionSourceListener( actionListener );
				this.source = null;
				if( size > 0 ){
					fireRemoved( 0, size-1 );
				}
			}
			this.source = source;
			if( this.source != null ){
				int size = getDockActionCount();
				if( size > 0 ){
					fireAdded( 0, size-1 );
				}
				this.source.addDockActionSourceListener( actionListener );
			}
		}
	}
	
	private void reset(){
		if( dockable != null && hasListeners() ){
			DockController controller = dockable.getController();
			if( controller == null ){
				setSource( null );
			}
			buildSourceCheck = true;
			distributor.setProperties( controller );
			if( buildSourceCheck ){
				buildSource( false );
			}
		}
	}
	
	private void buildSource( boolean force ){
		if( force || hasListeners() ){
			buildSourceCheck = false;
			if( dockable != null ){
				setSource( distributor.getValue().createSource( dockable, target ) );
			}
		}
	}
}
