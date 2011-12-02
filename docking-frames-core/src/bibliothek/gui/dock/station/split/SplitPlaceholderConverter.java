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
package bibliothek.gui.dock.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Path;

/**
 * Supporting class for {@link SplitDockStation}, provides the implementation of {@link SplitDockStation#getPlaceholders()}
 * and {@link SplitDockStation#setPlaceholders(PlaceholderMap)}
 * @author Benjamin Sigg
 */
public class SplitPlaceholderConverter extends AbstractSplitPlaceholderConverter<SplitDockStation, SplitNode, Dockable>{
	/**
	 * Creates a new converter
	 * @param station the station for which the converter will be used
	 */
	public SplitPlaceholderConverter( SplitDockStation station ){
		super( station );
	}
	
	@Override
	protected SplitNode getRoot( SplitDockStation station ){
		return station.getRoot();
	}
	
	@Override
	protected PlaceholderStrategy getPlaceholderStrategy( SplitDockStation station ){
		return station.getPlaceholderStrategy();
	}
	
	@Override
	protected SplitDockTree<Dockable> createTree(){
		return new DockableSplitDockTree();
	}
	
	@Override
	protected void dropTree( SplitDockStation station, SplitDockTree<Dockable> tree ){
		station.dropTree( tree );
	}
	
	@Override
	protected boolean isLeaf( SplitNode node ){
		return node instanceof Leaf;
	}
	
	@Override
	protected boolean isNode( SplitNode node ){
		return node instanceof Node;
	}
	
	@Override
	protected boolean isPlaceholder( SplitNode node ){
		return node instanceof Placeholder;
	}
	
	@Override
	protected boolean isRoot( SplitNode node ){
		return node instanceof Root;
	}
	
	@Override
	protected Path[] getPlaceholders( SplitNode node ){
		return node.getPlaceholders();
	}
	
	@Override
	protected PlaceholderMap getPlaceholderMap( SplitNode node ){
		return node.getPlaceholderMap();
	}
	
	@Override
	protected long getId( SplitNode node ){
		return node.getId();
	}
	
	@Override
	protected double getDivider( SplitNode node ){
		return ((Node)node).getDivider();
	}
	
	@Override
	protected Orientation getOrientation( SplitNode node ){
		return ((Node)node).getOrientation();
	}
	
	@Override
	protected Dockable getDockable( SplitNode leaf ){
		return ((Leaf)leaf).getDockable();
	}
	
	@Override
	protected SplitNode getLeftChild( SplitNode node ){
		return ((Node)node).getLeft();
	}
	
	@Override
	protected SplitNode getRightChild( SplitNode node ){
		return ((Node)node).getRight();
	}
	
	@Override
	protected SplitNode getRootChild( SplitNode root ){
		return ((Root)root).getChild();
	}
	
	@Override
	protected Path getPlaceholderFor( Dockable dockable ){
		return getStation().getPlaceholderStrategy().getPlaceholderFor( dockable );
	}
}
