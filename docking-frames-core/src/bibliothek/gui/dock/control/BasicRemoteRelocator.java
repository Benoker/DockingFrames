/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.control;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.dockable.MovingImage;
import bibliothek.gui.dock.title.DockTitle;

/**
 * Definition of properties shared by the {@link RemoteRelocator} and by the {@link DirectRemoteRelocator}.
 * @author Benjamin Sigg
 */
public interface BasicRemoteRelocator {
    /**
     * Sets the {@link DockTitle} which is dragged, the title might show up below the mouse.
     * @param title the title that is dragged, can be <code>null</code>
     */
    public void setTitle( DockTitle title );
    
    /**
     * Sets whether a {@link MovingImage} should appear during the drag and drop operation, the default
     * value is <code>true</code>.
     * @param imageWindow whether a preview of the moved {@link Dockable} should appear
     */
    public void setShowImageWindow( boolean imageWindow );
}
