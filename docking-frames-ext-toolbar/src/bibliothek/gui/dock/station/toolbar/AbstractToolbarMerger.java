package bibliothek.gui.dock.station.toolbar;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.Position;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm allowing to merge two {@link AbstractToolbarDockStation}s together.
 * @author Benjamin Sigg
 */
public abstract class AbstractToolbarMerger implements Merger {
	/**
	 * Checks whether the type of <code>station</code> is valid for this merger.
	 * @param station can either be the parent of the child that is merged
	 * @return <code>true</code> if the type is accepted
	 */
	protected abstract boolean validType( AbstractToolbarDockStation station );
	
	@Override
	public boolean canMerge( StationDropOperation operation, DockStation parent, DockStation child ){
		return (operation == null || !operation.isMove()) && parent instanceof AbstractToolbarDockStation && validType( (AbstractToolbarDockStation)parent ) && child instanceof AbstractToolbarDockStation && validType( (AbstractToolbarDockStation)child );
	}

	@Override
	public void merge( StationDropOperation operation, DockStation parent, DockStation child ){
		merge( (ToolbarDropInfo<?>) operation, (AbstractToolbarDockStation) parent, (AbstractToolbarDockStation) child );
	}

	public void merge( ToolbarDropInfo<?> operation, AbstractToolbarDockStation parent, AbstractToolbarDockStation child ){
		// WARNING: if I don't do a copy of dockables, problem occurs.
		// Perhaps due to concurrent access to the dockable (drop in
		// goal area ==> drag in origin area)?
		int count = child.getDockableCount();
		List<Dockable> insertDockables = new ArrayList<Dockable>();
		for( int i = 0; i < count; i++ ) {
			insertDockables.add( child.getDockable( i ) );
		}
		int increment = 0;
		if( operation.getSideDockableBeneathMouse() == Position.SOUTH || operation.getSideDockableBeneathMouse() == Position.EAST ) {
			increment++;
		}
		int dropIndex = parent.indexOf( operation.getDockableBeneathMouse() ) + increment;
		for( int i = 0; i < count; i++ ) {
			parent.drop( insertDockables.get( i ), dropIndex++ );
		}
	}
}
