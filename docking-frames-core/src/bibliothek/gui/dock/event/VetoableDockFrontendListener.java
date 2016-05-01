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
 * can cancel the operation.<br>
 * The number of calls of a method of this listener does not have to be the
 * same number for any other method. Also an array of <code>Dockable</code>s
 * given to one method may be split up into many arrays the next time or when
 * given to another method. It is however guaranteed that there are no false
 * alarms (i.e. an already invisible <code>Dockable</code> will never
 * be given to {@link #hiding(VetoableDockFrontendEvent)}).<br>
 * Note: the scope of this listener is limited, please read the comments
 * of {@link DockFrontend#addVetoableListener(VetoableDockFrontendListener)} for further
 * information.
 * @author Benjamin Sigg
 */
public interface VetoableDockFrontendListener {
    /**
     * Called before a {@link Dockable} is shown. To abort the operation, 
     * {@link VetoableDockFrontendEvent#cancel()} can be invoked.
     * This method may not be called for all {@link Dockable}s, it is certainly
     * called if a client opens a {@link Dockable} through {@link DockFrontend#show(Dockable)}
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
     * Called before a set of {@link Dockable}s is hidden. To abort the 
     * operation, {@link VetoableDockFrontendEvent#cancel()} can be invoked.
     * This method may not always be invoked for all {@link Dockable}s, it
     * is certainly invoked if {@link DockFrontend#hide(Dockable)}
     * is called or if {@link DockFrontend#setSetting(bibliothek.gui.dock.frontend.Setting, boolean)}.
     * @param event description of the element to close
     */
    public void hiding( VetoableDockFrontendEvent event );
    
    /**
     * Called whenever a set of {@link Dockable} was hidden. Other than
     * {@link #hiding(VetoableDockFrontendEvent)} this method is always called.
     * @param event description of the element and how it got closed
     */
    public void hidden( VetoableDockFrontendEvent event );
}
