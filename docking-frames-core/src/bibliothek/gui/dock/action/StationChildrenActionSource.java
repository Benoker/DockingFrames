/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.actions.SimpleSelectableAction;
import bibliothek.gui.dock.control.focus.DefaultFocusRequest;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockableListener;
import bibliothek.gui.dock.event.SelectableDockActionListener;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.container.Tuple;

/**
 * This {@link DockActionSource} is a wrapper around a {@link Dockable} or a {@link DockStation}
 * and adds one {@link DockAction} for each child of the station to itself. Subclasses may implement
 * a filter to show more or less actions.
 * @author Benjamin Sigg
 */
public class StationChildrenActionSource extends AbstractDockActionSource{
	private LocationHint hint;
	private Dockable dockable;
	private Listener listener;
	
	private boolean onUpdateList = false;
	private List<Tuple<Dockable, DockAction>> actions = new ArrayList<Tuple<Dockable,DockAction>>();
	
	/**
	 * Creates a new action source.
	 * @param dockable the dockable or station whose children to show, not <code>null</code>
	 * @param hint the preferred location of this {@link DockActionSource}, may be <code>null</code>
	 */
	public StationChildrenActionSource( Dockable dockable, LocationHint hint ){
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		this.dockable = dockable;
		if( hint == null ){
			hint = LocationHint.UNKNOWN;
		}
		this.hint = hint;
	}
	
	/**
	 * Gets the dockable or station which is managed by this source.
	 * @return the dockable, not <code>null</code>
	 */
	public Dockable getDockable(){
		return dockable;
	}

	public LocationHint getLocationHint(){
		return hint;
	}
	
	public DockAction getDockAction( int index ){
		updateActionList( false );
		return actions.get( index ).getB();
	}

	public int getDockActionCount(){
		updateActionList( false );
		return actions.size();
	}

	public Iterator<DockAction> iterator(){
		updateActionList( false );
		return new Iterator<DockAction>() {
			private Iterator<Tuple<Dockable, DockAction>> iterator = actions.iterator();
			
			public boolean hasNext(){
				return iterator.hasNext();
			}
			
			public DockAction next(){
				return iterator.next().getB();
			}
			
			public void remove(){
				throw new UnsupportedOperationException();	
			}
		};
	}

	@Override
	public void addDockActionSourceListener( DockActionSourceListener listener ){
		if( this.listener == null ){
			updateActionList( false );
		}
		super.addDockActionSourceListener( listener );
		if( this.listener == null ){
			this.listener = new Listener();
			this.listener.add( dockable );
		}
	}
	
	@Override
	public void removeDockActionSourceListener( DockActionSourceListener listener ){
		super.removeDockActionSourceListener( listener );
		if( listeners.size() == 0 && this.listener != null ){
			this.listener.remove( dockable );
			this.listener = null;
		}
	}
	
	/**
	 * Rebuilds the list of actions, introducing new actions if necessary, removing
	 * old actions if no longer needed.
	 * @param force whether the entire list has to be updated or optimizations are allowed
	 */
	private void updateActionList( boolean force ){
		if( onUpdateList ){
			return;
		}
		try{
			onUpdateList = true;
			if( listener != null && !force ){
				return;
			}
			
			List<Dockable> list = new ArrayList<Dockable>();
			fill( dockable, list );
			sort( list );
			
			// assume the list does not change much, i.e. an item was added or removed
			int i = 0, n = list.size();
			int j = 0, m = actions.size();
			
			Set<Dockable> pendingActions = new HashSet<Dockable>();
			for( Tuple<Dockable,DockAction> item : actions ){
				pendingActions.add( item.getA() );
			}
			
			while( i < n && j < m ){
				Dockable dockable = list.get( i );
				if( actions.get( j ).getA() == dockable ){
					i++;
					j++;
				}
				else if( pendingActions.contains( dockable ) ){
					actions.remove( j );
					m--;
					fireRemoved( j, j );
				}
				else{
					actions.add( j, new Tuple<Dockable, DockAction>( dockable, createActionFor( dockable )));
					m++;
					fireAdded( j, j );
					i++;
					j++;
				}
				
				pendingActions.remove( dockable );
			}

			if( j < m ){
				int length = m;
				while( j < m ){
					actions.remove( --m );
				}
				fireRemoved( j, length-1 );
			}
			
			if( i < n ){
				int index = i;
				while( i < n ){
					Dockable dockable = list.get( i++ );
					actions.add( new Tuple<Dockable, DockAction>( dockable, createActionFor( dockable )));
				}
				fireAdded( index, actions.size()-1 );
			}
		}
		finally{
			onUpdateList = false;
		}
	}
	
