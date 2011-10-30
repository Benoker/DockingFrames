package bibliothek.gui.dock.toolbar.expand;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarDockStationListener;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.event.DockRegisterAdapter;
import bibliothek.gui.dock.event.DockRegisterListener;

/**
 * The default implementation of an {@link ExpandableToolbarItemStrategy} searches for {@link ToolbarGroupDockStation}s.
 * @author Benjamin Sigg
 */
public class DefaultExpandableToolbarItemStrategy implements ExpandableToolbarItemStrategy{
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
	private ToolbarDockStationListener toolbarListener = new ToolbarDockStationListener(){
		@Override
		public void shrunk( AbstractToolbarDockStation station ){
			for( ExpandableToolbarItemStrategyListener listener : listeners() ){
				listener.shrunk( station );
			}
		}
		
		@Override
		public void expanded( AbstractToolbarDockStation station ){
			for( ExpandableToolbarItemStrategyListener listener : listeners() ){
				listener.expanded( station );
			}
		}
	};
	
	protected void handleAdd( DockStation station ){
		if( station instanceof AbstractToolbarDockStation ){
			((AbstractToolbarDockStation)station).addToolbarDockStationListener( toolbarListener );
		}
	}
	
	protected void handleRemove( DockStation station ){
		if( station instanceof AbstractToolbarDockStation ){
			((AbstractToolbarDockStation)station).removeToolbarDockStationListener( toolbarListener );
		}
	}
	
	@Override
	public void install( DockController controller ){
		if( this.controller != null ){
			throw new IllegalStateException( "this strategy is already installed" );
		}
		this.controller = controller;
		DockRegister register = controller.getRegister();
		
		register.addDockRegisterListener( registerListener );
		for( int i = 0, n = register.getStationCount(); i<n; i++ ){
			handleAdd( register.getStation( i ));
		}
	}
	
	@Override
	public void uninstall( DockController controller ){
		if( this.controller != controller ){
			throw new IllegalStateException( "this strategy is not installed at '" + controller + "'" );
		}
		DockRegister register = controller.getRegister();
		register.removeDockRegisterListener( registerListener );
		
		for( int i = 0, n = register.getStationCount(); i<n; i++ ){
			handleRemove( register.getStation( i ) );
		}
	}
	
	@Override
	public boolean isExpandable( Dockable item ){
		return item instanceof ToolbarGroupDockStation;
	}

	@Override
	public boolean isExpanded( Dockable item ){
		return ((ToolbarGroupDockStation)item).isExpanded();
	}

	@Override
	public void setExpanded( Dockable item, boolean expanded ){
		if( expanded ){
			((ToolbarGroupDockStation)item).expand();
		}
		else{
			((ToolbarGroupDockStation)item).shrink();
		}
	}

	@Override
	public void addExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		if( listener == null ){
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
		return listeners.toArray( new ExpandableToolbarItemStrategyListener[ listeners.size() ] );
	}
}
