package bibliothek.gui.dock.toolbar.location;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.DefaultLocationMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationMode;
import bibliothek.gui.dock.facile.mode.StationModeArea;
import bibliothek.gui.dock.station.toolbar.ToolbarStrategy;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.gui.dock.support.mode.ModeSettingFactory;
import bibliothek.gui.dock.support.mode.NullModeSettingsFactory;
import bibliothek.util.Path;

/**
 * A {@link LocationMode} describing items that are part of a toolbar.
 * @author Benjamin Sigg
 */
public class ToolbarMode<T extends StationModeArea> extends DefaultLocationMode<T>{
	/** the unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.toolbar" );
	
	/** the mode described by {@link ToolbarMode} */
	public static final ExtendedMode TOOLBAR = new ExtendedMode( IDENTIFIER );

	private DockController controller;
	
	/**
	 * Creates the new mode.
	 * @param controller the controller in whose realm this mode is used
	 */
	public ToolbarMode( DockController controller ){
		this.controller = controller;
	}
	
	@Override
	public ExtendedMode getExtendedMode(){
		return TOOLBAR;
	}

	@Override
	public void ensureNotHidden( Dockable dockable ){
		// ignore
	}

	@Override
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	@Override
	public boolean isDefaultMode( Dockable dockable ){
		ToolbarStrategy strategy = controller.getProperties().get( ToolbarStrategy.STRATEGY );
		return strategy.isToolbarGroupPart( dockable );
	}

	@Override
	public void writeSetting( ModeSetting<Location> setting ){
		// ignore
	}

	@Override
	public void readSetting( ModeSetting<Location> setting ){
		// ignore
	}

	@Override
	public ModeSettingFactory<Location> getSettingFactory(){
		return new NullModeSettingsFactory<Location>( getUniqueIdentifier() );
	}
}
