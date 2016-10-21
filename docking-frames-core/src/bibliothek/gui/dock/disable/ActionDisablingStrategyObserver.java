/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.disable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This class offers a convenient way to connect a {@link DockAction} with the current
 * {@link DisablingStrategy}. It only requires the {@link DockAction} to call {@link #bind(Dockable)}
 * and {@link #unbind(Dockable)} in order to observe a {@link Dockable}.  
 * @author Benjamin Sigg
 */
public abstract class ActionDisablingStrategyObserver {
	private DockAction action;

	/** all the observers that are currently monitoring {@link Dockable}s to find {@link DockController}s */
	private Map<Dockable, DockableObserver> dockables = new HashMap<Dockable, DockableObserver>();

	/** all the observers that are currently monitoring {@link DockController}s. */
	/* This might be a little overkill, but it's the way to ensure correctness in even the strangest settings */
	private Map<DockController, ControllerObserver> controllers = new HashMap<DockController, ControllerObserver>( 1 );
	
	/**
	 * Creates a new observer.
	 * @param action the action whose disabled state will be monitored.
	 */
	public ActionDisablingStrategyObserver( DockAction action ){
		if( action == null ){
			throw new IllegalArgumentException( "action must not be null" );
		}
		this.action = action;
	}
	
	/**
	 * Must be called to inform this observer that <code>dockable</code> is to be observed. Can be
	 * called multiple times.
	 * @param dockable the item to observe
	 */
	public void bind( Dockable dockable ){
		DockableObserver observer = dockables.get( dockable );
		if( observer == null ){
			observer = new DockableObserver( dockable );
			dockables.put( dockable, observer );
		}
		observer.inc();
	}
	
	/**
	 * Must be called to inform this observer that <code>dockable</code> is no longer to be observed. 
	 * Can be called multiple times.
	 * @param dockable the item to observe
	 */
	public void unbind( Dockable dockable ){
		DockableObserver observer = dockables.get( dockable );
		if( observer != null ){
			observer.dec();
			if( observer.destroy() ){
				dockables.remove( dockable );
			}
		}
	}
	
	private void set( DockController oldController, DockController newController, Dockable dockable ){
		if( oldController != newController ){
			if( oldController != null ){
				ControllerObserver observer = controllers.get( oldController );
				observer.remove( dockable );
				if( observer.destroy() ){
					controllers.remove( oldController );
				}
			}
			if( newController != null ){
				ControllerObserver observer = controllers.get( newController );
				if( observer == null ){
					observer = new ControllerObserver( newController );
					controllers.put( newController, observer );
				}
				observer.add( dockable );
			}
		}
	}
	
	/**
	 * Called if the disabled state of the action changed.
	 * @param dockable the dockable for which the state changed
	 * @param disabled the new state
	 */
	protected abstract void setDisabled( Dockable dockable, boolean disabled );
	
	/**
	 * Called if an entire set of {@link Dockable}s changed their state.
	 * @param dockable the items that changed their state
	 * @param disabled the new state
	 */
	protected abstract void setDisabled( Set<Dockable> dockable, boolean disabled );
	
	/**
	 * Tells whether the action of <code>dockable</code> is disabled.
	 * @param dockable the element whose action state is searched
	 * @return <code>true</code> if the action is disabled, <code>false</code> otherwise
	 */
	public boolean isDisabled( Dockable dockable ){
		DockController controller = dockable.getController();
		if( controller != null ){
			ControllerObserver observer = controllers.get( controller );
			if( observer != null ){
				return observer.isDisabled( dockable );
			}
		}
		return false;
	}
	
	/**
	 * Observes a single {@link Dockable} to find its current {@link DockController}.
	 * @author Benjamin Sigg
	 */
	private class DockableObserver implements DockHierarchyListener{
		private Dockable dockable;
		private DockController controller;
		private int count = 0;
		
		public DockableObserver( Dockable dockable ){
			this.dockable = dockable;
			dockable.addDockHierarchyListener( this );
			DockController controller = dockable.getController();
			if( controller != null ){
				bind( controller );
			}
		}
		
		private void bind( DockController controller ){
			set( this.controller, controller, dockable );
			this.controller = controller;
		}
		
		public void inc(){
			count++;
		}
		
		public void dec(){
			count--;
		}
		
		public boolean destroy(){
			if( count <= 0 ){
				dockable.removeDockHierarchyListener( this );
				bind( null );
				return true;
			}
			return false;
		}
		
		public void controllerChanged( DockHierarchyEvent event ){
			bind( dockable.getController() );
		}
		
		public void hierarchyChanged( DockHierarchyEvent event ){
			// ignore	
		}
	}
	
	/**
	 * Observers a {@link DockController} to find its current {@link DisablingStrategy} and uses
	 * the strategy to get the state of several {@link Dockable}s.
	 * @author Benjamin Sigg
	 */
	private class ControllerObserver extends PropertyValue<DisablingStrategy> implements DisablingStrategyListener{
		private Set<Dockable> dockables = new HashSet<Dockable>();
		
		public ControllerObserver( DockController controller ){
			super( DisablingStrategy.STRATEGY );
			setProperties( controller );
		}
		
		@Override
		protected void valueChanged( DisablingStrategy oldValue, DisablingStrategy newValue ){
			if( oldValue != null ){
				oldValue.removeDisablingStrategyListener( this );
			}
			if( newValue != null ){
				newValue.addDisablingStrategyListener( this );
				
				Set<Dockable> enabled = new HashSet<Dockable>();
				Set<Dockable> disabled = new HashSet<Dockable>();
				
				for( Dockable item : dockables ){
					if( newValue.isDisabled( item, action )){
						disabled.add( item );
					}
					else{
						enabled.add( item );
					}
				}
				
				if( !enabled.isEmpty() ){
					setDisabled( enabled, false );
				}
				if( !disabled.isEmpty() ){
					setDisabled( disabled, false );
				}
			}
			else{
				setDisabled(  dockables, false );
			}
		}
		
		public void changed( DockElement item ){
			Dockable dockable = item.asDockable();
			if( dockable != null && dockables.contains( dockable )){
				setDisabled( dockable, getValue().isDisabled( dockable, action ) );
			}
		}
		
		public boolean isDisabled( Dockable dockable ){
			DisablingStrategy strategy = getValue();
			if( strategy != null ){
				return strategy.isDisabled( dockable, action );
			}
			return false;
		}
		
		public void add( Dockable dockable ){
			DisablingStrategy strategy = getValue();
			if( strategy != null ){
				setDisabled( dockable, strategy.isDisabled( dockable, action ) );
			}
			dockables.add( dockable );
		}
		
		public void remove( Dockable dockable ){
			setDisabled( dockable, false );
			dockables.remove( dockable );
		}
		
		public boolean destroy(){
			if( dockables.isEmpty() ){
				setProperties( (DockController)null );
				return true;
			}
			return false;
		}
	}
}
