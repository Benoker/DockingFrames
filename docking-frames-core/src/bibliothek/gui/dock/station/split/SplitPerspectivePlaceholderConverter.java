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

import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation.Orientation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.split.SplitDockPerspective.Entry;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.station.support.PlaceholderStrategyListener;
import bibliothek.util.Path;

/**
 * A helper class to read and write {@link PlaceholderMap} that are related to {@link SplitDockPerspective}s. 
 * @author Benjamin Sigg
 */
public class SplitPerspectivePlaceholderConverter extends AbstractSplitPlaceholderConverter<SplitDockPerspective, SplitDockPerspective.Entry, PerspectiveDockable>{
	/**
	 * Creates a new converter
	 * @param station the perspective for which this converter is used
	 */
	public SplitPerspectivePlaceholderConverter( SplitDockPerspective station ){
		super( station );
	}
	
	@Override
	protected Entry getRoot( SplitDockPerspective station ){
		return station.getRoot();
	}
	
	@Override
	protected boolean isRoot( Entry node ){
		return node instanceof SplitDockPerspective.Root;
	}
	
	@Override
	protected boolean isNode( Entry node ){
		return node instanceof SplitDockPerspective.Node;
	}
	
	@Override
	protected boolean isLeaf( Entry node ){
		if( node instanceof SplitDockPerspective.Leaf ){
			return ((SplitDockPerspective.Leaf)node).getDockable() != null;
		}
		return false;
	}
	
	@Override
	protected boolean isPlaceholder( Entry node ){
		if( node instanceof SplitDockPerspective.Leaf ){
			return ((SplitDockPerspective.Leaf)node).getDockable() == null;
		}
		return false;
	}
	
	@Override
	protected Entry getRootChild( Entry root ){
		return ((SplitDockPerspective.Root)root).getChild();
	}
	
	@Override
	protected Entry getLeftChild( Entry node ){
		return ((SplitDockPerspective.Node)node).getChildA();
	}
	
	@Override
	protected Entry getRightChild( Entry node ){
		return ((SplitDockPerspective.Node)node).getChildB();
	}
	
	@Override
	protected double getDivider( Entry node ){
		return ((SplitDockPerspective.Node)node).getDivider();
	}
	
	@Override
	protected Orientation getOrientation( Entry node ){
		return ((SplitDockPerspective.Node)node).getOrientation();
	}
	
	@Override
	protected PerspectiveDockable getDockable( Entry leaf ){
		return ((SplitDockPerspective.Leaf)leaf).getDockable();
	}
	
	@Override
	protected long getId( Entry node ){
		return node.getNodeId();
	}
	
	@Override
	protected SplitDockTree<PerspectiveDockable> createTree(){
		return new PerspectiveSplitDockTree();
	}
	
	@Override
	protected void dropTree( SplitDockPerspective station, SplitDockTree<PerspectiveDockable> tree ){
		station.read( (PerspectiveSplitDockTree)tree, null );	
	}
	
	@Override
	protected Path[] getPlaceholders( Entry node ){
		Set<Path> set = node.getPlaceholders();
		if( set == null || set.isEmpty() ){
			return null;
		}
		return set.toArray( new Path[ set.size() ] );
	}
	
	@Override
	protected PlaceholderMap getPlaceholderMap( Entry node ){
		return node.getPlaceholderMap();
	}
	
	@Override
	protected PlaceholderStrategy getPlaceholderStrategy( SplitDockPerspective station ){
		return new PlaceholderStrategy(){
			public void uninstall( DockStation station ){
				// ignore
			}
			
			public void removeListener( PlaceholderStrategyListener listener ){
				// ignore
			}
			
			public boolean isValidPlaceholder( Path placeholder ){
				return true;
			}
			
			public void install( DockStation station ){
				// ignore
			}
			
			public Path getPlaceholderFor( Dockable dockable ){
				return null;
			}
			
			public void addListener( PlaceholderStrategyListener listener ){
				// ignore
			}
		};
	}
	
	@Override
	protected Path getPlaceholderFor( PerspectiveDockable dockable ){
		return dockable.getPlaceholder();
	}
}
