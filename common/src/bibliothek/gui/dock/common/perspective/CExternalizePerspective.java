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
package bibliothek.gui.dock.common.perspective;

import java.awt.Rectangle;

import bibliothek.gui.dock.common.CExternalizeArea;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.station.screen.ScreenDockPerspective;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * A representation of a {@link CExternalizeArea}.
 * @author Benjamin Sigg
 */
public class CExternalizePerspective implements CStationPerspective{
	/** the intern representation of this perspective */
	private CommonScreenDockPerspective delegate;
	
	/** the unique identifer of this perspective */
	private String id;
	
	/**
	 * Creates a new, empty perspective.
	 * @param id the unique identifier of this perspective
	 */
	public CExternalizePerspective( String id ){
		if( id == null ){
			throw new IllegalArgumentException( "id is null" );
		}
		this.id = id;
		delegate = new CommonScreenDockPerspective();
	}
	
	public String getUniqueId(){
		return id;
	}
	
	/**
	 * Adds <code>dockable</code> width boundaries <code>bounds</code> to this area.
	 * @param dockable the element to add, not <code>null</code>
	 * @param bounds the boundaries of <code>dockable</code>
	 * @param fullscreen whether <code>dockable</code> should be extended to fullscreen mode
	 */
	public void add( CDockablePerspective dockable, Rectangle bounds ){
		delegate.add( dockable.intern().asDockable(), bounds );
	}
	
	/**
	 * Adds <code>dockable</code> at location <code>x/y</code> with size <code>width/height</code> to
	 * this area.
	 * @param dockable the element to add, not <code>null</code>
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public void add( CDockablePerspective dockable, int x, int y, int width, int height ){
		delegate.add( dockable.intern().asDockable(), x, y, width, height, false );
	}
	
	/**
	 * Adds <code>dockable</code> width boundaries <code>bounds</code> to this area.
	 * @param dockable the element to add, not <code>null</code>
	 * @param bounds the boundaries of <code>dockable</code>
	 * @param fullscreen whether <code>dockable</code> should be extended to fullscreen mode
	 */
	public void add( CDockablePerspective dockable, Rectangle bounds, boolean fullscreen ){
		delegate.add( dockable.intern().asDockable(), bounds, fullscreen );
	}
	
	/**
	 * Adds <code>dockable</code> at location <code>x/y</code> with size <code>width/height</code> to
	 * this area.
	 * @param dockable the element to add, not <code>null</code>
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 * @param fullscreen whether <code>dockable</code> should be extended to fullscreen mode
	 */
	public void add( CDockablePerspective dockable, int x, int y, int width, int height, boolean fullscreen ){
		delegate.add( dockable.intern().asDockable(), x, y, width, height, fullscreen );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children to this
	 * area.
	 * @param dockable the element whose placeholder should be inserted
	 * @param bounds the location and size of <code>dockable</code>
	 */
	public void addPlaceholder( CDockablePerspective dockable, Rectangle bounds ){
		delegate.addPlaceholder( dockable.intern().asDockable(), bounds );
	}
	
	/**
	 * Adds a placeholder for <code>dockable</code> and all its children to this
	 * area.
	 * @param dockable the element whose placeholder should be inserted
	 * @param x the x-coordinate on the screen
	 * @param y the y-coordinate on the screen
	 * @param width the width of the window
	 * @param height the height of the window
	 */
	public void addPlaceholder( CDockablePerspective dockable, int x, int y, int width, int height ){
		delegate.addPlaceholder( dockable.intern().asDockable(), x, y, width, height );
	}
	
	/**
	 * Gets an object that stores all the properties that are associated with <code>dockable</code>.
	 * @param dockable the element whose window is searched
	 * @return the window, <code>null</code> if <code>dockable</code> is not known to this area
	 */
	public ScreenDockPerspective.ScreenPerspectiveWindow getWindow( CDockablePerspective dockable ){
		return delegate.getWindow( dockable.intern().asDockable() );
	}

	/**
	 * Removes <code>dockable</code> from this area.
	 * @param dockable the element to remove
	 * @return <code>true</code> if <code>dockable</code> was found and removed, <code>false</code>
	 * otherwise.
	 */
	public boolean remove( CDockablePerspective dockable ){
		return delegate.remove( dockable.intern().asDockable() );
	}
	
	/**
	 * Removes the <code>index</code>'th dockable of this area.
	 * @param index the index of a child of this area
	 * @return the element that was removed, <code>null</code> if the element is not a {@link CDockablePerspective}
	 */
	public CDockablePerspective remove( int index ){
		PerspectiveDockable dockable = delegate.remove( index );
		if( dockable instanceof CommonElementPerspective ){
			return ((CommonElementPerspective)dockable).getElement().asDockable();
		}
		else{
			return null;
		}
	}
	
	/**
	 * Gets the location of <code>dockable</code>.
	 * @param dockable some child of this area
	 * @return the location or -1 if not found
	 */
	public int indexOf( CDockablePerspective dockable ){
		return delegate.indexOf( dockable.intern().asDockable() );
	}
	
	public CommonScreenDockPerspective intern(){
		return delegate;
	}

	public CDockablePerspective asDockable(){
		return null;
	}
	
	public CStationPerspective asStation(){
		return this;
	}

	public String getFactoryID(){
		return delegate.getFactoryID();
	}
	
	public PlaceholderMap getPlaceholders(){
		return delegate.getPlaceholders();
	}
	
	public void setPlaceholders( PlaceholderMap placeholders ){
		delegate.setPlaceholders( placeholders );	
	}
	
	/**
	 * This type of object is used by the {@link CExternalizePerspective} as intern representation.
	 * @author Benjamin Sigg
	 */
	public class CommonScreenDockPerspective extends ScreenDockPerspective implements CommonElementPerspective{
		public CElementPerspective getElement(){
			return CExternalizePerspective.this;
		}		
	}
}