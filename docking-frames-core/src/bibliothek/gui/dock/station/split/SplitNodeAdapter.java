/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
 * An implementation of {@link SplitNodeVisitor}, all methods call {@link #handle(SplitNode)}.
 * @author Benjamin Sigg
 */
public abstract class SplitNodeAdapter implements SplitNodeVisitor{
	public void handleLeaf( Leaf leaf ) {
		handle( leaf );
	}
	
	public void handleNode( Node node ) {
		handle( node );
	}
	
	public void handlePlaceholder( Placeholder placeholder ) {
		handle( placeholder );
	}
	
	public void handleRoot( Root root ) {
		handle( root );
	}
	
	protected void handle( SplitNode node ){
		// do nothing
	}
}
