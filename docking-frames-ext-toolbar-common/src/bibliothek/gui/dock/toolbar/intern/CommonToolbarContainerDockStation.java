package bibliothek.gui.dock.toolbar.intern;

import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.toolbar.CToolbarArea;

/**
 * A {@link ToolbarContainerDockStation} used as root station.
 * @author Benjamin Sigg
 */
public class CommonToolbarContainerDockStation extends ToolbarContainerDockStation implements CommonDockStation<ToolbarContainerDockStation, CommonToolbarContainerDockStation>{
	private CToolbarArea container;
	
	public CommonToolbarContainerDockStation( CToolbarArea container, Orientation orientation ){
		super( orientation );
		this.container = container;
	}
	
	@Override
	public CommonToolbarContainerDockStation asDockStation(){
		return this;
	}
	
	@Override
	public CommonDockable asDockable(){
		return null;
	}
	
	@Override
	public CommonToolbarContainerDockStation getDockStation(){
		return this;
	}

	@Override
	public CStation<ToolbarContainerDockStation> getStation(){
		return container;
	}

	@Override
	public String getFactoryID(){
		return CommonDockStationFactory.FACTORY_ID;
	}
	
	@Override
	public String getConverterID(){
		return super.getFactoryID();
	}
}
