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
 * An <code>ActionOffer</code> creates a {@link DockActionSource} for a {@link Dockable}. An <code>ActionOffer</code>
 * is {@link DockController#addActionOffer(ActionOffer) added} to the {@link DockController}. When the {@link Dockable}s method
 * {@link Dockable#getGlobalActionOffers()} is called, the <code>Dockable</code> most often will call
 * {@link DockController#listOffers(Dockable)} to create the list of actions. This method in return will call
 * {@link #getSource(Dockable, DockActionSource, DockActionSource[], DockActionSource, DockActionSource[])} on the first
 * {@link ActionOffer} which is {@link #interested(Dockable)} in the {@link Dockable}.
 * @author Benjamin Sigg
 * @see DockController#addActionOffer(ActionOffer)
 * @see DockController#removeActionOffer(ActionOffer)
 */
public interface ActionOffer {
    /**
     * Tells whether this <code>ActionOffer</code> wants to collect the 
     * actions for the <code>dockable</code>, or if this ActionOffer
     * is not interested in the {@link Dockable}.
     * @param dockable The {@link Dockable} to test
     * @return <code>true</code> if this ActionOffer should tell which
     * {@link DockAction actions} will be associated with the <code>dockable</code>,
     * <code>false</code> otherwise. 
     */
    public boolean interested( Dockable dockable );
    
    /**
     * Generates one {@link DockActionSource source} of {@link DockAction actions}
     * for the given {@link Dockable}. The ActionOffer is free how to use the
     * actions that are created by other parts of the system, but it is a good
     * idea to use all of them. Note that each argument, and each element in
     * an array, can be <code>null</code>.
     * @param dockable The {@link Dockable} for which the the {@link DockActionSource source}
     * has to be created. An invocation of {@link #interested(Dockable) interested}
     * should return <code>true</code>, otherwise the behavior of this method
     * is not specified.
     * @param source the DockActionSource derived from dockable
     * @param guards a list of DockActionSources derived from {@link ActionGuard ActionGuards} 
     * @param parent the DockActionSource derived from the parent of <code>dockable</code>
     * @param parents a list of DockActionSources derived from all parents of <code>dockable</code>
     * @return The source that was created.
     */
    public DockActionSource getSource( Dockable dockable, DockActionSource source, DockActionSource[] guards, DockActionSource parent, DockActionSource[] parents );
}
