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
package bibliothek.gui.dock.facile.action;

import java.util.Iterator;

import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.event.CDockableAdapter;
import bibliothek.gui.dock.common.event.CDockablePropertyListener;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.util.FrameworkOnly;

/**
 * An action source using {@link CDockable#getAction(String)} to determine which
 * action to use. If <code>getAction</code> returns <code>null</code>, then
 * a default action can be used. This action source also allows to disable any
 * action.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class KeyedActionSource extends AbstractDockActionSource {
	/** the observed element */
	private CDockable dockable;
	
	/** the key of the action to handle */
	private String key;
	
	/** the default action to be used if {@link CDockable#getAction(String)} returns <code>null</code> */
	private DockAction defaultAction;
	
	/** the location of this source */
	private LocationHint hint = LocationHint.UNKNOWN;
	
	/** whether to show an action at all or not */
	private boolean visible = true;

	/** whether {@link #propertyListener} is added to {@link #dockable} */
	private boolean propertyListenerInstalled = false;
	
	/** a listener observing {@link #dockable} */
	private CDockablePropertyListener propertyListener = new CDockableAdapter(){
		public void actionChanged( CDockable dockable, String key, CAction oldAction, CAction newAction ){
			if( visible ){
				boolean shown = (oldAction != null) || (defaultAction != null);
				boolean showing = (newAction != null) || (defaultAction != null);
				
				if( shown && !showing )
					fireRemoved( 0, 0 );
				else if( !shown && showing )
					fireAdded( 0, 0 );
				else if( shown && showing ){
					visible = false;
					fireRemoved( 0, 0 );
					visible = true;
					fireAdded( 0, 0 );
				}
			}
		}
	};

	/**
	 * Creates a new action source
	 * @param dockable the element for which this source is used
	 * @param key the key for calling {@link CDockable#getAction(String)}
	 */
	public KeyedActionSource( CDockable dockable, String key ){
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		if( key == null )
			throw new IllegalArgumentException( "key must not be null" );
		this.dockable = dockable;
		this.key = key;
	}
	
	/**
	 * Gets the key which is used for calling {@link CDockable#getAction(String)}
	 * @return the key, not <code>null</code>
	 */
	public String getKey(){
		return key;
	}
	
	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		super.addDockActionSourceListener( listener );
		if( visible && !propertyListenerInstalled && !listeners.isEmpty() ){
			dockable.addCDockablePropertyListener( propertyListener );
			propertyListenerInstalled = true;
		}
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( visible && propertyListenerInstalled && listeners.isEmpty() ){
			dockable.removeCDockablePropertyListener( propertyListener );
			propertyListenerInstalled = false;
		}
	}
	
	/**
	 * Detaches the listeners this {@link DockActionSource} added to
	 * other objects.
	 */
	public void destroy(){
		if( propertyListenerInstalled ){
			dockable.removeCDockablePropertyListener( propertyListener );
			propertyListenerInstalled = false;
		}
	}
	
	/**
	 * Changes whether any actions are shown or not.
	 * @param visible the new state
	 */
	public void setVisible( boolean visible ){
		if( this.visible != visible ){
			int current = getDockActionCount();
			this.visible = visible;
			
			if( visible ){
				if( !propertyListenerInstalled && !listeners.isEmpty() ){
					dockable.addCDockablePropertyListener( propertyListener );
					propertyListenerInstalled = true;
				}
			}
			else{
				if( propertyListenerInstalled ){
					dockable.removeCDockablePropertyListener( propertyListener );
					propertyListenerInstalled = false;
				}
			}
			int updated = getDockActionCount();
			
			if( current < updated )
				fireAdded( 0, 0 );
			else if( current > updated )
				fireRemoved( 0, 0 );
		}
	}
	
	/**
	 * Sets the default action of this source. The default action
	 * is used if {@link CDockable#getAction(String)} returns
	 * <code>null</code>.
	 * @param defaultAction the default action, may be <code>null</code>
	 */
	public void setDefaultAction( DockAction defaultAction ){
		if( visible ){
			if( dockable.getAction( key ) == null ){
				visible = false;
				fireRemoved( 0, 0 );
				this.defaultAction = defaultAction;
				visible = true;
				fireAdded( 1, 1 );
			}
			else{
				this.defaultAction = defaultAction;
			}
		}
		else{
			this.defaultAction = defaultAction;
		}
	}
	
	/**
	 * Gets the default action of this source.
	 * @return the default action, can be <code>null</code>
	 */
	public DockAction getDefaultAction(){
		return defaultAction;
	}
	
	/**
	 * Gets the action that is currently to be shown.
	 * @return the current action or <code>null</code> if invisible or not available.
	 */
	private DockAction currentAction(){
		if( !visible )
			return null;
		
		CAction result = dockable.getAction( key );
		if( result == null ){
			return defaultAction;
		}
		
		return result.intern();
	}
	
	public DockAction getDockAction( int index ){
		if( index != 0 )
			throw new IllegalArgumentException( "only index=0 supported" );
		
		return currentAction();
	}

	public int getDockActionCount(){
		if( currentAction() != null )
			return 1;
		else
			return 0;
	}

	public LocationHint getLocationHint(){
		return hint;
	}

	public Iterator<DockAction> iterator(){
		return new Iterator<DockAction>(){
			private DockAction action = currentAction();
			
			public boolean hasNext(){
				return action != null;
			}
			
			public DockAction next(){
				if( action == null )
					throw new IllegalStateException( "no elements left" );
				DockAction result = action;
				action = null;
				return result;
			}
			
			public void remove(){
				throw new UnsupportedOperationException();	
			}
		};
	}
	
	
}
