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

import bibliothek.gui.DockController;
import bibliothek.gui.dock.control.DefaultDockControllerFactory;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * The DockingFrames normally uses some system-resources to handle global events.
 * However, there are some environments where system-resources are not available
 * due to a SecurityManager (for example in an Applet). The Secure-X-classes
 * are designed to work in such an environment. They are not as efficient as
 * the normal classes, and there may be some settings where they have a
 * weird behaviour.<br>
 * Setting up the secure environment is easy: just use a SecureXZY where normally
 * a XYZ would be. Additionally clients must pack all stations into one or
 * more {@link GlassedPane GlassedPanes}. These panes must then be added to
 * the {@link SecureMouseFocusObserver} of this SecureDockController. <br>
 * Note that {@link SecureFlapDockStation} and {@link SecureScreenDockStation}
 * will add a {@link GlassedPane} to their windows and dialogs automatically.
 * 
 * @author Benjamin Sigg
 * @deprecated this class is no longer necessary, {@link DockController} can now handle
 * restricted environments as well. See {@link DockController#setRestrictedEnvironment(boolean)}.
 * This class will be removed in a future release
 */
@Deprecated
@Todo( compatibility=Compatibility.BREAK_MAJOR, priority=Todo.Priority.MAJOR, target=Version.VERSION_1_1_1,
		description="remove this class, no replacement required")
public class SecureDockController extends DockController {
    /**
     * Creates a new controller
     */
    public SecureDockController() {
        super( new DefaultDockControllerFactory() );
        setRestrictedEnvironment( true );
    }
    
    /**
     * Creates a new controller, but does not initiate the properties
     * if <code>factory</code> is <code>null</code>.
     * @param factory the factory that will create the elements of this controller
     * @see #initiate()   
     */
    public SecureDockController( SecureDockControllerFactory factory ) {
        super( factory );
        setRestrictedEnvironment( true );
    }
}
