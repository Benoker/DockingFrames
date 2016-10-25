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
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.intern.CommonElement;

/**
 * An interface that can be implemented by classes that extend a {@link DockStation} and
 * that represent a {@link CStation}.
 * @author Benjamin Sigg
 * @param <S> the kind of station this wraps
 * @param <C> the kind of {@link CommonDockStation} this is
 */
public interface CommonDockStation<S extends DockStation, C extends CommonDockStation<S,C>> extends CommonElement, DockStation{
	/**
	 * Gets <code>this</code> as <code>S</code>.
	 * @return <code>this</code>
	 */
	public S getDockStation();
	
	/**
	 * Returns <code>this</code> as station of type <code>C</code>. This method
	 * is not allowed to return <code>null</code>.
	 * @return <code>this</code>
	 */
	public C asDockStation();
	
	/**
	 * Gets the model of this station.
	 * @return the model, may not be <code>null</code>
	 */
	public CStation<C> getStation();
	
	/**
	 * Gets the unique identifier of the {@link DockFactory} that stores and loads the layout of this
	 * station. For {@link CommonDockStation}s the result should always be {@link CommonDockStationFactory#FACTORY_ID}
	 * @see #getConverterID()
	 */
	public String getFactoryID();
	
	/**
	 * Gets the unique identifier of the {@link DockFactory} that should be used by the {@link CommonDockStationFactory}
	 * to actually write or read the layout. Usually the result of this method is the same result
	 * as {@link DockStation#getFactoryID()} (note: the factory id from the <b>super</b> class).
	 * @return the unique identifier of a {@link DockFactory}. Can be <code>null</code> if 
	 * {@link #getFactoryID()} does not return {@link CommonDockStationFactory#FACTORY_ID}
	 */
	public String getConverterID();
}
