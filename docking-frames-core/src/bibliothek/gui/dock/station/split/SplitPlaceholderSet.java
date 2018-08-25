/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.FrameworkOnly;
import bibliothek.util.Path;
import bibliothek.util.container.Single;

/**
 * Keeps track of the various placeholders and {@link Dockable}s of a {@link SplitDockStation}
 * and makes sure that a placeholder is used by no more than one {@link SplitNode}. 
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class SplitPlaceholderSet {
	/** access to the owner of this set */
	private SplitDockAccess access;
	
	/**
	 * Creates a new set.
	 * @param access Access to the owner of this set, not <code>null</code>
	 */
	public SplitPlaceholderSet( SplitDockAccess access ){
		this.access = access;
		
	}
	
	/**
	 * Ensures that <code>node</code> is associated with the placeholder for <code>dockable</code>.
	 * @param node the owner of <code>dockable</code>, can be <code>null</code> to indicate
	 * that no-one must use the placeholder of <code>dockable</code>
	 * @param dockable the item whose placeholder is updated
	 * @param protectedNodes nodes that will not be removed even if they are no longer {@link SplitNode#isOfUse() useful}
	 */
	public void set( SplitNode node, Dockable dockable, SplitNode... protectedNodes ){
		PlaceholderStrategy strategy = access.getOwner().getPlaceholderStrategy();
		if( strategy != null ){
			Path placeholder = strategy.getPlaceholderFor( dockable );
			if( placeholder != null ){
				set( node, placeholder, protectedNodes );
			}
		}
	}
	
    /**
     * Ensures that <code>node</code> is associated with <code>placeholder</code> 
     * but no other node has <code>placeholder</code>.
     * @param node the node which must have <code>placeholder</code>, <code>null</code> to
     * indicate that no-one must use <code>placeholder</code>
     * @param placeholder the placeholder to set or to move
     * @param protectedNodes nodes that will not be removed even if they are no longer {@link SplitNode#isOfUse() useful}
     */
    public void set( final SplitNode node, final Path placeholder, final SplitNode... protectedNodes ){
    	final List<SplitNode> nodesToDelete = new ArrayList<SplitNode>();
    	
    	Root root = access.getOwner().getRoot();
    	root.visit( new SplitNodeVisitor() {
			public void handleRoot( Root root ){
				handle( root );
			}
			
			public void handlePlaceholder( Placeholder placeholder ){
				handle( placeholder );
			}
			
			public void handleNode( Node node ){
				handle( node );
			}
			
			public void handleLeaf( Leaf leaf ){
				handle( leaf );
			}
			
			private void handle( SplitNode check ){
				if( check != node ){
					check.removePlaceholder( placeholder );
					PlaceholderMap map = check.getPlaceholderMap();
					if( map != null ){
						map.removeAll( placeholder, true );
					}
					if( !check.isOfUse() ){
						nodesToDelete.add( check );
					}
				}
			}
		});
    	
    	if( node != null ){
    		node.addPlaceholder( placeholder );
    	}
    	
    	if( access.isTreeAutoCleanupEnabled() ){
    		for( SplitNode protectedNode : protectedNodes ){
    			nodesToDelete.remove( protectedNode );
    		}
    		
    		for( SplitNode delete : nodesToDelete ){
    			delete.delete( true );
    		}
    	}
    }

    /**
     * Removes any placeholder from <code>placeholderMap</code> that is stored in another node
     * than <code>node</code>.
     * @param node the node which must not be searched, can be <code>null</code>
     * @param map the map from which placeholders will be removed
     */
	public void removeDoublePlaceholders( final SplitNode node, final PlaceholderMap map ){
		final Set<Path> placeholdersToRemove = new HashSet<Path>();
		final PlaceholderStrategy strategy = access.getOwner().getPlaceholderStrategy();
		
		Root root = access.getOwner().getRoot();
		root.visit( new SplitNodeVisitor() {
			public void handleRoot( Root root ){
				handle( root );
			}
			
			public void handlePlaceholder( Placeholder placeholder ){
				handle( placeholder );
			}
			
			public void handleNode( Node node ){
				handle( node );
			}
			
			public void handleLeaf( Leaf leaf ){
				if( leaf != node ){
					handle( leaf );
					handle( leaf.getDockable() );
				}
			}
			
			private void handle( SplitNode check ){
				if( check != node ){
					for( Path placeholder : check.getPlaceholders() ){
						placeholdersToRemove.add( placeholder );
					}
				}
			}
			
			private void handle( Dockable dockable ){
				if( strategy != null && dockable != null ){
					Path placeholder = strategy.getPlaceholderFor( dockable );
					if( placeholder != null ){
						placeholdersToRemove.add( placeholder );
					}
					DockStation station = dockable.asDockStation();
					if( station != null ){
						for( int i = 0, n = station.getDockableCount(); i<n; i++ ){
							handle( station.getDockable( i ) );
						}
					}
				}
			}
		});
		
		map.removeAll( placeholdersToRemove, true );
	}

	/**
	 * Visits all nodes of the tree, searching for <code>placeholder</code>
	 * @param placeholder the placeholder so search
	 * @return <code>true</code> if a node was found containing <code>placeholder</code>
	 * @see SplitNode#hasPlaceholder(Path)
	 */
	public boolean contains( final Path placeholder ) {
		Root root = access.getOwner().getRoot();

		final Single<Boolean> result = new Single<Boolean>( false );

		root.visit( new SplitNodeVisitor() {
			public void handleRoot( Root root ) {
				if( root.hasPlaceholder( placeholder ) ) {
					result.setA( true );
				}
			}

			public void handlePlaceholder( Placeholder node ) {
				if( node.hasPlaceholder( placeholder ) ) {
					result.setA( true );
				}
			}

			public void handleNode( Node node ) {
				if( node.hasPlaceholder( placeholder ) ) {
					result.setA( true );
				}
			}

			public void handleLeaf( Leaf leaf ) {
				if( leaf.hasPlaceholder( placeholder ) ) {
					result.setA( true );
				}
			}
		} );

		return result.getA();
	}
}
