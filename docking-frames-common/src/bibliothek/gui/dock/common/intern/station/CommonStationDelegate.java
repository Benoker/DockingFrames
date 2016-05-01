/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.title.DockTitleVersion;

/**
 * Implements several methods that could be used by a {@link CommonDockStation}. Instances
 * of this interface are most times used as delegate by a {@link CommonDockStation}.
 * @author Benjamin Sigg
 * @param <S> the type of {@link DockStation} this delegate represents
 */
public interface CommonStationDelegate<S extends CommonDockStation<?, ?>> {
	/**
	 * Gets a result for {@link CommonDockable#getDockable()}.
	 * @return the dockable as it is seen by Common
	 */
	public CDockable getDockable();
	
	/**
	 * Gets a result for {@link CommonDockStation#getStation()}.
	 * @return the station as it is seen by Common
	 */
	public CStation<S> getStation();
	
	/**
	 * Gets a result for {@link CommonDockable#getSources()}.
	 * @return the sources for the owner of this delegate
	 */
	public DockActionSource[] getSources();
	
	/**
	 * Decides whether to show <code>title</code> for the owner
	 * of this delegate.
	 * @param title the title that might be shown
	 * @return <code>true</code> if the title should be visible, <code>false</code>
	 * otherwise
	 */
	public boolean isTitleDisplayed( DockTitleVersion title );
}
