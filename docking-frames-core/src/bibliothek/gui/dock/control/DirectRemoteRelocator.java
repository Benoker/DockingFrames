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
package bibliothek.gui.dock.control;

import bibliothek.gui.Dockable;

/**
 * A simplified version of {@link RemoteRelocator}. This direct remote relocator
 * assumes that the correct mouse buttons are always pressed. A client
 * can initiate a {@literal drag & drop} operation by calling {@link #init(int, int, int, int) init}.
 * Then the client has to call at least one time {@link #drag(int, int, boolean) drag}, before
 * he can invoke {@link #drop(int, int) drop} to let the dragged {@link Dockable}
 * fall down. A client can {@link #cancel() cancel} the operation at any time.<br>
 * Note that only one operation is supported at once. Note also that there is 
 * no guarantee, that a {@literal drag & drop} operation is executed successfully.
 * @author Benjamin Sigg
 *
 */
public interface DirectRemoteRelocator extends BasicRemoteRelocator{
    /**
     * This method starts a {@literal drag & drop} operation. This method simulates
     * a mouse-pressed event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param dx the x-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     * @param dy the y-coordinate of the mouse on the simulated Component which sent the event, 0 is a good default-value.
     */
    public void init( int x, int y, int dx, int dy );
    
    /**
     * Gives a feedback to the user, that a {@link Dockable} is moved around.<br>
     * Prepares for a drop-event.
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     * @param always <code>true</code> if a call to this method should always
     * result in a drag-event, <code>false</code> if the restrictions of the
     * {@link DockRelocator} should be respected. A restriction might be, that
     * the location of the mouse must have a minimal distance to the initial
     * location of the mouse.
     * @see DockRelocator#getDragDistance()
     */
    public void drag( int x, int y, boolean always );
    
    /**
     * Stops a dnd-operation either by dropping the {@link Dockable} 
     * (if possible) or by canceling the operation.     
     * @param x the x-coordinate on the screen, where the (simulated) event occurred
     * @param y the y-coordinate on the screen, where the (simulated) event occurred
     */
    public void drop( int x, int y );
    
    /**
     * Cancels the current {@literal drag & drop} operation.
     */
    public void cancel();
}
