/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2016 Benjamin Sigg
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
package bibliothek.gui.dock.station.layer;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * A factory that is responsible for creating {@link DockStationDropLayer}s for {@link DockStation}s.
 * @see DockStationDropLayer
 * @see DockStation#getLayers()
 * @author Benjamin Sigg
 */
public interface DockStationDropLayerFactory {
	/**
	 * Key to replace the default {@link DockStationDropLayerFactory} with a custom factory.
	 */
	public static PropertyKey<DockStationDropLayerFactory> DROP_LAYER_FACTORY = 
			new PropertyKey<DockStationDropLayerFactory>( "dock.dropLayerFactory", 
					new ConstantPropertyFactory<DockStationDropLayerFactory>( new DefaultDockStationDropLayerFactory() ), true );
	
	/**
	 * Gets all the {@link DockStationDropLayer}s that should be used for finding out whether a drag and drop
	 * operation has <code>station</code> as target.<br>
	 * @see DockStation#getLayers()
	 * @param station the station whose drag-and-drop layers are requested
	 * @return the layers, not <code>null</code> - but an empty array is a valid result
	 */
	public DockStationDropLayer[] getLayers( DockStation station );
}
