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
package bibliothek.sizeAndColor;

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;

/**
 * Special {@link PlaceholderStrategy} working with {@link Frame}s.
 * @author Benjamin Sigg
 */
public class FramePlaceholderStrategy implements PlaceholderStrategy {
	private int count = 0;
	
	public void addListener( PlaceholderStrategyListener listener ){
		// ignore
	}

	public Path getPlaceholderFor( Dockable dockable ){
		if( dockable instanceof Frame ){
			Frame frame = (Frame)dockable;
			Path path = frame.getPlaceholder();
			if( path == null ){
				path = new Path( "demo", "frame", "i" + count );
				count++;
				frame.setPlaceholder( path );
			}
			return path;
		}
		
		return null;
	}

	public void install( DockStation station ){
		// ignore	
	}

	public boolean isValidPlaceholder( Path placeholder ){
		return true;
	}

	public void removeListener( PlaceholderStrategyListener listener ){
		// ignore
	}

	public void uninstall( DockStation station ){
		// ignore
	}
}
