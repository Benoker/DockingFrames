/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.common.perspective;

import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.intern.station.CommonDockStationFactory;
import bibliothek.gui.dock.perspective.PerspectiveElement;

/**
 * A {@link PerspectiveElement} that represents a {@link CommonDockStation}.
 * @author Benjamin Sigg
 */
public interface CommonDockStationPerspective extends CommonElementPerspective{
	/**
	 * Gets the unique identifier of the {@link DockFactory} that handles this perspective element, for
	 * {@link CommonDockStationPerspective}s the result should be {@link CommonDockStationFactory#FACTORY_ID}.
	 */
	public String getFactoryID();
	
	/**
	 * Gets the unique identifier of the {@link DockFactory} that is actually used to layout this perspective.
	 * @return the identifier of the actual factory
	 * @see CommonDockStation#getConverterID()
	 */
	public String getConverterID();
}
