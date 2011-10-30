package bibliothek.gui.dock.station;

import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.StackDockStationFactory;

/**
 * A factory creating new {@link ToolbarTabDockStation}s.
 * @author Benjamin Sigg
 */
public class ToolbarTabDockStationFactory extends StackDockStationFactory{
	public static final String FACTORY_ID = "ToolbarTabDockStation";
	
	@Override
	protected StackDockStation createStation(){
		return new ToolbarTabDockStation();
	}
}
