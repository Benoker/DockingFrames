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
 * This {@link ActionGuard} is responsible for adding the {@link AbstractGroupedExpandAction}
 * to {@link Dockable}s.
 * 
 * @author Benjamin Sigg
 */
public class ExpandedActionGuard implements ActionGuard{
	/** all the {@link ExpandSource}s that are currently used */
	private final Map<Dockable, ExpandSource> sources = new HashMap<Dockable, ExpandSource>();

	/** action added to enlarge {@link Dockable}s */
	private final DockAction largerAction;

	/** action added to shrink {@link Dockable}s */
	private final DockAction smallerAction;

	/** action added to switch {@link Dockable}s between two state */
	private final DockAction switchAction;

	/** where to show {@link #action} */
	private final LocationHint locationHint = new LocationHint(
			LocationHint.ACTION_GUARD, LocationHint.LITTLE_RIGHT);

	/** the controller in whose realm this guard is used */
	private final DockController controller;

	/**
	 * the currently used strategy to decide which items are expandable and
	 * expanded
	 */
	private final PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>(
			ExpandableToolbarItemStrategy.STRATEGY){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue,
				ExpandableToolbarItemStrategy newValue ){
			if (oldValue != null){
				for (final ExpandSource source : sources.values()){
					oldValue.removeExpandedListener(source);
				}
				oldValue.uninstall(controller);
			}
			if (newValue != null){
				newValue.install(controller);
				for (final ExpandSource source : sources.values()){
					newValue.addExpandedListener(source);
					source.update();
				}
			}
		}
	};

	/**
	 * Creates a new {@link ExpandedActionGuard}.
	 * 
	 * @param controller
	 *            the controller in whose realm this guard is used
	 */
	public ExpandedActionGuard( DockController controller ){
		this.controller = controller;
		strategy.setProperties(controller);

		largerAction = new LargeExpandAction(controller);
		smallerAction = new SmallExpandAction(controller);
		switchAction = new SwitchExpandAction(controller);
	}

	@Override
	public boolean react( Dockable dockable ){
		return true;
	}

	@Override
	public DockActionSource getSource( Dockable dockable ){
		return new ExpandSource(dockable);
	}

	/**
	 * Gets the currently used {@link ExpandableToolbarItemStrategy}.
	 * 
	 * @return the current strategy
	 */
	private ExpandableToolbarItemStrategy getStrategy(){
		return strategy.getValue();
	}

	/**
	 * Either adds or removes <code>source</code> as listener from the current
	 * {@link ExpandableToolbarItemStrategy}.
	 * 
	 * @param source
	 *            the source that should be added or removed as listener
	 * @param listening
	 *            whether the source is listening
	 */
	private void set( ExpandSource source, boolean listening ){
		if (listening){
			sources.put(source.dockable, source);
			getStrategy().addExpandedListener(source);
		} else{
			sources.remove(source.dockable);
			getStrategy().removeExpandedListener(source);
		}
	}

	/**
	 * This {@link DockActionSource} automatically adds or removes
	 * {@link ExpandedActionGuard#action} from itself depending on the current
	 * value of {@link ExpandableToolbarItemStrategy#isExpandable(Dockable)}.
	 * 
	 * @author Benjamin Sigg
	 */
	private class ExpandSource extends AbstractDockActionSource implements
			ExpandableToolbarItemStrategyListener{
		private final Dockable dockable;
		private int stateCount;

		/**
		 * Creates a new source.
		 * 
		 * @param dockable
		 *            the element for which the action is shown
		 */
		public ExpandSource( Dockable dockable ){
			this.dockable = dockable;
		}

		private void calculateStateCount(){
			stateCount = 0;
			final ExpandableToolbarItemStrategy strategy = getStrategy();
			for (final ExpandedState state : ExpandedState.values()){
				if (strategy.isEnabled(dockable, state)){
					stateCount++;
				}
			}
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state,
				boolean enabled ){
			if (item == dockable){
				update();
			}
		}

		/**
		 * Updates the actions that are shown on this source.
		 */
		public void update(){
			if (hasListeners()){
				final int oldCount = getDockActionCount();
				calculateStateCount();
				final int newCount = getDockActionCount();

				if (oldCount != newCount){
					stateCount = 0;
					if (oldCount > 0){
						fireRemoved(0, oldCount - 1);
					}
					calculateStateCount();
					if (newCount > 0){
						fireAdded(0, newCount - 1);
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
					return getDockAction(index++);
				}

				@Override
				public void remove(){
					throw new UnsupportedOperationException();
				}
			};
		}

		@Override
		public DockAction getDockAction( int index ){
			if ((index < 0) || (index >= getDockActionCount())){
				throw new IllegalArgumentException("index out of bounds");
			}
			if (stateCount == 2){
				return switchAction;
			} else{
				if (index == 0){
					return smallerAction;
				} else{
					return largerAction;
				}
			}
		}

		@Override
		public int getDockActionCount(){
			if (!hasListeners()){
				calculateStateCount();
			}
			switch (stateCount) {
			case 0:
			case 1:
				return 0;
			case 2:
				return 1;
			case 3:
				return 2;
			default:
				return 0;
			}
		}

		@Override
		public LocationHint getLocationHint(){
			return locationHint;
		}

		@Override
		public void addDockActionSourceListener(
				DockActionSourceListener listener ){
			if (!hasListeners()){
				set(this, true);
				calculateStateCount();
			}
			super.addDockActionSourceListener(listener);
		}

		@Override
		public void removeDockActionSourceListener(
				DockActionSourceListener listener ){
			super.removeDockActionSourceListener(listener);
			if (!hasListeners()){
				set(this, false);
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

		@Override
		public void stretched( Dockable item ){
			// ignore
		}
	}
}
