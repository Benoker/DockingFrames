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

import java.awt.Component;

/**
 * Moving images are used when a drag&drop operation is in progress, and
 * an object has to be shown somehow on the screen.
 * @author Benjamin Sigg
 *
 */
public interface MovingImage {
    /**
     * Gets a Component which represents the {@link MovingImage}. 
     * This method must always return the same Component.
     * @return always the same Component
     */
    public Component getComponent();
    
    /**
     * Called before this image is displayed. The method should connect the
     * image with other objects, like the object it represents.<br>
     * This method is never called twice in a row.
     */
    public void bind();
    
    /**
     * The reverse of {@link #bind()}. The image should remove any connections
     * to other objects.<br>
     * This method is never called twice in a row.
     */
    public void unbind();
}
