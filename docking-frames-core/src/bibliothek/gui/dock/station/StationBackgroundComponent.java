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
package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A {@link BackgroundComponent} representing a {@link DockStation}.
 * @author Benjamin Sigg
 */
public interface StationBackgroundComponent extends BackgroundComponent{
	/** The kind of {@link UIValue} this is */
	public static final Path KIND = BackgroundComponent.KIND.append( "station" );
	
	/**
	 * Gets the {@link DockStation} which is using this background.
	 * @return the station, not <code>null</code>
	 */
	public DockStation getStation();
}
