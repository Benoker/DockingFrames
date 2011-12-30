package bibliothek.gui.dock.toolbar.intern;

import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.toolbar.CToolbarItem;

/**
 * A {@link ComponentDockable} that is used as {@link CommonDockable} by a {@link CToolbarItem}.
 * @author Benjamin Sigg
 */
public class CommonComponentDockable extends ComponentDockable implements CommonDockable{
	private CToolbarItem item;
	
	/**
	 * Creates a new dockable.
	 * @param item the item which is represented by this dockable
	 */
	public CommonComponentDockable( CToolbarItem item ){
		this.item = item;
	}
	
	@Override
	public CommonComponentDockable asDockable(){
		return this;
	}
	
	@Override
	public CommonDockStation<?, ?> asDockStation(){
		return null;
	}
	
	@Override
	public CDockable getDockable(){
		return item;
	}

	@Override
	public CStation<?> getStation(){
		return null;
	}

	@Override
	public DockActionSource[] getSources(){
		return new DockActionSource[]{};
	}

}
