/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.security;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.FocusController;

/**
 * The DockingFrames normally uses some system-resources to handle the focus.
 * However, there are some environments where system-resources are not available
 * due to a SecurityManager (for example in an Applet). The Secure-X-classes
 * are designed to work in such an environment. They are not as efficient as
 * the normal classes, and there may be some settings where they have a
 * weird behaviour.<br>
 * Setting up the secure environment is easy: just use a SecureXZY where normally
 * a XYZ would be. Additionally clients must pack all stations into one or
 * more {@link GlassedPane GlassedPanes}. These panes must then be added to
 * the {@link SecureFocusController} of this SecureDockController. <br>
 * Note that {@link SecureFlapDockStation} and {@link SecureScreenDockStation}
 * will add a {@link GlassedPane} to their windows and dialogs automatically.
 * 
 * @author Benjamin Sigg
 */
public class SecureDockController extends DockController {
    @Override
    protected FocusController createFocusController() {
        return new SecureFocusController( this );
    }
    
    @Override
    public SecureFocusController getFocusController() {
        return (SecureFocusController)super.getFocusController();
    }
}
