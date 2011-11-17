package bibliothek.gui.dock.station;

import bibliothek.gui.dock.StackDockStation;

/**
 * A {@link StackDockStation} modified such that it can show toolbar items.
 * @author Benjamin Sigg
 */
public class ToolbarTabDockStation extends StackDockStation{
	public ToolbarTabDockStation(){
		setSmallMinimumSize( false );
		setTitleIcon( null );
	}
	
	@Override
	public String getFactoryID(){
		return ToolbarTabDockStationFactory.FACTORY_ID;
	}
}
