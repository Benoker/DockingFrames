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
package bibliothek.gui.dock.themes.basic.action;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;


/**
 * Used as a callback by a {@link BasicButtonModel} to ensure the availability of properties and to inform
 * when the user triggers a view like a button.
 * @author Benjamin Sigg
 */
public interface BasicTrigger {
    /**
     * Invoked by a {@link BasicButtonModel} when the button, which is monitored
     * by the model, is clicked.
     */
    public void triggered();
    
    /**
     * Gets the {@link DockAction} which will be triggered by this object. This is a completely optional
     * method, as not every {@link BasicTrigger} is actually connected to a {@link DockAction}.
     * @return the action, can be <code>null</code>
     */
    public DockAction getAction();
    
    /**
     * Gets the {@link Dockable} for which an action will be performed if this object is triggered. This is 
     * a completely optional method, as not every {@link BasicTrigger} is actually connected to a {@link Dockable}.
     * @return the associated dockable or <code>null</code>
     */
    public Dockable getDockable();
}
