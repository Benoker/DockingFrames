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
package bibliothek.gui.dock.event;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;

/**
 * This listener is added to a {@link DockFrontend}. It gets informed before
 * and after a {@link Dockable} is shown or hidden. In some cases this listener 
 * can cancel the operation.
 * @author Benjamin Sigg
 */
public interface VetoableDockFrontendListener {
    /**
     * Called before a {@link Dockable} is shown. This method is only called
     * if the user tries to open the element through the standard way, meaning
     * that {@link DockFrontend#show(Dockable,boolean)} is called.<br>
     * To abort the operation, {@link VetoableDockFrontendEvent#cancel()} can
     * be invoked.
     * @param event description of the element to close
     */
    public void showing( VetoableDockFrontendEvent event );
    
    /**
     * Called whenever a {@link Dockable} was shown. Other than
     * {@link #showing(VetoableDockFrontendEvent)} this method is always called.
     * @param event description of the element and how it got shown
     */
    public void shown( VetoableDockFrontendEvent event );
    
    /**
     * Called before a {@link Dockable} is hidden. This method is only called
     * if the user tries to close the element through the close-action, meaning
     * that {@link DockFrontend#hide(Dockable,boolean)} is called.<br>
     * To abort the operation, {@link VetoableDockFrontendEvent#cancel()} can
     * be invoked.
     * @param event description of the element to close
     */
    public void hiding( VetoableDockFrontendEvent event );
    
    /**
     * Called whenever a {@link Dockable} was hidden. Other than
     * {@link #hiding(VetoableDockFrontendEvent)} this method is always called.
     * @param event description of the element and how it got closed
     */
    public void hidden( VetoableDockFrontendEvent event );
}
