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
package bibliothek.gui.dock.station.split;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A factory used to create trees that somehow represent the layout
 * of a {@link SplitDockStation}.
 * @author Benjamin Sigg
 *
 * @param <N> the type of elements representing leafs and nodes
 * @see SplitDockStation#visit(SplitTreeFactory)
 */
public interface SplitTreeFactory<N> {
    /**
     * Informs about a leaf of the tree.
     * @param dockable the element in the leaf
     * @param id the unique identifier of this node or -1
     * @param placeholders the placeholders associated with this node, can be empty
     * @param placeholderMap placeholder information of a child {@link DockStation}
     * @return the representation of the leaf or <code>null</code>
     */
    public N leaf( Dockable dockable, long id, Path[] placeholders, PlaceholderMap placeholderMap );
    
    /**
     * Informs about a set of placeholder in the tree.
     * @param id the unique id of this placeholder
     * @param placeholders the placeholders 
     * @param placeholderMap placeholder information of a child {@link DockStation}
     * @return the representation of the placeholder or <code>null</code>
     */
    public N placeholder( long id, Path[] placeholders, PlaceholderMap placeholderMap );
    
    /**
     * Informs about a node that is divided vertically.
     * @param left the left child of the node, might be <code>null</code>
     * @param right the right child of the node, might be <code>null</code>
     * @param divider the size of the left node, a value between 0 and 1.
     * @param id the unique identifier of this node or -1
     * @param placeholders the placeholders associated with this node, can be empty
     * @param placeholderMap placeholder information of a child {@link DockStation}
     * @param visible whether this node is visible to the user or not. A node is only visible to the user
     * if at least one of its children is visible
     * @return the representation of this node, might be <code>null</code>
     */
    public N horizontal( N left, N right, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible );
    
    /**
     * Informs about a node that is divided vertically.
     * @param top the top child of the node, might be <code>null</code>
     * @param bottom the bottom child of the node, might be <code>null</code>
     * @param divider the size of the top node, a value between 0 and 1.
     * @param id the unique identifier of this node or -1
     * @param placeholders the placeholders associated with this node, can be empty
     * @param placeholderMap placeholder information of a child {@link DockStation}
     * @param visible whether this node is visible to the user or not. A node is only visible to the user
     * if at least one of its children is visible
     * @return the representation of this node, might be <code>null</code>
     */
    public N vertical( N top, N bottom, double divider, long id, Path[] placeholders, PlaceholderMap placeholderMap, boolean visible );
    
    /**
     * Informs about the node that is the root.
     * @param root the root of the tree, might be <code>null</code>
     * @param id the unique identifier of this node or -1
     * @return the tree itself, or <code>null</code>
     */
    public N root( N root, long id );
}
