/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.facile.station.split;

/**
 * A {@link ConflictResolver} determines how to merge {@link ResizeRequest}s
 * and later how to resolve situations where two requests have to be handled
 * at the same time.
 * @author Benjamin Sigg
 */
public interface ConflictResolver<T> {
    
    /**
     * Combines the two request <code>left</code> and <code>right</code> into
     * a new request.
     * @param left the request from the left, can be <code>null</code>
     * @param right the request from the right, can be <code>null</code>
     * @param node the node for which the requests have to be merged
     * @return a new request that somehow represents <code>left</code> and <code>right</code>
     * or <code>null</code> to cancel the requests
     */
    public ResizeRequest requestHorizontal( ResizeRequest left, ResizeRequest right, ResizeNode<T> node );
    
    /**
     * Combines the two requests <code>top</code> and <code>bottom</code> into
     * a new request.
     * @param top the request from the top, can be <code>null</code>
     * @param bottom the request from the bottom, can be <code>null</code>
     * @param node the node for which the requests have to be merged
     * @return a new request that somehow represents <code>top</code> and <code>bottom</code>
     * or <code>null</code> to cancel the requests
     */
    public ResizeRequest requestVertical( ResizeRequest top, ResizeRequest bottom, ResizeNode<T> node );
    
    /**
     * Called whenever a node has to grant two resize requests at the same time.
     * @param node the node at which a conflict occurred
     * @param left the request from the left child
     * @param deltaLeft how much the left child would move the divider
     * @param right the request from the right child
     * @param deltaRight how much the right child would move the divider
     * @return how much the divider should be moved
     */
    public double resolveHorizontal( ResizeNode<T> node, ResizeRequest left, double deltaLeft, ResizeRequest right, double deltaRight );
    
    /**
     * Called whenever a node has to grant two resize requests at the same time.
     * @param node the node at which a conflict occurred
     * @param top the request from the top child
     * @param deltaTop how much the top child would move the divider
     * @param bottom the request from the bottom child
     * @param deltaBottom how much the bottom child would move the divider
     * @return how much the divider should be moved
     */
    public double resolveVertical( ResizeNode<T> node, ResizeRequest top, double deltaTop, ResizeRequest bottom, double deltaBottom );
}
