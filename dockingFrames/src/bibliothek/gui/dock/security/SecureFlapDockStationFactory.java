/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.security;

import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.FlapDockStationFactory;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A factory that creates instances of {@link SecureFlapDockStation}
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary and will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, make sure old layouts can still be read")
public class SecureFlapDockStationFactory extends FlapDockStationFactory {
    /** The id used for this factory */
    public static final String ID = "secure " + FlapDockStationFactory.ID;
    
    @Override
    public String getID() {
        return ID;
    }
    
    @Override
    protected FlapDockStation createStation() {
        return new SecureFlapDockStation();
    }
}
