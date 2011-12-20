package bibliothek.gui.dock.station.toolbar.group;

import java.util.Iterator;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.action.AbstractDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.LocationHint.Origin;
import bibliothek.gui.dock.event.DockActionSourceListener;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.expand.LargeExpandAction;
import bibliothek.gui.dock.toolbar.expand.SmallExpandAction;
import bibliothek.gui.dock.toolbar.expand.SwitchExpandAction;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * This class uses the {@link ExpandableToolbarItemStrategy} to find out whether the items of some columns
 * can be expanded, and if so this class generates an appropriate {@link DockActionSource} containing actions
 * to expand or shrink all the items of one column.
 * @author Benjamin Sigg
 */
public class ExpandToolbarGroupActions extends AbstractToolbarGroupActions<ExpandToolbarGroupActions.ExpandColumn> {
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
			// TODO Auto-generated method stub

		}

		@Override
		public void shrunk( Dockable item ){
			// TODO Auto-generated method stub

		}

		@Override
		public void expanded( Dockable item ){
			// TODO Auto-generated method stub

		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state, boolean enabled ){
			// TODO Auto-generated method stub

		}
	};
	
	/** the controller in whose realm this action is used */
	private DockController controller;

	/**
	 * Creates a new set of actions.
	 * @param controller the controller in whose realm this object is used
	 */
	public ExpandToolbarGroupActions( DockController controller ){
		this.controller = controller;
		strategy.setProperties( controller );
	}

	public void destroy(){
		strategy.setProperties( (DockController) null );
	}

	@Override
	protected ExpandColumn createColumn( ToolbarColumn column ){
		return new ExpandColumn( column );
	}

	/**
	 * Gets the strategy that is currently used to decide which actions are available for which {@link Dockable}s.
	 * @return the current strategy, can be <code>null</code>
	 */
	public ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	protected class ExpandColumn extends AbstractToolbarGroupActions<ExpandColumn>.Column {
//		private ExpandSource source;

		public ExpandColumn( ToolbarColumn column ){
			super( column );
//			source = new ExpandSource( this );
		}

		@Override
		protected DockActionSource createSource(){
			return null; // pending
//			return source;
		}

		@Override
		protected void inserted( int index, Dockable dockable ){
//			source.update();
		}

		@Override
		protected void removed( int index, Dockable dockable ){
//			source.update();
		}

		@Override
		protected void removed(){
			// nothing to do
		}
	}

//	/**
//	 * A {@link DockActionSource} that offers methods to update its content depending on the {@link ExpandedState} of
//	 * the {@link Dockable}s of one {@link ExpandColumn}.
//	 * @author Benjamin Sigg
//	 */
//	private class ExpandSource extends AbstractDockActionSource {
//		private ExpandColumn column;
//		
//
//		public ExpandSource( ExpandColumn column ){
//			this.column = column;
//		}
//
//		private void findEnabledActions(){
//			boolean[] canPerform = new boolean[ExpandedState.values().length];
//			for( int i = 0; i < canPerform.length; i++ ) {
//				canPerform[i] = true;
//			}
//
//			ExpandableToolbarItemStrategy strategy = getStrategy();
//			if( strategy != null ) {
//				for( Dockable dockable : column.getDockables() ) {
//					for( ExpandedState state : ExpandedState.values() ) {
//						if( !strategy.isEnabled( dockable, state ) ) {
//							canPerform[state.ordinal()] = false;
//						}
//					}
//				}
//			}
//
//			int enabledCount = 0;
//			for( boolean can : canPerform ){
//				if( can ){
//					enabledCount++;
//				}
//			}
//		}
//
//		/**
//		 * Updates the actions that are shown on this source.
//		 */
//		public void update(){
//			if( hasListeners() ) {
//				final int oldCount = getDockActionCount();
//				findEnabledActions();
//				final int newCount = getDockActionCount();
//
//				if( oldCount != newCount ) {
//					if( oldCount > 0 ) {
//						fireRemoved( 0, oldCount - 1 );
//					}
//					findEnabledActions();
//					if( newCount > 0 ) {
//						fireAdded( 0, newCount - 1 );
//					}
//				}
//			}
//		}
//
//		@Override
//		public Iterator<DockAction> iterator(){
//			return new Iterator<DockAction>(){
//				private int index = 0;
//
//				@Override
//				public boolean hasNext(){
//					return index < getDockActionCount();
//				}
//
//				@Override
//				public DockAction next(){
//					return getDockAction( index++ );
//				}
//
//				@Override
//				public void remove(){
//					throw new UnsupportedOperationException();
//				}
//			};
//		}
//
//		@Override
//		public DockAction getDockAction( int index ){
//			if( (index < 0) || (index >= getDockActionCount()) ) {
//				throw new IllegalArgumentException( "index out of bounds" );
//			}
//			if( stateCount == 2 ) {
//				return switchAction;
//			}
//			else {
//				if( index == 0 ) {
//					return smallerAction;
//				}
//				else {
//					return largerAction;
//				}
//			}
//		}
//
//		@Override
//		public int getDockActionCount(){
//			if( !hasListeners() ) {
//				findEnabledActions();
//			}
//			switch( stateCount ){
//				case 0:
//				case 1:
//					return 0;
//				case 2:
//					return 1;
//				case 3:
//					return 2;
//				default:
//					return 0;
//			}
//		}
//
//		@Override
//		public LocationHint getLocationHint(){
//			return new LocationHint( LocationHint.INDIRECT_ACTION, LocationHint.RIGHT );
//		}
//	}
}
