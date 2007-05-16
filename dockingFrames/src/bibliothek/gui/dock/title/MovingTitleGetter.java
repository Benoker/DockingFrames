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

package bibliothek.gui.dock.title;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;

/**
 * The MovingTitleGetter decides which {@link DockTitle} should be shown
 * under the mousepointer when the user grabs a {@link Dockable} and moves
 * it around.
 * @author Benjamin Sigg
 */
public interface MovingTitleGetter {
    /**
     * Gets a title which will be shown underneath the cursor. Assumes that the 
     * user clicked on the title <code>snatched</code>.
     * @param controller The controller which will be responsible for the title
     * @param snatched The title which is grabbed by the user
     * @return A {@link DockTitle}. The title must not be {@link DockTitle#bind() binded}
     * to any {@link Dockable} except the owner of <code>snatched</code>. The title
     * may be <code>snatched</code> itself, a new instance, an existing title of
     * somewhere else, or <code>null</code>. Note that clients may use the 
     * {@link DockTitle#getOrigin() origin} of the result to create a new,
     * unbinded and independent title.
     */
    public DockTitle get( DockController controller, DockTitle snatched );

    /**
     * Gets a title which will be shown underneath the cursor. Assumes
     * that the user clicked on <code>dockable</code>.
     * @param controller The controller which will be responsible for the title
     * @param dockable The Dockable which is snatched
     * @return  A {@link DockTitle}. The title must not be {@link DockTitle#bind() binded}
     * to any {@link Dockable} except <code>dockable</code>. The title
     * may be a new instance, an existing title of somewhere else, or <code>null</code>
     */
    public DockTitle get( DockController controller, Dockable dockable );
}
