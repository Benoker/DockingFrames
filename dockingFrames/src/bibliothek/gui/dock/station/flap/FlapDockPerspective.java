
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
package bibliothek.gui.dock.station.flap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;
import bibliothek.util.Todo;

/**
 * A representation of a {@link FlapDockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public class FlapDockPerspective implements PerspectiveDockable, PerspectiveStation{
	private PerspectiveStation parent;
	private List<PerspectiveDockable> dockables = new ArrayList<PerspectiveDockable>();

	/**
	 * Updates the content of this perspective by reading the contents of <code>map</code>.
	 * @param map the placeholders
	 * @param children the possible children of this perspective
	 */
	@Todo
	public void read( PlaceholderMap map, final Map<Integer, PerspectiveDockable> children ){
		PerspectivePlaceholderList.simulatedRead( map, new PlaceholderListItemAdapter<PerspectiveDockable, PlaceholderListItem<PerspectiveDockable>>(){
			@Override
			public PlaceholderListItem<PerspectiveDockable> convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				if( dockable != null ){
//					boolean hold = item.getBoolean( "hold" );
//					int size = item.getInt( "size" );
					
					dockables.add( dockable );
					dockable.setParent( FlapDockPerspective.this );
			        
//			        setHold( dockable, hold );
//			        setWindowSize( dockable, size );
				}
				return null;
			}
		});
	}
	
	@Todo
	public PlaceholderMap toMap( final Map<PerspectiveDockable, Integer> children ){
		PerspectivePlaceholderList<PerspectiveDockable> list = new PerspectivePlaceholderList<PerspectiveDockable>();
		for( PerspectiveDockable dockable : dockables ){
			list.dockables().add( dockable );
		}
		
		return list.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			@Override
			public ConvertedPlaceholderListItem convert( int index, PerspectiveDockable dockable ){
				ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
				item.putInt( "id", children.get( dockable.asDockable() ) );
				item.putInt( "index", index );
				item.putBoolean( "hold", false ); // TODO
				item.putInt( "size", 50 ); // TODO
				
//				item.putString( "placeholder", value );
//				item.setPlaceholder( placeholder );
				return item;
			}
		});
	}
	
	/**
	 * Adds a {@link Dockable} to this station.
	 * @param index the location of the new child
	 * @param dockable the new child, not <code>null</code>, must not have a parent
	 */
	public void insert( int index, PerspectiveDockable dockable ){
		if( dockable.getParent() != null ){
			throw new IllegalArgumentException( "dockable already has a parent" );
		}
		dockables.add( index, dockable );
		dockable.setParent( this );
	}
	
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;
	}
	
	public PerspectiveStation getParent(){
		return parent;
	}

	public Path getPlaceholder(){
		return null;
	}

	public PerspectiveDockable asDockable(){
		return this;
	}

	public PerspectiveStation asStation(){
		return this;
	}

	public String getFactoryID(){
		return FlapDockStationFactory.ID;
	}

	public PerspectiveDockable getDockable( int index ){
		return dockables.get( index );
	}

	public int getDockableCount(){
		return dockables.size();
	}
}
