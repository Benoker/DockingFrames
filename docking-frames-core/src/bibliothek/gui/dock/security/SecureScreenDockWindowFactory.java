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

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.DefaultScreenDockWindowFactory;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.ScreenDockWindowFactory;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * A {@link ScreenDockWindowFactory} creating new {@link SecureScreenDockDialog}s.
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary, use {@link DefaultScreenDockWindowFactory}
 * instead
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MINOR, priority=Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, no replacement necessary" )
public class SecureScreenDockWindowFactory implements ScreenDockWindowFactory {
    public ScreenDockWindow createWindow( ScreenDockStation station ) {
        boolean undecorated = true;
        
        Window owner = station.getOwner();
        if( owner instanceof Frame )
            return new SecureScreenDockDialog( station, (Frame)owner, undecorated );
        if( owner instanceof Dialog )
            return new SecureScreenDockDialog( station, (Dialog)owner, undecorated );
        
        return new SecureScreenDockDialog( station, undecorated );
    }

}
