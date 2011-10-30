package bibliothek.gui.dock.toolbar.expand;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This {@link ActionGuard} is responsible for adding the {@link ExpandAction} to
 * {@link Dockable}s.
 * @author Benjamin Sigg
 */
public class ExpandedActionGuard implements ActionGuard{
	/** all the {@link ExpandSource}s that are currently used */
	private Map<Dockable, ExpandSource> sources = new HashMap<Dockable, ExpandSource>();
	
	/** the action that is added to all {@link Dockable}s */
	private ExpandAction action;

	/** where to show {@link #action} */
	private LocationHint locationHint = new LocationHint( LocationHint.ACTION_GUARD, LocationHint.LITTLE_RIGHT );
	
	/** the currently used strategy to decide which items are expandable and expanded */
	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ){
				for( ExpandSource source : sources.values() ){
					oldValue.removeExpandedListener( source );
				}
			}
			if( newValue != null ){
				for( ExpandSource source : sources.values() ){
					newValue.addExpandedListener( source );
					source.expandableChanged( source.dockable, newValue.isExpandable( source.dockable ) );
				}
			}
		}
	};
	
	public ExpandedActionGuard( DockController controller ){
		strategy.setProperties( controller );
		action = new ExpandAction( controller );
	}
	
	@Override
	public boolean react( Dockable dockable ){
		return true;
	}
	
	@Override
	public DockActionSource getSource( Dockable dockable ){
		return new ExpandSource( dockable );
	}
	
	/**
	 * Gets the currently used {@link ExpandableToolbarItemStrategy}.
	 * @return the current strategy
	 */
	private ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}
	
	/**
	 * Either adds or removes <code>source</code> as listener from the current
	 * {@link ExpandableToolbarItemStrategy}.
	 * @param source the source that should be added or removed as listener
	 * @param listening whether the source is listening
	 */
	private void set( ExpandSource source, boolean listening ){
		if( listening ){
			sources.put( source.dockable, source );
			getStrategy().addExpandedListener( source );
		}
		else{
			sources.remove( source.dockable );
			getStrategy().removeExpandedListener( source );
		}
	}
	
	/**
	 * This {@link DockActionSource} automatically adds or removes {@link ExpandedActionGuard#action} from itself
	 * depending on the current value of {@link ExpandableToolbarItemStrategy#isExpandable(Dockable)}.
	 * @author Benjamin Sigg
	 */
	private class ExpandSource extends AbstractDockActionSource implements ExpandableToolbarItemStrategyListener{
		private Dockable dockable;
		private boolean showing = false;
		
		/**
		 * Creates a new source.
		 * @param dockable the element for which the action is shown
		 */
		public ExpandSource( Dockable dockable ){
			this.dockable = dockable;
		}
		
		@Override
		public void expandableChanged( Dockable item, boolean expandable ){
			if( item == dockable ){
				if( showing != expandable ){
					showing = expandable;
					if( showing ){
						fireAdded( 0, 0 );
					}
					else{
						fireRemoved( 0, 0 );
					}
				}
			}
		}
		
		@Override
		public Iterator<DockAction> iterator(){
			return new Iterator<DockAction>(){
				private int index = 0;
				
				@Override
				public boolean hasNext(){
					return index < getDockActionCount();
				}
				
				@Override
				public DockAction next(){
					return getDockAction( index++ );
				}
				
				@Override
				public void remove(){
					throw new UnsupportedOperationException();	
				}
			};
		}
		
		@Override
		public DockAction getDockAction( int index ){
			if( index < 0 || index >= getDockActionCount() ){
				throw new IllegalArgumentException( "index out of bounds" );
			}
			return action;
		}
		
		@Override
		public int getDockActionCount(){
			if( hasListeners() ){
				if( showing ){
					return 1;
				}
				else{
					return 0;
				}
			}
			if( getStrategy().isExpandable( dockable )){
				return 1;
			}
			else{
				return 0;
			}
		}
		
		@Override
		public LocationHint getLocationHint(){
			return locationHint;
		}
		
		@Override
		public void addDockActionSourceListener( DockActionSourceListener listener ){
			if( !hasListeners() ){
				set( this, true );
				showing = getStrategy().isExpandable( dockable );
			}
			super.addDockActionSourceListener( listener );
		}
		
		@Override
		public void removeDockActionSourceListener( DockActionSourceListener listener ){
			super.removeDockActionSourceListener( listener );
			if( !hasListeners() ){
				set( this, false );
			}
		}
		
		@Override
		public void expanded( Dockable item ){
			// ignore
		}
		
		@Override
		public void shrunk( Dockable item ){
			// ignore
		}
		
	}
}
