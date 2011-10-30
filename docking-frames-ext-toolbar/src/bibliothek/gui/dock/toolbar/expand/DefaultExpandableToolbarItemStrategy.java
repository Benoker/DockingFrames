package bibliothek.gui.dock.toolbar.expand;

import java.util.ArrayList;

import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * The default implementation of an {@link ExpandableToolbarItemStrategy} searches for {@link ToolbarGroupDockStation}s.
 * @author Benjamin Sigg
 */
public class DefaultExpandableToolbarItemStrategy implements ExpandableToolbarItemStrategy{
	/** All the listeners that are registered */
	private List<ExpandableToolbarItemStrategyListener> listeners = new ArrayList<ExpandableToolbarItemStrategyListener>();
	
	@Override
	public boolean isExpandable( Dockable item ){
		return item instanceof ToolbarGroupDockStation;
	}

	@Override
	public boolean isExpanded( Dockable item ){
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setExpanded( Dockable item, boolean expanded ){
		// TODO Auto-generated method stub
		
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
	
}
