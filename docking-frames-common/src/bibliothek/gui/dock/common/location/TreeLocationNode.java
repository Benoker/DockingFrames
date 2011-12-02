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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.station.split.SplitDockPathProperty;

/**
 * A turn in a path to an element, beginning at the "normalized-area".
 * @author Benjamin Sigg
 */
public class TreeLocationNode extends AbstractTreeLocation {
	/** the turn above this location */
	private AbstractTreeLocation parent;
	
	/**
	 * Creates a new node.
	 * @param parent the turn above this location
	 * @param size the relative size of this location
	 * @param side the side which is occupied by this location
	 * @param nodeId the unique identifier of the node represented by this location, can be -1
	 * @see AbstractTreeLocation#AbstractTreeLocation(double, Side, long)
	 */
	public TreeLocationNode( AbstractTreeLocation parent, double size, Side side, long nodeId ){
		super( size, side, nodeId );
		if( parent == null )
			throw new NullPointerException( "parent must not be null" );
		this.parent = parent;
	}

	@Override
	public AbstractTreeLocation getParent(){
		return parent;
	}
	
	@Override
	protected SplitDockPathProperty findParentProperty(){
		return parent.findProperty( null );
	}

	@Override
	public String findRoot(){
		return parent.findRoot();
	}

	@Override
	public String toString() {
	    return String.valueOf( parent ) + " " + super.toString();
	}
}
