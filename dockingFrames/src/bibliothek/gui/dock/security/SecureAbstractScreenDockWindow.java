/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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

import java.awt.event.MouseEvent;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.AbstractScreenDockWindow;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * An {@link AbstractScreenDockWindow} that uses a {@link GlassedPane}
 * in order to catch all {@link MouseEvent}s and to report them to the
 * {@link SecureMouseFocusObserver}.
 * @author Benjamin Sigg
 * @deprecated this class is no longer required and will be removed in a future release, use
 * {@link AbstractScreenDockWindow} instead.
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, no replacement required" )
public abstract class SecureAbstractScreenDockWindow extends AbstractScreenDockWindow {
    
    /**
     * Creates a new window
     * @param station the owner of this window
     */
    public SecureAbstractScreenDockWindow( ScreenDockStation station ){
        super( station );
    }
}