	private void fill( Dockable dockable, List<Dockable> list ){
		if( shouldShow( dockable )){
			list.add( dockable );
		}
		DockStation station = dockable.asDockStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
				fill( station.getDockable( i ), list );
			}
		}
	}
	
	/**
	 * Creates the {@link DockAction} which is shown for <code>dockable</code>. The
	 * default behavior is to create a {@link ButtonDockAction} which can be pressed
	 * and will transfer the focus to <code>dockable</code>.
	 * @param dockable the item for which an action is required
	 * @return the new action, not <code>null</code>
	 */
	protected DockAction createActionFor( Dockable dockable ){
		return new FocusAction( dockable );
	}
	
	/**
	 * Puts an order in the dockables, telling which items to show when. The default behavior
	 * is to keep the current order (which is the order given by the tree of {@link DockStation}s
	 * and {@link Dockable}s). Subclasses may also modify the list by adding or removing items, 
	 * although a filter is better implemented by overriding {@link #shouldShow(Dockable)}
	 * @param dockables the array to order
	 */
	protected void sort( List<Dockable> dockables ){
		// nothing
	}
	
	/**
	 * Tells which children to show and which not. This method is called with all children (direct
	 * and indirect) of the station. The default behavior is to return <code>true</code> for 
	 * any direct child or <code>true</code> if the monitored dockable is no station at all.
	 * @param dockable the child to check
	 * @return <code>true</code> if there should be a button, <code>false</code> otherwise
	 */
	protected boolean shouldShow( Dockable dockable ){
		if( dockable.getDockParent() == getDockable().asDockStation() ){
			return true;
		}
		if( dockable == getDockable() && dockable.asDockStation() == null ){
			return true;
		}
		return false;
	}
	
	/**
	 * An action that can transfer the focus
	 * @author Benjamin Sigg
	 */
	protected class FocusAction extends SimpleSelectableAction.Check implements DockableListener{
		private Dockable dockable;
		private DockStation parent;
		
		private boolean onChange = false;
		private int bound = 0;
		
		private DockStationAdapter adapter = new DockStationAdapter(){
			@Override
			public void dockableSelected( DockStation station, Dockable oldSelection, Dockable newSelection ){
				checkState();
			}
			@Override
			public void dockableShowingChanged( DockStation station, Dockable dockable, boolean visible ){
				checkState();
			}
		};
		
		private DockHierarchyListener hierarchy = new DockHierarchyListener(){
			public void hierarchyChanged( DockHierarchyEvent event ){
				if( bound > 0 ){
					if( parent != null ){
						parent.removeDockStationListener( adapter );
					}
					parent = dockable.getDockParent();
					if( parent != null ){
						parent.addDockStationListener( adapter );
					}
					checkState();
				}
			}
			
			public void controllerChanged( DockHierarchyEvent event ){
				// ignore
			}
		};
		
		/**
		 * Creates a new action
		 * @param dockable the element to observe
		 */
		public FocusAction( Dockable dockable ){
			this.dockable = dockable;
			setDockableRepresentation( dockable );
			
			addSelectableListener( new SelectableDockActionListener(){
				public void selectedChanged( SelectableDockAction action, Set<Dockable> dockables ){
					checkDockable();
				}
			});
		}
		
		@Override
		public void bind( Dockable dockable ){
			bound++;
			if( bound == 1 ){
				this.dockable.addDockHierarchyListener( hierarchy );
				parent = this.dockable.getDockParent();
				
				if( parent != null ){
					parent.addDockStationListener( adapter );
				}
				
				checkState();
			}
			super.bind( dockable );
		}
		
		@Override
		public void unbind( Dockable dockable ){
			super.unbind( dockable );
			bound--;
			if( bound == 0 ){
				if( parent != null ){
					parent.removeDockStationListener( adapter );
				}
				this.dockable.removeDockHierarchyListener( hierarchy );
				parent = null;
			}
		}
		
		private void checkState(){
			if( !onChange ){
				try{
					onChange = true;
					
					DockStation parent = dockable.getDockParent();
					boolean select = false;
					
					if( parent != null ){
						select = parent.isChildShowing( dockable ) && parent.getFrontDockable() == dockable;
					}
					
					setSelected( select );
				}
				finally{
					onChange = false;
				}
			}
		}
		
		private void checkDockable(){
			if( !onChange ){
				try{
					onChange = true;
					
					if( isSelected() ){
						DockController controller = dockable.getController();
						if( controller != null ){
							controller.setFocusedDockable( new DefaultFocusRequest( dockable, null, true, true, true ));
						}
					}
					else{
						DockStation parent = this.parent;
						Dockable dockable = this.dockable;
						
						DockStation finalParent = StationChildrenActionSource.this.dockable.getDockParent();
						
						while( parent != null ){
							if( parent.getFrontDockable() == dockable ){
								parent.setFrontDockable( null );
							}
							if( parent == finalParent ){
								parent = null;
							}
							else{
								dockable = parent.asDockable();
								if( dockable != null ){
									parent = dockable.getDockParent();
								}
								else{
									parent = null;
								}
							}
						}
					}
				}
				finally{
					onChange = false;
				}
			}
		}
		
		protected void bound(Dockable dockable){
			this.dockable.addDockableListener( this );
			setIcon( this.dockable.getTitleIcon() );
			setText( this.dockable.getTitleText() );
			setTooltip( this.dockable.getTitleToolTip() );
		}
		
		protected void unbound( Dockable dockable ){
			this.dockable.removeDockableListener( this );
		}
		
		public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ){
			setIcon( newIcon );
		}
		
		public void titleTextChanged( Dockable dockable, String oldTitle, String newTitle ){
			setText( newTitle );
			String tooltip = dockable.getTitleToolTip();
			if( tooltip == null || tooltip.length() == 0 ){
				setTooltip( newTitle );
			}
		}
		
		public void titleToolTipChanged( Dockable dockable, String oldToolTip, String newToolTip ){
			setTooltip( newToolTip );
			if( newToolTip == null || newToolTip.length() == 0 ){
				setTooltip( dockable.getTitleText() );
			}
		}
		
		public void titleBound( Dockable dockable, DockTitle title ){
			// ignore
		}
		
		public void titleExchanged( Dockable dockable, DockTitle title ){
			// ignore
		}
		
		public void titleUnbound( Dockable dockable, DockTitle title ){
			// ignore	
		}
	}
	
	/**
	 * The listener added to all {@link DockStation}s.
	 * @author Benjamin Sigg
	 */
	private class Listener extends DockStationAdapter{
		public void dockableAdded( DockStation station, Dockable dockable ){
			add( dockable );
			updateActionList( true );
		}
		
		public void dockableRemoving( DockStation station, Dockable dockable ){
			remove( dockable );
		}
		
		public void dockableRemoved( DockStation station, Dockable dockable ){
			updateActionList( true );
		}
		
		public void dockablesRepositioned( DockStation station, Dockable[] dockables ){
			updateActionList( true );
		}
		
		public void add( Dockable dockable ){
			DockStation station = dockable.asDockStation();
			if( station != null ){
				station.addDockStationListener( this );
				for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
					add( station.getDockable( i ));
				}
			}
		}
		
		public void remove( Dockable dockable ){
			DockStation station = dockable.asDockStation();
			if( station != null ){
				station.removeDockStationListener( this );
				for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
					remove( station.getDockable( i ));
				}
			}
		}
	}
}
