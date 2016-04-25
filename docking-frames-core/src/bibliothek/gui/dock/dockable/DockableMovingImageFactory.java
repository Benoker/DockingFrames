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
package bibliothek.gui.dock.dockable;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A factory for {@link MovingImage}s. This factory is used to show an image
 * that is somehow related to a {@link Dockable} which is dragged.
 *  
 * @author Benjamin Sigg
 */
public interface DockableMovingImageFactory {
    /**
     * Gets an image which will be shown underneath the cursor. Assumes that the 
     * user clicked on the title <code>snatched</code>.
     * @param controller The controller which will be responsible for the title
     * @param snatched The title which is grabbed by the user
     * @return the image under the cursor, can be <code>null</code>
     */
    public MovingImage create( DockController controller, DockTitle snatched );

    /**
     * Gets an image which will be shown underneath the cursor. Assumes
     * that the user clicked on <code>dockable</code>.
     * @param controller The controller which will be responsible for the title
     * @param dockable The Dockable which is snatched
     * @return  the image under the cursor, can be <code>null</code>
     */
    public MovingImage create( DockController controller, Dockable dockable );
}
