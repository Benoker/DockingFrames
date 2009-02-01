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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CommonDockable;

/**
 * An interface that can be implemented by classes that extend a {@link DockStation},
 * are {@link Dockable}s and that should implement {@link CommonDockable}
 * as well. This interface guarantees type safety but does not specify any
 * additional features.
 * @author Benjamin Sigg
 *
 * @param <S> the kind of station this wrapps
 */
public interface CommonStation<S extends DockStation> extends CommonDockable{

	/**
	 * Returns <code>this</code> as station of type <code>S</code>. This method
	 * is not allowed to return <code>null</code>.
	 * @return <code>this</code>
	 */
	public S asDockStation();
}
