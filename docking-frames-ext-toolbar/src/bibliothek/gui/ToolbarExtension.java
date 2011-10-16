package bibliothek.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.control.relocator.DefaultDockRelocator;
import bibliothek.gui.dock.control.relocator.Merger;
import bibliothek.gui.dock.layout.DockSituation;
import bibliothek.gui.dock.layout.DockablePropertyFactory;
import bibliothek.gui.dock.layout.PropertyTransformer;
import bibliothek.gui.dock.station.screen.magnet.AttractorStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarAttractorStrategy;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarContainerPropertyFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarDockStationMerger;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarGroupDockStationMerger;
import bibliothek.gui.dock.station.toolbar.ToolbarPartDockFactory;
import bibliothek.gui.dock.station.toolbar.ToolbarPropertyFactory;
import bibliothek.gui.dock.util.extension.Extension;
import bibliothek.gui.dock.util.extension.ExtensionName;

/**
 * Allows seamless integration of the toolbar extension into the core and common
 * library without them having any references back to the toolbar project.
 * @author Benjamin Sigg
 */
public class ToolbarExtension implements Extension{
	@Override
	public void install( DockController controller ){
		// nothing to do
	}

	@Override
	public void uninstall( DockController controller ){
		// nothing to do
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> Collection<E> load( DockController controller, ExtensionName<E> extension ){
		if( extension.getName().equals( PropertyTransformer.FACTORY_EXTENSION )){
			return (Collection<E>)createPropertyFactoryExtension();
		}
		if( extension.getName().equals( DefaultDockRelocator.MERGE_EXTENSION )){
			return (Collection<E>)createMergerExtension();
		}
		if( extension.getName().equals( ScreenDockStation.ATTRACTOR_STRATEGY_EXTENSION )){
			return (Collection<E>)createAttractorStrategies();
		}
		if( extension.getName().equals( DockSituation.DOCK_FACTORY_EXTENSION )){
			return (Collection<E>)createDockFactories();
		}
		
		return null;
	}
	
	private Collection<DockablePropertyFactory> createPropertyFactoryExtension(){
		List<DockablePropertyFactory> result = new ArrayList<DockablePropertyFactory>();
		result.add( new ToolbarPropertyFactory() );
		result.add( new ToolbarContainerPropertyFactory() );
		return result;
	}
	
	private Collection<Merger> createMergerExtension(){
		List<Merger> result = new ArrayList<Merger>();
		result.add( new ToolbarGroupDockStationMerger() );
		result.add( new ToolbarDockStationMerger() );
		return result;
	}
	
	private Collection<AttractorStrategy> createAttractorStrategies(){
		List<AttractorStrategy> result = new ArrayList<AttractorStrategy>();
		result.add( new ToolbarAttractorStrategy() );
		return result;
	}
	
	private Collection<DockFactory<?,?,?>> createDockFactories(){
		List<DockFactory<?,?,?>> result = new ArrayList<DockFactory<?,?,?>>();
		result.add( new ToolbarPartDockFactory() );
		result.add( new ToolbarDockStationFactory() );
		result.add( new ToolbarGroupDockStationFactory() );
		result.add( new ToolbarContainerDockStationFactory() );
		return result;
	}
}
