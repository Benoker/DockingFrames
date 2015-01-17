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

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.PlaceholderMapping;
import bibliothek.util.Path;

/**
 * A {@link PlaceholderMapping} representing the placeholders on a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public class SplitDockPlaceholderMapping implements PlaceholderMapping{
	/** the owner and creator of this mapping */
	private SplitDockStation station;
	
	/**
	 * Creates a new mapping for <code>station</code>. Clients should not call this method, instead they
	 * should call {@link SplitDockStation#getPlaceholderMapping()}.
	 * @param station the source of the mapping
	 */
	public SplitDockPlaceholderMapping( SplitDockStation station ){
		this.station = station;
	}
	
	public SplitDockStation getStation() {
		return station;
	}
	
	public void addPlaceholder( Dockable dockable, Path placeholder ) {
		if( dockable == null )
			throw new IllegalArgumentException( "dockable must not be null" );
		
		if( placeholder == null )
			throw new IllegalArgumentException( "placeholder must not be null" );
		
		Leaf leaf = station.getRoot().getLeaf( dockable );
		if( leaf == null ){
			throw new IllegalArgumentException( "unable to find location of dockable" );
		}
		removePlaceholder( leaf, placeholder );
		leaf.addPlaceholder( placeholder );
	}
	
	@Override
	public void removePlaceholder( Path placeholder ) {
		removePlaceholder( null, placeholder );
	}
	
	private void removePlaceholder( final Leaf ignore, final Path placeholder ) {
		final List<Placeholder> nodesToRemove = new ArrayList<Placeholder>();
		
		station.getRoot().visit( new SplitNodeVisitor() {
			@Override
			public void handleRoot( Root root ) {
				root.removePlaceholder( placeholder );
			}
			
			@Override
			public void handlePlaceholder( Placeholder node ) {
				node.removePlaceholder( placeholder );
				if( !node.isOfUse() ){
					nodesToRemove.add( node );
				}
			}
			
			@Override
			public void handleNode( Node node ) {
				node.removePlaceholder( placeholder );
			}
			
			@Override
			public void handleLeaf( Leaf leaf ) {
				if( leaf != ignore ){
					leaf.removePlaceholder( placeholder );
				}
			}
		});
		
		for( Placeholder node : nodesToRemove ){
			node.delete( true );
		}
	}
}
