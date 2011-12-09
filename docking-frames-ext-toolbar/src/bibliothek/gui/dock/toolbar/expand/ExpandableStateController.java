package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.event.DockHierarchyEvent;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.PropertyValue;

/**
 * The {@link ExpandableStateController} is a helper class intended for
 * {@link ExpandableToolbarItem}s, it finds the first parent of a
 * {@link ExpandableToolbarItem} which is acknowledged by the current
 * {@link ExpandableToolbarItemStrategy} and changes the {@link ExpandedState}
 * of the item to the {@link ExpandedState} of the parent.
 * 
 * @author Benjamin Sigg
 */
public class ExpandableStateController{
	/** the observed item */
	private final ExpandableToolbarItem item;

	/** the currently observed controller */
	private DockController controller;

	/** the current strategy */
	private final PropertyValue<ExpandableToolbarItemStrategy> strategy = new PropertyValue<ExpandableToolbarItemStrategy>(
			ExpandableToolbarItemStrategy.STRATEGY){
		@Override
		protected void valueChanged( ExpandableToolbarItemStrategy oldValue,
				ExpandableToolbarItemStrategy newValue ){
			if (oldValue != null){
				oldValue.removeExpandedListener(strategyListener);
			}
			if ((newValue != null) && (controller != null)){
				newValue.addExpandedListener(strategyListener);
			}
			refresh();
		}
	};

	private final ExpandableToolbarItemStrategyListener strategyListener = new ExpandableToolbarItemStrategyListener(){
		@Override
		public void stretched( Dockable item ){
			if (DockUtilities.isAncestor(item, getItem())){
				refresh();
			}
		}

		@Override
		public void shrunk( Dockable item ){
			if (DockUtilities.isAncestor(item, getItem())){
				refresh();
			}
		}

		@Override
		public void expanded( Dockable item ){
			if (DockUtilities.isAncestor(item, getItem())){
				refresh();
			}
		}

		@Override
		public void enablementChanged( Dockable item, ExpandedState state,
				boolean enabled ){
			// ignore
		}
	};

	/**
	 * Creates a new controller.
	 * 
	 * @param item
	 *            the item to observe
	 */
	public ExpandableStateController( ExpandableToolbarItem item ){
		this.item = item;

		item.addDockHierarchyListener(new DockHierarchyListener(){
			@Override
			public void hierarchyChanged( DockHierarchyEvent event ){
				refresh();
			}

			@Override
			public void controllerChanged( DockHierarchyEvent event ){
				controller = getItem().getController();
				strategy.setProperties(controller);
			}
		});
		strategy.setProperties(getItem().getController());
		refresh();
	}

	/**
	 * Gets the item which is observed by this controller.
	 * 
	 * @return the observed item, not <code>null</code>
	 */
	public ExpandableToolbarItem getItem(){
		return item;
	}

	/**
	 * Searches the first parent of {@link #getItem() the item} which is
	 * acknowledged by the current {@link ExpandableToolbarItemStrategy} and
	 * updates the {@link ExpandedState} of the item such that it has the same
	 * state as its parent.
	 */
	public void refresh(){
		Dockable current = item;
		final ExpandableToolbarItemStrategy strategy = this.strategy.getValue();
		if (strategy != null){
			while (current != null){
				final ExpandedState state = strategy.getState(current);
				if (state != null){
					item.setExpandedState(state);
					return;
				}

				final DockStation station = current.getDockParent();
				if (station != null){
					current = station.asDockable();
				} else{
					current = null;
				}
			}
		}
	}
}
