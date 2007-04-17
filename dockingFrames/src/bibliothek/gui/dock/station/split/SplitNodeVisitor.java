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
}
