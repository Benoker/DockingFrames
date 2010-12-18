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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.flap.ButtonPane;
import bibliothek.gui.dock.station.flap.DefaultFlapWindow;
import bibliothek.gui.dock.station.flap.FlapWindow;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link FlapWindow} which inserts a {@link GlassedPane} between its
 * {@link Dockable} and the outer world. Adding and removing of the GlassPane
 * are handled automatically.
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary and will be removed in a future release, use {@link DefaultFlapWindow} instead
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="Remove this class, no replacemenet required")
public class SecureFlapWindow extends DefaultFlapWindow {
    /**
     * Creates a new window
     * @param station the station which will use this window
     * @param buttonPane the visible part of the station
     * @param window the parent of this window
     */
    public SecureFlapWindow( FlapDockStation station, ButtonPane buttonPane, Parent window ) {
        super( station, buttonPane, window );
    }
}
