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
package bibliothek.gui.dock.station.screen;

import java.awt.Rectangle;
import java.util.Map;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderList.Level;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.gui.dock.station.support.PlaceholderMetaMap;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.util.Path;

/**
 * A representation of a {@link ScreenDockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
public class ScreenDockPerspective implements PerspectiveStation{
	/** a list of all children and placeholders of this station */
	private PerspectivePlaceholderList<ScreenPerspectiveWindow> dockables = new PerspectivePlaceholderList<ScreenPerspectiveWindow>(); 
	
	/**
	 * Reads the contents of <code>map</code> and replaces any content of this perspective.
	 * @param map the layout
	 * @param children the children of this station
	 */
	public void read( PlaceholderMap map, final Map<Integer, PerspectiveDockable> children ){
    	PerspectivePlaceholderList<ScreenPerspectiveWindow> next = new PerspectivePlaceholderList<ScreenPerspectiveWindow>();
    	
    	next.read( map, new PlaceholderListItemAdapter<PerspectiveDockable, ScreenPerspectiveWindow>(){
			@Override
			public ScreenPerspectiveWindow convert( ConvertedPlaceholderListItem item ){
				if( children == null ){
					return null;
				}
				
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				if( dockable != null ){
					ScreenPerspectiveWindow child = new ScreenPerspectiveWindow( dockable );
					dockable.setParent( ScreenDockPerspective.this );
					child.setX( item.getInt( "x" ) );
					child.setY( item.getInt( "y" ) );
					child.setWidth( item.getInt( "width" ) );
					child.setHeight( item.getInt( "height" ) );
					child.setFullscreen( item.getBoolean( "fullscreen" ) );
					
					return child;
				}
				return null;
			}
		});	
    	
    	dockables = next;
    }
	
	public PerspectiveDockable getDockable( int index ){
		return dockables.dockables().get( index ).asDockable();
	}

	public int getDockableCount(){
		return dockables.dockables().size();
	}

	public PerspectiveDockable asDockable(){
		return null;
	}

	public PerspectiveStation asStation(){
		return this;
	}

	public String getFactoryID(){
		return ScreenDockStationFactory.ID;
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		if( getDockableCount() > 0 ){	
			throw new IllegalStateException( "there are already children on this station" );
		}
		dockables = new PerspectivePlaceholderList<ScreenPerspectiveWindow>( placeholders );
	}
	
	public PlaceholderMap getPlaceholders(){
		return dockables.toMap();
	}
	
	/**
	 * Converts the content of this perspective to a {@link PlaceholderMap} that can be
	 * stored persistently. 
	 * @param children unique identifiers for the children of this perspective
	 * @return the map, not <code>null</code>
	 */
	public PlaceholderMap toMap( final Map<PerspectiveDockable, Integer> children ){
    	return dockables.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, ScreenPerspectiveWindow>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, ScreenPerspectiveWindow child ) {
    			if( children == null ){
    				return null;
    			}
    			
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			item.putInt( "id", children.get( child.asDockable() ) );
    			item.putInt( "x", child.getX() );
    			item.putInt( "y", child.getY() );
    			item.putInt( "width", child.getWidth() );
    			item.putInt( "height", child.getHeight() );
    			item.putBoolean( "fullscreen", child.isFullscreen() );
	    		
    			Path placeholder = child.asDockable().getPlaceholder();
    			if( placeholder != null ){
    				item.putString( "placeholder", placeholder.toString() );
    				item.setPlaceholder( placeholder );
    			}
    			
    			return item;
    		}
		});
    }
	
	/**
	 * Adds <code>dockable</code> with boundaries <code>bounds</code> to this perspective.
	 * @param dockable the element to add
	 * @param bounds the boundaries of <code>dockable</code>
	 */
	public void add( PerspectiveDockable dockable, Rectangle bounds ){
		add( dockable, bounds, false );
	}
	
	/**
	 * Adds <code>dockable</code> width boundaries <code>bounds</code> to this perspective
	 * @param dockable the element to add, not <code>null</code>
	 * @param bounds the boundaries of <code>dockable</code>
	 * @param fullscreen whether <code>dockable</code> should be extended to fullscreen mode
	 */
	public void add( PerspectiveDockable dockable, Rectangle bounds, boolean fullscreen ){
		add( dockable, bounds.x, bounds.y, bounds.width, bounds.height, fullscreen );
	}

	/**
	 * Adds <code>dockable</code> at location <code>x/y</code> with size <code>width/height</code> to
	 * this perspective.
	 * @param dockable the element to add, not <code>null</code>
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public void add( PerspectiveDockable dockable, int x, int y, int width, int height ){
		add( dockable, x, y, width, height, false );
	}
	
	/**
	 * Adds <code>dockable</code> at location <code>x/y</code> with size <code>width/height</code> to
	 * this perspective.
	 * @param dockable the element to add, not <code>null</code>
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 * @param fullscreen whether <code>dockable</code> should be extended to fullscreen mode
	 */
	public void add( PerspectiveDockable dockable, int x, int y, int width, int height, boolean fullscreen ){
		DockUtilities.ensureTreeValidity( this, dockable );
		ScreenPerspectiveWindow child = new ScreenPerspectiveWindow( dockable );
		dockable.setParent( this );
		child.setX( x );
		child.setY( y );
		child.setWidth( width );
		child.setHeight( height );
		child.setFullscreen( fullscreen );
		dockables.dockables().add( child );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children to this
	 * station.
	 * @param dockable the element whose placeholder should be inserted
	 * @param bounds the location and size of <code>dockable</code>
	 */
	public void addPlaceholder( PerspectiveDockable dockable, Rectangle bounds ){
		addPlaceholder( dockable, bounds.x, bounds.y, bounds.width, bounds.height );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children to this
	 * station.
	 * @param dockable the element whose placeholder should be inserted
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public void addPlaceholder( PerspectiveDockable dockable, int x, int y, int width, int height ){
		ScreenPerspectiveWindow child = new ScreenPerspectiveWindow( dockable );
		child.setX( x );
		child.setY( y );
		child.setWidth( width );
		child.setHeight( height );
		child.setFullscreen( false );
		dockables.dockables().add( child );
		remove( dockable );
	}
	
	/**
	 * Adds <code>placeholder</code> to this station.
	 * @param placeholder the placeholder to add
	 * @param bounds the location and size of <code>placeholder</code>
	 */
	public void addPlaceholder( Path placeholder, Rectangle bounds ){
		addPlaceholder( placeholder, bounds.x, bounds.y, bounds.width, bounds.height );
	}
	
	/**
	 * Adds <code>placeholder</code> to this station.
	 * @param placeholder the placeholder to add
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public void addPlaceholder( Path placeholder, int x, int y, int width, int height ){
		int index = dockables.list().size();
		
		dockables.list().insertPlaceholder( index, placeholder );
		PlaceholderMetaMap map = dockables.list().getMetaMap( index );
		
		map.putInt( "x", x );
		map.putInt( "y", y );
		map.putInt( "width", width );
		map.putInt( "height", height );
	}
	
	/**
	 * Gets the index of <code>dockable</code>.
	 * @param dockable some child of this station
	 * @return the index or -1 if <code>dockable</code> was not found
	 */
	public int indexOf( PerspectiveDockable dockable ){
		int count = 0;
		for( ScreenPerspectiveWindow child : dockables.dockables() ){
			if( child.asDockable() == dockable ){
				return count;
			}
			count++;
		}
		return -1;
	}
	
	/**
	 * Removes the child <code>dockable</code> from this station.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was removed,
	 * <code>false</code> otherwise
	 */
	public boolean remove( PerspectiveDockable dockable ){
		int index = indexOf( dockable );
		if( index >= 0 ){
			remove( index );
			return true;
		}
		return false;
	}
	
	/**
	 * Removes the <code>index</code>'th element of this station.
	 * @param index the index of the element to remove
	 * @return the removed element
	 */
	public PerspectiveDockable remove( int index ){
		PlaceholderMetaMap map = dockables.dockables().getMetaMap( index );
		ScreenPerspectiveWindow child = dockables.dockables().get( index );
		
		map.putInt( "x", child.getX() );
		map.putInt( "y", child.getY() );
		map.putInt( "width", child.getWidth() );
		map.putInt( "height", child.getHeight() );
		
		dockables.remove( child );
		child.dockable.setParent( null );
		return child.dockable;
	}
	
	public void replace( PerspectiveDockable oldDockable, PerspectiveDockable newDockable ){
		int index = indexOf( oldDockable );
		if( index < 0 ){
			throw new IllegalArgumentException( "oldDockable is not a child of this station" );
		}
		DockUtilities.ensureTreeValidity( this, newDockable );
		
		ScreenPerspectiveWindow window = dockables.dockables().get( index );
		
		Path placeholder = oldDockable.getPlaceholder();
		if( placeholder != null ){
			dockables.put( placeholder, window );
		}
		
		oldDockable.setParent( null );
		newDockable.setParent( this );
		window.dockable = newDockable;

		if( oldDockable.asStation() != null ){
			int listIndex = dockables.levelToBase( index, Level.DOCKABLE );
			PerspectivePlaceholderList<ScreenPerspectiveWindow>.Item item = dockables.list().get( listIndex );
			item.setPlaceholderMap( oldDockable.asStation().getPlaceholders() );
		}
	}
	
	/**
	 * Gets access to the window that shows <code>dockable</code>
	 * @param dockable the element whose window is requested
	 * @return the window or <code>null</code> if <code>dockable</code> was not found
	 */
	public ScreenPerspectiveWindow getWindow( PerspectiveDockable dockable ){
		for( ScreenPerspectiveWindow child : dockables.dockables() ){
			if( child.asDockable() == dockable ){
				return child;
			}
		}
		return null;
	}
	
	public DockableProperty getDockableProperty( PerspectiveDockable child, PerspectiveDockable target ){
		ScreenPerspectiveWindow window = getWindow( child );
		if( window == null ){
			throw new IllegalArgumentException( "child is not a child of this station" );
		}
		
		Path placeholder = null;
		if( target != null ){
			placeholder = target.getPlaceholder();
		}
		else{
			placeholder = child.getPlaceholder();
		}
		
		return new ScreenDockProperty( window.getX(), window.getY(), window.getWidth(), window.getHeight(), placeholder, window.isFullscreen() );
	}
	
	/**
	 * Represents a child of a {@link ScreenDockPerspective}.
	 * @author Benjamin Sigg
	 */
	public static class ScreenPerspectiveWindow implements PlaceholderListItem<PerspectiveDockable>{
		private PerspectiveDockable dockable;
		
		private int x;
		private int y;
		private int width;
		private int height;
		private boolean fullscreen;
		
		/**
		 * Creates a new object.
		 * @param dockable the element which is represented by <code>this</code>
		 */
		public ScreenPerspectiveWindow( PerspectiveDockable dockable ){
			if( dockable == null ){
				throw new IllegalArgumentException( "dockable must not be null" );
			}
			this.dockable = dockable;
		}

		public PerspectiveDockable asDockable(){
			return dockable;
		}
		
		/**
		 * Gets the x-coordinate of this dockable on the screen.
		 * @return the x-coordinate
		 */
		public int getX(){
			return x;
		}
		
		/**
		 * Sets the x-coordinate of this dockable on the screen.
		 * @param x the x-coordinate
		 */
		public void setX( int x ){
			this.x = x;
		}
		
		/**
		 * Gets the y-coordinate of this dockable on the screen.
		 * @return the y-coordinate
		 */
		public int getY(){
			return y;
		}
		
		/**
		 * Sets the y-coordinate of this dockable on the screen. 
		 * @param y the y-coordinate
		 */
		public void setY( int y ){
			this.y = y;
		}
		
		/**
		 * Gets the width of this dockable in pixels.
		 * @return the width
		 */
		public int getWidth(){
			return width;
		}
		
		/**
		 * Sets the width of this dockable in pixels.
		 * @param width the width
		 */
		public void setWidth( int width ){
			if( width < 0 ){
				throw new IllegalArgumentException( "width must be >= 0: " + width );
			}
			this.width = width;
		}
		
		/**
		 * Gets the height of this dockable in pixels.
		 * @return the height
		 */
		public int getHeight(){
			return height;
		}
		
		/**
		 * Sets the height of this dockable in pixels.
		 * @param height the height
		 */
		public void setHeight( int height ){
			if( height < 0 ){
				throw new IllegalArgumentException( "height must be >= 0: " + height );
			}
			this.height = height;
		}
		
		/**
		 * Tells whether this dockable is shown in fullscreen-mode or not.
		 * @return <code>true</code> if fullscreen-mode is active
		 */
		public boolean isFullscreen(){
			return fullscreen;
		}
		
		/**
		 * Sets whether this dockable is shown in fullscreen-mode or not.
		 * @param fullscreen whether to activate fullscreen-mode
		 */
		public void setFullscreen( boolean fullscreen ){
			this.fullscreen = fullscreen;
		}
	}
}
