/**
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
import bibliothek.gui.dock.control.MouseFocusObserver;

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
    /**
     * Creates a new controller
     */
    public SecureDockController() {
        super();
    }
    
    /**
     * Creates a new controller, but does not initiate the properties
     * if <code>initiate</code> is <code>false</code>.
     * @param initiate <code>true</code> if all properties should
     * be initiated
     * @see #initiate()   
     */
    protected SecureDockController( boolean initiate ) {
        super( initiate );
    }
    
    @Override
    protected MouseFocusObserver createMouseFocusObserver() {
        return new SecureMouseFocusObserver( this );
    }
    
    @Override
    public SecureMouseFocusObserver getFocusObserver() {
        return (SecureMouseFocusObserver)super.getFocusObserver();
    }
}
