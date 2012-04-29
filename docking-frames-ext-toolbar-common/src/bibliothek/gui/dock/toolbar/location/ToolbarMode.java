/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

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
