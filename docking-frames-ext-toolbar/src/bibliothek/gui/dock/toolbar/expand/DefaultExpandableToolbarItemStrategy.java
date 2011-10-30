package bibliothek.gui.dock.toolbar.expand;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * The default implementation of an {@link ExpandableToolbarItemStrategy} searches for {@link ToolbarGroupDockStation}s.
 * @author Benjamin Sigg
 */
public class DefaultExpandableToolbarItemStrategy implements ExpandableToolbarItemStrategy {
	/** All the listeners that are registered */
	private List<ExpandableToolbarItemStrategyListener> listeners = new ArrayList<ExpandableToolbarItemStrategyListener>();

	/** the controller in whose realm this strategy is used */
	private DockController controller;

	/** this listener is used to detect new station that need to be observed */
	private DockRegisterListener registerListener = new DockRegisterAdapter(){
		@Override
		public void dockStationRegistered( DockController controller, DockStation station ){
			handleAdd( station );
		}

		@Override
		public void dockStationUnregistered( DockController controller, DockStation station ){
			handleRemove( station );
		}
	};

	/**
	 * A listener added to all {@link AbstractToolbarDockStation}s.
	 */
	private ExpandableToolbarItemListener expandableListener = new ExpandableToolbarItemListener(){
		@Override
		public void changed( ExpandableToolbarItem item, ExpandedState oldState, ExpandedState newState ){
			switch( newState ){
				case EXPANDED:
					for( ExpandableToolbarItemStrategyListener listener : listeners() ) {
						listener.expanded( item );
					}
					break;
				case SHRUNK:
					for( ExpandableToolbarItemStrategyListener listener : listeners() ) {
						listener.shrunk( item );
					}
					break;
				case STRETCHED:
					for( ExpandableToolbarItemStrategyListener listener : listeners() ) {
						listener.stretched( item );
					}
					break;
			}
		}
	};

	protected void handleAdd( DockStation station ){
		if( station instanceof AbstractToolbarDockStation ) {
			((AbstractToolbarDockStation) station).addExpandableListener( expandableListener );
		}
	}

	protected void handleRemove( DockStation station ){
		if( station instanceof AbstractToolbarDockStation ) {
			((AbstractToolbarDockStation) station).addExpandableListener( expandableListener );
		}
	}

	@Override
	public void install( DockController controller ){
		if( this.controller != null ) {
			throw new IllegalStateException( "this strategy is already installed" );
		}
		this.controller = controller;
		DockRegister register = controller.getRegister();

		register.addDockRegisterListener( registerListener );
		for( int i = 0, n = register.getStationCount(); i < n; i++ ) {
			handleAdd( register.getStation( i ) );
		}
	}

	@Override
	public void uninstall( DockController controller ){
		if( this.controller != controller ) {
			throw new IllegalStateException( "this strategy is not installed at '" + controller + "'" );
		}
		DockRegister register = controller.getRegister();
		register.removeDockRegisterListener( registerListener );

		for( int i = 0, n = register.getStationCount(); i < n; i++ ) {
			handleRemove( register.getStation( i ) );
		}
	}

	@Override
	public boolean isEnabled( Dockable item, ExpandedState state ){
		return item instanceof ToolbarGroupDockStation;
	}

	@Override
	public ExpandedState getState( Dockable item ){
		return ((ToolbarGroupDockStation) item).getExpandedState();
	}

	@Override
	public void setState( Dockable item, ExpandedState state ){
		((ToolbarGroupDockStation) item).setExpandedState( state );
	}

	@Override
	public void addExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		if( listener == null ) {
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	@Override
	public void removeExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		listeners.remove( listener );
	}

	/**
	 * Gets all the {@link ExpandableToolbarItemStrategyListener}s that are currently registered.
	 * @return all the listeners
	 */
	protected ExpandableToolbarItemStrategyListener[] listeners(){
		return listeners.toArray( new ExpandableToolbarItemStrategyListener[listeners.size()] );
	}
}
