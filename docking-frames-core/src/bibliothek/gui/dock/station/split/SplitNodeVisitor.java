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

/**
 * A visitor for a tree of {@link SplitNode SplitNodes}.
 * @author Benjamin Sigg
 */
public interface SplitNodeVisitor {
	/**
	 * Invoked when visiting the root of the tree.
	 * @param root the root
	 */
    public void handleRoot( Root root );
    
    /**
     * Invoked when visiting a node of the tree.
     * @param node a node
     */
    public void handleNode( Node node );
    
    /**
     * Invoked when visiting a leaf of the tree.
     * @param leaf the tree
     */
    public void handleLeaf( Leaf leaf );
    
    /**
     * Invoked when visiting a leaf that is a placeholder.
     * @param placeholder the placeholder
     */
    public void handlePlaceholder( Placeholder placeholder );
}
