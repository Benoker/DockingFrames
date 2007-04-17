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


package bibliothek.gui.dock.action;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.Dockable;

/**
 * An ActionOffer is {@link DockController#addActionOffer(ActionOffer) added}
 * to the {@link DockController}. Whenever the {@link DockActionSource actions}
 * of a {@link Dockable} have to be collected, one (and only one) 
 * <code>ActionOffer</code> can create the final {@link #getSource(Dockable, DockActionSource, DockActionSource[], DockActionSource, DockActionSource[]) source}
 * of the actions.<br>
 * ActionOffers are not {@link ActionGuard ActionGuars}. Only one ActionOffer
 * can collect the actions of a Dockable, but this one ActionOffer will completely
 * determine, how the actions are combined.
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
