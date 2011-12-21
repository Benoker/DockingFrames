package bibliothek.gui.dock.station.toolbar.group;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.expand.SimpleExpandAction;
import bibliothek.gui.dock.toolbar.expand.SimpleExpandAction.Action;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This class uses the {@link ExpandableToolbarItemStrategy} to find out whether the items of some columns
 * can be expanded, and if so this class generates an appropriate {@link DockActionSource} containing actions
 * to expand or shrink all the items of one column.
 * @author Benjamin Sigg
 * @param <P> the type of object that represent a {@link Dockable}
 */
public abstract class ExpandToolbarGroupActions<P> extends AbstractToolbarGroupActions<P, ExpandToolbarGroupActions<P>.ExpandColumn> {
	/**
	 * The strategy that is currently used.
	 */
	private PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>( ExpandableToolbarItemStrategy.STRATEGY ){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue, ExpandableToolbarItemStrategy newValue ){
			if( oldValue != null ) {
				oldValue.removeExpandedListener( listener );
			}
			if( newValue != null ) {
				newValue.addExpandedListener( listener );
			}
		}
	};

	/**
	 * This listener is monitoring the current {@link #strategy}
	 */
	private ExpandableToolbarItemStrategyListener listener = new ExpandableToolbarItemStrategyListener(){
		@Override
		public void stretched( Dockable item ){
			update( item );
		}

		@Override
		public void shrunk( Dockable item ){
			update( item );
		}

		@Override
		public void expanded( Dockable item ){
			update( item );
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			update( item );
		}
		
		private void update( Dockable item ){
			ExpandColumn column = getColumn( item );
			if( column != null ){
				column.source.update();
			}
		}
	};
	
	/** the controller in whose realm this action is used */
	private DockController controller;

	/**
	 * Creates a new set of actions.
	 * @param controller the controller in whose realm this object is used
	 * @param station th station which uses this set of actions
	 */
	public ExpandToolbarGroupActions( DockController controller, ToolbarGroupDockStation station ){
		super( station );
		this.controller = controller;
		strategy.setProperties( controller );
	}

	public void destroy(){
		strategy.setProperties( (DockController) null );
	}

	@Override
	protected ExpandColumn createColumn( ToolbarColumn<P> column ){
		return new ExpandColumn( column );
	}

	/**
	 * Gets the strategy that is currently used to decide which actions are available for which {@link Dockable}s.
	 * @return the current strategy, can be <code>null</code>
	 */
	public ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	protected class ExpandColumn extends AbstractToolbarGroupActions<P, ExpandColumn>.Column {
		private ExpandSource source;

		public ExpandColumn( ToolbarColumn<P> column ){
			super( null );
			source = new ExpandSource( this );
			init( column );
		}

		@Override
		protected DockActionSource createSource(){
			return source;
		}

		@Override
		protected void inserted( int index, P item ){
			source.update();
		}

		@Override
		protected void removed( int index, P item ){
			source.update();
		}

		@Override
		protected void removed(){
			// nothing to do
		}
		
		/**
		 * Executes the action <code>action</code>, the exact behavior of this method depends
		 * also on the current {@link #getState() state}.
		 * @param action how to modify the state
		 */
		public void performAction( Action action ){
			boolean[] canPerform = getEnabledStates();
			ExpandedState current = getState();
			ExpandedState next;
			
			switch( action ){
				case LARGER:
					next = current.larger();
					break;
				case LARGEST:
					next = ExpandedState.EXPANDED;
					break;
				case SMALLER:
					next = current.smaller();
					break;
				case SMALLEST:
					next = ExpandedState.SHRUNK;
					break;
				default:
					throw new IllegalStateException( "never happens" );
			}
			
			int attempts = canPerform.length;
			while( attempts > 0 && current != next && !canPerform[ next.ordinal() ]){
				attempts--;
				switch( action ){
					case LARGER:
					case SMALLEST:
						next = next.larger();
						break;
					case LARGEST:
					case SMALLER:
						next = next.smaller();
						break;
				}
			}
			
			if( current != next && canPerform[ next.ordinal() ]){
				setState( next );
			}
		}
		
		public void setState( ExpandedState state ){
			ExpandableToolbarItemStrategy strategy = getStrategy();
			for( Dockable dockable : getDockables() ){
				strategy.setState( dockable, state );
			}
			source.update();
		}
		
		/**
		 * Gets the current {@link ExpandedState} of this column. The current state is always the
		 * state of the first child 
		 * @return the current state, not <code>null</code>
		 */
		public ExpandedState getState(){
			ToolbarColumn<P> column = getColumn();
			ExpandedState result = null;
			if( column.getDockableCount() > 0 ){
				ExpandableToolbarItemStrategy strategy = getStrategy();
				if( strategy != null ){
					for( int i = 0, n = column.getDockableCount(); i<n && result == null; i++){
						result = strategy.getState( column.getDockable( i ) );
					}
				}
			}
			if( result == null ){
				result = ExpandedState.SHRUNK;
			}
			return result;
		}
		
		/**
		 * Gets an array telling for each {@link ExpandedState} whether it is enabled or not.
		 * @return an array, a value of <code>true</code> indicates that an {@link ExpandedState}
		 * is enabled.
		 */
		public boolean[] getEnabledStates(){
			boolean[] canPerform = new boolean[ExpandedState.values().length];
			for( int i = 0; i < canPerform.length; i++ ) {
				canPerform[i] = true;
			}

			ExpandableToolbarItemStrategy strategy = getStrategy();
			if( strategy != null ) {
				for( Dockable dockable : getDockables() ) {
					for( ExpandedState state : ExpandedState.values() ) {
						if( !strategy.isEnabled( dockable, state ) ) {
							canPerform[state.ordinal()] = false;
						}
					}
				}
			}

			return canPerform;
		}
	}

	/**
	 * A {@link DockActionSource} that offers methods to update its content depending on the {@link ExpandedState} of
	 * the {@link Dockable}s of one {@link ExpandColumn}.
	 * @author Benjamin Sigg
	 */
	private class ExpandSource extends AbstractDockActionSource {
		private ExpandColumn column;
		private SimpleExpandAction[] actions;

		public ExpandSource( ExpandColumn column ){
			this.column = column;
		}
		
		/**
		 * Called if the <code>index</code>'th {@link SimpleExpandAction} is clicked.
		 * @param index the index of the action that was clicked
		 */
		private void onAction( int index ){
			column.performAction( actions[index].getBehavior() );
		}
		
		private void findEnabledActions(){
			boolean[] canPerform = column.getEnabledStates();
			
			int enabledCount = 0;
			for( boolean can : canPerform ){
				if( can ){
					enabledCount++;
				}
			}
			
			boolean canExpand = canPerform[ ExpandedState.EXPANDED.ordinal() ];
			boolean canShrink = canPerform[ ExpandedState.SHRUNK.ordinal() ];
			boolean canStretch = canPerform[ ExpandedState.STRETCHED.ordinal() ];
			
			int length = Math.max( 0, enabledCount-1 );
			
			if( actions == null || actions.length != length ){
				SimpleExpandAction[] next = new SimpleExpandAction[ length ];
				if( actions != null ){
					System.arraycopy( actions, 0, next, 0, Math.min( actions.length, next.length ) );
				}
				for( int i = 0; i < next.length; i++ ){
					if( next[i] == null ){
						next[i] = new SimpleExpandAction( controller, Action.LARGEST );
						
						final int index = i;
						next[i].addActionListener( new ActionListener(){
							@Override
							public void actionPerformed( ActionEvent e ){
								onAction( index );
							}
						});
					}
				}
				actions = next;
			}
			
			ExpandedState state = column.getState();
			if( canExpand && canShrink && canStretch ){
				switch( state ){
					case SHRUNK:
						actions[0].setBehavior( Action.LARGER );
						actions[1].setBehavior( Action.LARGEST );
						break;
					case STRETCHED:
						actions[0].setBehavior( Action.SMALLER );
						actions[1].setBehavior( Action.LARGER );
						break;
					case EXPANDED:
						actions[0].setBehavior( Action.SMALLEST );
						actions[1].setBehavior( Action.SMALLER );
						break;
				}
			}
			else if( canShrink && (canExpand || canStretch) ){
				switch( state ){
					case SHRUNK:
						actions[0].setBehavior( Action.LARGER );
						break;
					default:
						actions[0].setBehavior( Action.SMALLER );
						break;
				}
			}
			else if( canStretch && canExpand ){
				switch( state ){
					case EXPANDED:
						actions[0].setBehavior( Action.SMALLER );
						break;
					default:
						actions[0].setBehavior( Action.LARGER );
						break;
				}
			}
		}

		/**
		 * Updates the actions that are shown on this source.
		 */
		public void update(){
			if( hasListeners() ) {
				final int oldCount = getDockActionCount();
				findEnabledActions();
				final int newCount = getDockActionCount();

				if( oldCount != newCount ) {
					if( oldCount > 0 ) {
						fireRemoved( 0, oldCount - 1 );
					}
					findEnabledActions();
					if( newCount > 0 ) {
						fireAdded( 0, newCount - 1 );
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
			if( (index < 0) || (index >= getDockActionCount()) ) {
				throw new IllegalArgumentException( "index out of bounds" );
			}
			return actions[index];
		}

		@Override
		public int getDockActionCount(){
			if( !hasListeners() ) {
				findEnabledActions();
			}
			return actions.length;
		}

		@Override
		public LocationHint getLocationHint(){
			return new LocationHint( LocationHint.INDIRECT_ACTION, LocationHint.RIGHT );
		}
	}
}
