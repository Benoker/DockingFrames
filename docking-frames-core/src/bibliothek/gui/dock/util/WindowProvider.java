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
package bibliothek.gui.dock.util;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Window;

/**
 * A window provider has somehow access to one or many {@link Window}s and can
 * provide anyone which needs a window with one.
 * @author Benjamin Sigg
 */
public interface WindowProvider {
    /**
     * Tries to find a window. The result should either be a {@link Frame}
     * or a {@link Dialog}, and a plain {@link Window} only as last resort. 
     * If possible the main-frame or another important window that will not
     * be closed soon should be returned. Visible windows are preferred over
     * non visible ones. This method is not guaranteed to have success, 
     * <code>null</code> is a valid result. This method is not required
     * to return always the same window, however the {@link WindowProviderListener}s
     * should be informed when the result changes.
     * @return if possible a visible {@link Frame} which won't be closed in
     * the near future, any window which does not fulfill the requirements
     * or <code>null</code> if no window is available at all. 
     */
    public Window searchWindow();
    
    /**
     * Tells whether this {@link WindowProvider} represents a window that is visible. Under
     * normal circumstances this method would return:<br>
     * <code>Window window = searchWindow();<br>
     * return window == null ? false : window.isShowing();</code><br>
     * This method is explicitly allowed to return any value it likes. The result
     * of this method does not have to correspond with reality.
     * @return whether this providers window is visible or not
     */
    public boolean isShowing();
    
    /**
     * Adds a new listener to this provider. The listener should be called
     * when the window provided by this object changes.
     * @param listener the new listener
     */
    public void addWindowProviderListener( WindowProviderListener listener );
    
    /**
     * Removes a listener from this provider
     * @param listener the listener to remove
     */
    public void removeWindowProviderListener( WindowProviderListener listener );
}
