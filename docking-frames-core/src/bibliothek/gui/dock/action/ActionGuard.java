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

package bibliothek.gui.dock.action;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * An ActionGuard is {@link DockController#addActionGuard(ActionGuard) added}
 * to a {@link DockController}, and whenever the {@link DockActionSource actions}
 * of a {@link Dockable} have to be determined, all known ActionGuards are
 * asked, if they may add some additional {@link DockActionSource actions}.
 * @author Benjamin Sigg
 * @see DockController#addActionGuard(ActionGuard)
 * @see DockController#removeActionGuard(ActionGuard)
 * @see DockController#listOffers(Dockable)
 */
public interface ActionGuard {
    /**
     * Tests the given {@link Dockable} and tells whether this
     * ActionGuard is interested in it and wants to add some additional
     * {@link #getSource(Dockable) actions} to it, or if this guard
     * is not made for the <code>dockable</code>.
     * @param dockable The {@link Dockable} to test
     * @return <code>true</code> if the {@link #getSource(Dockable)}-method
     * should be invoked, <code>false</code> otherwise
     */
    public boolean react( Dockable dockable );
    
    /**
     * Gets a list of actions for the {@link Dockable}
     * @param dockable The {@link Dockable} for which {@link #react(Dockable)}
     * is <code>true</code>
     * @return The actions which shall be used together with the <code>dockable</code>.
     */
    public DockActionSource getSource( Dockable dockable );
}
