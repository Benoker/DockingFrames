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
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

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
			DockUtilities.ensureTreeValidity( this, child );
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
				if( children == null ){
					return null;
				}
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				if( dockable != null ){
					dockable.setParent( StackDockPerspective.this );
				}
				return dockable;
			}
		});
		
		this.dockables = dockables;
		if( children != null ){
			selection = children.get( selected );
		}
	}
	
	/**
	 * Tells whether <code>placeholders</code> has the right format for this perspective to read it.
	 * @param placeholders the map whose format needs checking
	 * @return <code>true</code> if a call to {@link #setPlaceholders(PlaceholderMap)} will not result in an exception
	 */
	public boolean canRead( PlaceholderMap placeholders ){
		return PlaceholderList.PLACEHOLDER_MAP_FORMAT.equals( placeholders.getFormat() );
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
	 * Adds a placeholder for <code>dockable</code> at the end of the list of dockables.
	 * @param dockable the element for which a placeholder should be added
	 */
	public void addPlaceholder( PerspectiveDockable dockable ){
		insertPlaceholder( getDockableCount(), dockable );
	}
	
	/**
	 * Adds <code>placeholder</code> at the end of the list of dockables.
	 * @param placeholder the new placeholder
	 */
	public void addPlaceholder( Path placeholder ){
		insertPlaceholder( getDockableCount(), placeholder, Level.DOCKABLE );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> at location <code>index</code>.
	 * @param index the location where the placeholder goes
	 * @param dockable the element for which a placeholder should be left
	 */
	public void insertPlaceholder( int index, PerspectiveDockable dockable ){
		insertPlaceholder( index, dockable.getPlaceholder(), Level.DOCKABLE );
	}
	
	/**
	 * Adds <code>placeholder</code> at location <code>index</code>.
	 * @param index the location where the placeholder goes
	 * @param placeholder the new placeholder
	 * @param level at which level <code>index</code> should be evaluated, not <code>null</code>
	 */
	public void insertPlaceholder( int index, Path placeholder, Level level ){
		switch( level ){
			case BASE:
				dockables.list().insertPlaceholder( index, placeholder );
				break;
			case DOCKABLE:
				dockables.dockables().insertPlaceholder( index, placeholder );
				break;
			case PLACEHOLDER:
				dockables.purePlaceholders().insertPlaceholder( index, placeholder );
				break;
		}
	}

	/**
	 * Adds <code>dockable</code> at the end of the list of dockables.
	 * @param dockable the element to add
	 */
	public void add( PerspectiveDockable dockable ){
		insert( getDockableCount(), dockable );
	}
	
	/**
	 * Inserts <code>dockable</code> at location <code>index</code>.
	 * @param index the location
	 * @param dockable the new element
	 */
	public void insert( int index, PerspectiveDockable dockable ){
		DockUtilities.ensureTreeValidity( this, dockable );
				
		if( dockable == null ){
			throw new IllegalArgumentException( "dockable must not be null" );
		}
		dockables.dockables().add( index, dockable );
	}
	
	/**
	 * Gets the location of <code>dockable</code>.
	 * @param dockable the dockable whose location is searched
	 * @return the location or -1 if not found
	 */
	public int indexOf( PerspectiveDockable dockable ){
		return dockables.dockables().indexOf( dockable );
	}
	
	/**
	 * Gets the location where <code>placeholder</code> would be found if it were a 
	 * <code>dockable</code>.
	 * @param placeholder the placeholder to search
	 * @return the location of <code>placeholder</code> or <code>-1</code>
	 */
	public int indexOf( Path placeholder ){
		return dockables.getDockableIndex( placeholder );
	}
	
	/**
	 * Removes <code>dockable</code> from this station.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was removed, <code>false</code>
	 * otherwise
	 */
	public boolean remove( PerspectiveDockable dockable ){
		int index = indexOf( dockable );
		if( index < 0 ){
			return false;
		}
		remove( index );
		return true;
	}
	
	/**
	 * Removes the <code>index</code>'th child of this station. If the child is the
	 * {@link #setSelection(PerspectiveDockable) selected element}, then the selected
	 * element is set to <code>null</code>.
	 * @param index the location of the child
	 * @return the child that was removed
	 */
	public PerspectiveDockable remove( int index ){
		PerspectiveDockable result = dockables.dockables().get( index );
		dockables.remove( result );
		result.setParent( null );
		
		if( selection == result ){
			selection = null;
		}
		
		return result;
	}
	
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		int index = indexOf( oldDockable );
		if( index < 0 ){
			throw new IllegalArgumentException( "oldDockable is not a child of this station" );
		}
		DockUtilities.ensureTreeValidity( this, newDockable );
		
		boolean selected = selection == oldDockable;
		remove( index );
		insert( index, newDockable );
		if( selected ){
			setSelection( newDockable );
		}
	}
	
	/**
	 * Changes the selected element of this station.
	 * @param dockable the selected element, can be <code>null</code>
	 */
	public void setSelection( PerspectiveDockable dockable ){
		if( dockable != null && indexOf( dockable ) < 0 ){
			throw new IllegalArgumentException( "dockable is not a child of this station" );
		}
		this.selection = dockable;
	}
	
	/**
	 * Gets the currently selected element.
 	 * @return the selected child or <code>null</code>
	 */
	public PerspectiveDockable getSelection(){
		return selection;
	}
	
	public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target ){
		int index = indexOf( child );
		
		Path placeholder = null;
		if( target != null ){
			placeholder = target.getPlaceholder();
		}
		else{
			placeholder = child.getPlaceholder();
		}
		
		return new StackDockProperty( index, placeholder );
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
		return StackDockStationFactory.ID;
	}

	public PerspectiveDockable getDockable( int index ){
		return dockables.dockables().get( index );
	}

	public int getDockableCount(){
		return dockables.dockables().size();
	}
	
	/**
	 * Gets the number of children, this includes <code>dockables</code> and <code>placeholders</code>.
	 * @return the total number of children
	 */
	public int getItemCount(){
		return dockables.list().size();
	}
}
