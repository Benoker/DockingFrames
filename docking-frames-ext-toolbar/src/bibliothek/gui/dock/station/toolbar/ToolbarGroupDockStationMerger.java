package bibliothek.gui.dock.station.toolbar;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm that allows to merge {@link ToolbarGroupDockStation}s together.
 * @author Herve Guillaume
 * @author Benjamin Sigg
 */
public class ToolbarGroupDockStationMerger implements Merger{
	@Override
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
		return (operation == null || !operation.isMove()) && parent instanceof ToolbarGroupDockStation && child instanceof ToolbarGroupDockStation;
	}
	
	@Override
	public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
		merge( (ToolbarDropInfo<?>)operation, (ToolbarGroupDockStation)parent, (ToolbarGroupDockStation)child );	
	}
	
	public void merge( ToolbarDropInfo<?> operation, ToolbarGroupDockStation parent, ToolbarGroupDockStation child ){
		// WARNING: if I don't do a copy of dockables, problem occurs.
		// Perhaps due to concurrent access to the dockable (drop in
		// goal area ==> drag in origin area)?
		
		int count = child.getDockableCount();
		List<Dockable> insertDockables = new ArrayList<Dockable>();
		for( int i = 0; i < count; i++ ) {
			insertDockables.add( child.getDockable( i ) );
		}
		
//		int index = operation.getIndex( ReferencePoint.BOTTOMRIGHT );
		int index = operation.getIndex();
		
		for( int i = 0; i < count; i++ ) {
			parent.drop( insertDockables.get( i ), index++ );
		}
	}
}
