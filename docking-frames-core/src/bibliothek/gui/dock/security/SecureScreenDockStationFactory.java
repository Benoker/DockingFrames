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

import java.awt.Window;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockStationFactory;
import bibliothek.gui.dock.util.WindowProvider;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A factory which creates instances of {@link SecureScreenDockStation}.
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary and will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, make sure old layouts can still be loaded" )
public class SecureScreenDockStationFactory extends ScreenDockStationFactory {
    /** The identifier of this factory */
    public static final String ID = "secure " + ScreenDockStationFactory.ID;
    
    /**
     * Creates a new factory.
     * @param owner the window which will be used as owner of all windows
     * created by this station.
     */
    public SecureScreenDockStationFactory( Window owner ) {
        super(owner);
    }
    
    /**
     * Creates a new factory.
     * @param owner the window which will be used as owner of all windows
     * created by this station.
     */
    public SecureScreenDockStationFactory( WindowProvider owner ) {
        super(owner);
    }

    @Override
    public String getID() {
        return ID;
    }
    
    @Override
    protected ScreenDockStation createStation() {
        return new SecureScreenDockStation( getProvider() );
    }
}
