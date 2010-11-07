/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack;

import java.util.Map;

import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;
import bibliothek.util.Todo;

/**
 * A representation of a {@link StackDockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public class StackDockPerspective implements PerspectiveDockable, PerspectiveStation{
	private PerspectivePlaceholderList<PerspectiveDockable> dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
	private PerspectiveDockable selection;
	
	private PerspectiveStation parent;
	
	/**
	 * Creates a new empty {@link StackDockPerspective}.
	 */
	public StackDockPerspective(){
		// nothing
	}
	
	/**
	 * Creates a new perspective.
	 * @param children the children of the new station
	 * @param selection the current selection, may be <code>null</code>
	 */
	public StackDockPerspective( PerspectiveDockable[] children, PerspectiveDockable selection ){
		boolean found = false;
		
		for( PerspectiveDockable child : children ){
			if( child.getParent() != null ){
				throw new IllegalArgumentException( "child already has a parent" );
			}
			child.setParent( this );
			dockables.dockables().add( child );
			if( child == selection ){
				found = true;
			}
		}
		
		if( selection != null && !found ){
			throw new IllegalArgumentException( "selected dockable is not child of this station" );
		}
		this.selection = selection;
	}

	/**
	 * Reads the contents of <code>map</code> and replaces any content of this perspective.
	 * @param placeholders the map to convert
	 * @param children the children of this station
	 * @param selected the selected child or -1
	 */
	public void read( PlaceholderMap placeholders, final Map<Integer, PerspectiveDockable> children, int selected ){
		PerspectivePlaceholderList<PerspectiveDockable> dockables = new PerspectivePlaceholderList<PerspectiveDockable>();
		dockables.read( placeholders, new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>(){
			@Override
			public PerspectiveDockable convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				dockable.setParent( StackDockPerspective.this );
				return dockable;
			}
		});
		
		this.dockables = dockables;
		selection = children.get( selection );
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		if( getDockableCount() > 0 ){
			throw new IllegalStateException( "there are already children on this station" );
		}
		
		dockables = new PerspectivePlaceholderList<PerspectiveDockable>( placeholders );
	}
	
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}
	
	/**
	 * Converts this perspective into a {@link PlaceholderMap}.
	 * @param children identifiers for the children of this station
	 * @return the new map
	 */
	public PlaceholderMap toMap( final Map<PerspectiveDockable, Integer> children ){
    	return dockables.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, PerspectiveDockable>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, PerspectiveDockable dockable ){
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			item.putInt( "id", children.get( dockable ) );
    			item.putInt( "index", index );
    			
    			Path placeholder = dockable.getPlaceholder();
    			if( placeholder != null ){
    				item.putString( "placeholder", placeholder.toString() );
    				item.setPlaceholder( placeholder );
    			}
    			
    			return item;
    		}
		});
	}

	/**
	 * Gets the currently selected element.
 	 * @return the selected child or <code>null</code>
	 */
	public PerspectiveDockable getSelection(){
		return selection;
	}
	
	public void setParent( PerspectiveStation parent ){
		this.parent = parent;
	}
	
	public PerspectiveStation getParent(){
		return parent;
	}

	@Todo
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
		return StackDockStationFactory.ID;
	}

	public PerspectiveDockable getDockable( int index ){
		return dockables.dockables().get( index );
	}

	public int getDockableCount(){
		return dockables.dockables().size();
	}
}
