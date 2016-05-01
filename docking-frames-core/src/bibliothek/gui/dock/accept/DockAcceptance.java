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

package bibliothek.gui.dock.accept;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * Used by a {@link DockController} to decide globally which 
 * {@link Dockable} can become child of which {@link DockStation}.<br>
 * The acceptance can be set through the method 
 * {@link DockController#addAcceptance(DockAcceptance)}
 * @author Benjamin Sigg
 */
public interface DockAcceptance {
    
    /**
     * Tells whether or not <code>child</code> is allowed to become a 
     * real child of <code>parent</code>.
     * @param parent the future parent
     * @param child the future child
     * @return whether or not <code>child</code> and <code>parent</code>
     * are allowed to be combined
     */
    public boolean accept( DockStation parent, Dockable child );
    
    /**
     * Tells whether <code>child</code>, which may be already a child of 
     * <code>parent</code>, and <code>next</code> are allowed to be combined.
     * The result of this combination would replace <code>child</code>
     * on <code>parent</code>.
     * @param parent the future parent of the combination
     * @param child a Dockable which may be a child of parent
     * @param next a new Dockable
     * @return whether the combination is allowed or not
     */
    public boolean accept( DockStation parent, Dockable child, Dockable next );
}
