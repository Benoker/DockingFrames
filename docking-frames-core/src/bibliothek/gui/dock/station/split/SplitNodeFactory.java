/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.util.FrameworkOnly;

/**
 * A {@link SplitNodeFactory} is responsible for creating the various {@link SplitNode}s 
 * that are used by a {@link SplitDockStation}.<br>
 * This interface is not intended for clients to use or implement, it is purely an internal abstraction
 * keeping an option for customized subclasses of {@link SplitDockStation} open. 
 * @author Benjamin Sigg
 */
@FrameworkOnly
public interface SplitNodeFactory {
    /**
     * Creates a new leaf.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param id the unique id of this leaf
     * @return the new leaf, must not be <code>null</code>
     */
	public Leaf createLeaf( SplitDockAccess access, long id );
	
    /**
     * Creates a new node.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param id the unique id of this node
     * @return the new node, must not be <code>null</code>
     */	
	public Node createNode( SplitDockAccess access, long id );
	
    /**
     * Creates a new placeholder.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param id the unique id of this placeholder
     * @return the new placeholder, must not be <code>null</code>
     */
	public Placeholder createPlaceholder( SplitDockAccess access, long id );
	
    /**
     * Creates a new root.
     * @param access the access to the private functions of the owning {@link SplitDockStation}
     * @param id the unique id of this root
     * @return the new root, must not be <code>null</code>
     */
	public Root createRoot( SplitDockAccess access, long id );
}
