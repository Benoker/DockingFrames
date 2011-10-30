package bibliothek.gui.dock;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandableToolbarItemStrategyListener;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;

/**
 * An {@link ExpandableToolbarItemStrategy} is a strategy that allows to expand and
 * to shrink items of a toolbar.
 * @author Benjamin Sigg
 */
public interface ExpandableToolbarItemStrategy {
	/** an identifier to exchange the strategy */
	public static final PropertyKey<ExpandableToolbarItemStrategy> STRATEGY = 
			new PropertyKey<ExpandableToolbarItemStrategy>( "expandable toolbar item strategy", new DynamicPropertyFactory<ExpandableToolbarItemStrategy>(){
				@Override
				public ExpandableToolbarItemStrategy getDefault( PropertyKey<ExpandableToolbarItemStrategy> key, DockProperties properties ){
					return new DefaultExpandableToolbarItemStrategy();
				}
			}, true );
	
	/**
	 * Called if this strategy is used by <code>controller</code>.
	 * @param controller the controller using this strategy
	 */
	public void install( DockController controller );
	
	/**
	 * Called if this strategy is no longer used by <code>controller</code>.
	 * @param controller the controller which is no longer using this strategy
	 */
	public void uninstall( DockController controller );
	
	/**
	 * Tells whether the {@link Dockable} <code>item</code> can be expanded or shrunk.
	 * @param item the item which is to be tested
	 * @return <code>true</code> if this strategy knows how to handle <code>item</code>
	 */
	public boolean isExpandable( Dockable item );
	
	/**
	 * Tells whether this item is in its "large" state.
	 * @param item the item whose state is requested
	 * @return <code>true</code> if this item is large
	 */
	public boolean isExpanded( Dockable item );
	
	/**
	 * Tells the <code>item</code> to show up in its "large" state.
	 * @param item the item whose state should be changed 
	 * @param expanded <code>true</code> if this item should make itself
	 * large, <code>false</code> if it should make itself small
	 */
	public void setExpanded( Dockable item, boolean expanded );
	
	/**
	 * Adds a listener to this strategy, the listener is to be informed if the state of
	 * an item changes.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addExpandedListener( ExpandableToolbarItemStrategyListener listener );
	
	/**
	 * Removes a listener from this strategy.
	 * @param listener the listener to remove
	 */
	public void removeExpandedListener( ExpandableToolbarItemStrategyListener listener );
}
