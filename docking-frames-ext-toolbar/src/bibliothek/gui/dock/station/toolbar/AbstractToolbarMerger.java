package bibliothek.gui.dock.station.toolbar;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.AbstractToolbarDockStation;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * An algorithm allowing to merge two {@link AbstractToolbarDockStation}s together.
 * @author Benjamin Sigg
 * @author Herve Guillaume
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
	
}
