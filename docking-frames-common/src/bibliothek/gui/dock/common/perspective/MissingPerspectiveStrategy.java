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

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.util.Path;

/**
 * This strategy is used by a {@link CControl} or a {@link CControlPerspective} to create
 * {@link CStationPerspective}s for stations that are not (yet) registered at the 
 * {@link CControl}.
 * @author Benjamin Sigg
 */
public interface MissingPerspectiveStrategy {
	/**
	 * Creates the station that represented <code>id</code>.
	 * @param id the identifier of the station
	 * @param typeId the type of the station, this is the identifier that was returned by {@link CStation#getTypeId()},
	 * can be <code>null</code>
	 * @return the new perspective, can be <code>null</code>
	 */
	public CStationPerspective createStation( String id, Path typeId );
}
