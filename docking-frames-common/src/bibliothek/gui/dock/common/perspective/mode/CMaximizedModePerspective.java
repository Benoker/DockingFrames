/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.common.perspective.mode;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.common.mode.CMaximizedMode;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.MaximizedModeSetting;
import bibliothek.gui.dock.support.mode.ModeSetting;
import bibliothek.util.Path;

/**
 * Represents a {@link CMaximizedMode}.
 * @author Benjamin Sigg
 */
public class CMaximizedModePerspective extends AbstractModePerspective<CMaximizedModeAreaPerspective>{
	/** unforwarded information about the last mode some dockable was in before maximization */
	private Map<String, Path> lastMaximizedMode = new HashMap<String, Path>();
	/** unforwarded information about the last location some dockable was in before maximization */
	private Map<String, Location> lastMaximizedLocation = new HashMap<String, Location>();
	
	public ExtendedMode getIdentifier(){
		return ExtendedMode.MAXIMIZED;
	}
	
	@Override
	public void add( CMaximizedModeAreaPerspective location ){
		super.add( location );
		forward( location );
	}
	
	private void forward( CMaximizedModeAreaPerspective area ){
		Path mode = lastMaximizedMode.remove( area.getUniqueId() );
		Location location = lastMaximizedLocation.remove( area.getUniqueId() );
		
		area.setUnmaximize( mode, location );
	}
	
	@Override
	public void readSetting( ModeSetting<Location> setting ){
		if( setting instanceof MaximizedModeSetting ){
			lastMaximizedLocation.clear();
			lastMaximizedMode.clear();
			
			lastMaximizedLocation.putAll( ((MaximizedModeSetting)setting).getLastMaximizedLocation() );
			lastMaximizedMode.putAll( ((MaximizedModeSetting)setting).getLastMaximizedMode() );
			
			for( int i = 0, n = getAreaCount(); i < n; i++ ){
				forward( getArea( i ));
			}
		}
	}
	
	@Override
	public void writeSetting( ModeSetting<Location> setting ){
		if( setting instanceof MaximizedModeSetting ){
			MaximizedModeSetting modeSetting = (MaximizedModeSetting)setting;
			
			for( int i = 0, n = getAreaCount(); i<n; i++ ){
				CMaximizedModeAreaPerspective area = getArea( i );
				Path mode = area.getUnmaximizeMode();
				if( mode != null ){
					modeSetting.getLastMaximizedMode().put( area.getUniqueId(), mode );
					Location location = area.getUnmaximizeLocation();
					if( location != null ){
						modeSetting.getLastMaximizedLocation().put( area.getUniqueId(), location );
					}
				}
			}
		}
	}
}
