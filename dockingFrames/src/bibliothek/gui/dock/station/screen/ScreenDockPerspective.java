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

import java.util.Map;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.support.ConvertedPlaceholderListItem;
import bibliothek.gui.dock.station.support.PerspectivePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderListItemAdapter;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Todo;

/**
 * A representation of a {@link ScreenDockStation} in a {@link Perspective}.
 * @author Benjamin Sigg
 */
@Todo
public class ScreenDockPerspective implements PerspectiveStation{
	/** a list of all children and placeholders of this station */
	private PerspectivePlaceholderList<ScreenChild> dockables = new PerspectivePlaceholderList<ScreenChild>(); 
	
	/**
	 * Reads the contents of <code>map</code> and replaces any content of this perspective.
	 * @param map the layout
	 * @param children the children of this station
	 */
	public void read( PlaceholderMap map, final Map<Integer, PerspectiveDockable> children ){
    	PerspectivePlaceholderList<ScreenChild> next = new PerspectivePlaceholderList<ScreenChild>();
    	
    	next.read( map, new PlaceholderListItemAdapter<PerspectiveDockable, ScreenChild>(){
			@Override
			public ScreenChild convert( ConvertedPlaceholderListItem item ){
				int id = item.getInt( "id" );
				PerspectiveDockable dockable = children.get( id );
				if( dockable != null ){
					ScreenChild child = new ScreenChild( dockable );
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
	
	/**
	 * Converts the content of this perspective to a {@link PlaceholderMap} that can be
	 * stored persistently. 
	 * @param children unique identifiers for the children of this perspective
	 * @return the map, not <code>null</code>
	 */
	@Todo
	public PlaceholderMap toMap( final Map<PerspectiveDockable, Integer> children ){
//    	final PlaceholderStrategy strategy = getPlaceholderStrategy();
    	
    	return dockables.toMap( new PlaceholderListItemAdapter<PerspectiveDockable, ScreenChild>() {
    		@Override
    		public ConvertedPlaceholderListItem convert( int index, ScreenChild child ) {
    			ConvertedPlaceholderListItem item = new ConvertedPlaceholderListItem();
    			item.putInt( "id", children.get( child.asDockable() ) );
    			item.putInt( "x", child.getX() );
    			item.putInt( "y", child.getY() );
    			item.putInt( "width", child.getWidth() );
    			item.putInt( "height", child.getHeight() );
    			item.putBoolean( "fullscreen", child.isFullscreen() );
	    		
//	    		if( strategy != null ){
//	    			Path placeholder = strategy.getPlaceholderFor( dockable.asDockable() );
//	    			if( placeholder != null ){
//	    				item.putString( "placeholder", placeholder.toString() );
//	    				item.setPlaceholder( placeholder );
//	    			}
//	    		}
    			return item;
    		}
		});
    }
	
	/**
	 * Represents a child of a {@link ScreenDockPerspective}.
	 * @author Benjamin Sigg
	 */
	public static class ScreenChild implements PlaceholderListItem<PerspectiveDockable>{
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
		public ScreenChild( PerspectiveDockable dockable ){
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
