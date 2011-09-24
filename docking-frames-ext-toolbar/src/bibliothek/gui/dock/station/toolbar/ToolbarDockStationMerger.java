package bibliothek.gui.dock.station.toolbar;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * A {@link Merger} for merging two {@link ToolbarDockStation}s.
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarDockStationMerger implements Merger {
	@Override
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
		return (operation == null || !operation.isMove()) && parent instanceof ToolbarDockStation && child instanceof ToolbarDockStation;
	}

	public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
		merge( (ToolbarDropInfo<?>)operation, (ToolbarDockStation)parent, (ToolbarDockStation)child );
	}
	
	public void merge( ToolbarDropInfo<?> operation, ToolbarDockStation parent, ToolbarDockStation child ){
		// WARNING: if I don't do a copy of dockables, problem occurs.
		// Perhaps due to concurrent access to the dockable (drop in
		// goal area ==> drag in origin area)?
		int count = child.getDockableCount();
		List<Dockable> insertDockables = new ArrayList<Dockable>();
		for( int i = 0; i < count; i++ ) {
			insertDockables.add( child.getDockable( i ) );
		}
		
		int index = operation.getIndex( ReferencePoint.BOTTOMRIGHT );
		
		for( int i = 0; i < count; i++ ) {
			parent.drop( insertDockables.get( i ), index++ );
		}
	}
}
