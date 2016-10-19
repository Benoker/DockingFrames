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
package bibliothek.gui.dock.frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.perspective.Perspective;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.perspective.PerspectiveStation;
import bibliothek.gui.dock.station.flap.FlapDockPerspective;
import bibliothek.gui.dock.station.screen.ScreenDockPerspective;
import bibliothek.gui.dock.station.split.SplitDockPerspective;
import bibliothek.gui.dock.station.stack.StackDockPerspective;
import bibliothek.gui.dock.util.extension.ExtensionName;
import bibliothek.util.Path;

/**
 * This default implementation of a {@link FrontendPerspectiveCache} assumes that the information clients
 * gave to a {@link DockFrontend} is enough and use only the default {@link PerspectiveElement}s. This cache
 * handles only the default implementation of the {@link DockStation}s and of {@link DefaultDockable}. Using any
 * other {@link DockElement} will result in exceptions.<br>
 * {@link DefaultDockable}s are represented by {@link FrontendDockablePerspective}s.<br>
 * This implementation has several <b>drawbacks</b>: 
 * <ul>
 * 	<li>All {@link PerspectiveElement} that were ever created are stored in a cache. Clients should make sure to 
 *  use a new cache every time the access a {@link Perspective}, this way the old cache can be cleaned up by the
 *  garbage collector.</li>
 *  <li>The unique identifier of {@link Dockable}s is read from the {@link DockFrontend}, this cache has no way
 *  to know the identifiers of unregistered elements. These elements will be ignored and thrown away when reading the layout.</li>
 * </ul> 
 * @author Benjamin Sigg
 */
public class DefaultFrontendPerspectiveCache implements FrontendPerspectiveCache{
	/** Unique identifier to load extensions of type {@link FrontendPerspectiveCacheExtension} */
	public static final Path CACHE_EXTENSION = new Path( "dock.defaultFrontendPerspectiveCache" );
	
	private Map<PerspectiveElement, String> identifiers = new HashMap<PerspectiveElement, String>();
	
	/** the frontend to query for information */
	private DockFrontend frontend;
	
	/** additional types */
	private List<FrontendPerspectiveCacheExtension> extensions;
	
	/**
	 * Creates a new cache
	 * @param frontend the frontend to query for information about {@link DockElement}s.
	 */
	public DefaultFrontendPerspectiveCache( DockFrontend frontend ){
		if( frontend == null ){
			throw new IllegalArgumentException( "frontend must not be null" );
		}
		this.frontend = frontend;
		extensions = frontend.getController().getExtensions().load( new ExtensionName<FrontendPerspectiveCacheExtension>( CACHE_EXTENSION, FrontendPerspectiveCacheExtension.class ) );
	}
	
	public PerspectiveElement get( String id, DockElement element, boolean isRootStation ){
		PerspectiveElement result = null;
		
		for( FrontendPerspectiveCacheExtension extension : extensions ){
			result = extension.get( id, element, isRootStation );
			if( result != null ){
				break;
			}
		}
		
		if( result == null ){
			if( element instanceof StackDockStation ){
				result = new StackDockPerspective();
			}
			if( element instanceof FlapDockStation ){
				result = new FlapDockPerspective();
			}
			if( element instanceof SplitDockStation ){
				SplitDockPerspective split = new SplitDockPerspective();
				split.setHasFullscreenAction( ((SplitDockStation)element).hasFullScreenAction() );
				result = split;
			}
			if( element instanceof ScreenDockStation ){
				result = new ScreenDockPerspective();
			}
			if( element instanceof DefaultDockable ){
				result = new FrontendDockablePerspective( id );
			}
		}

		if( result == null ){
			throw new IllegalArgumentException( "'" + id + "' is of unknown type: " + element );
		}
		else{
			if( !element.getFactoryID().equals( result.getFactoryID() )){
				throw new IllegalArgumentException( "the factory configured for 'element' is '" + element.getFactoryID() + "', but expected was '" + result.getFactoryID() + "'. Clients need to subclass this cache and handle this special case." );
			}
			
			put( result, id );
			return result;
		}
	}
	
	/**
	 * Makes an association that the identifier <code>id</code> is used for <code>element</code>. 
	 * @param element the element whose identifier is stored, not <code>null</code>
	 * @param id the identifier to store, not <code>null</code>
	 */
	protected void put( PerspectiveElement element, String id ){
		if( element == null ){
			throw new IllegalArgumentException( "element must not be null" );
		}
		if( id == null ){
			throw new IllegalArgumentException( "id must not be null" );
		}
		
		identifiers.put( element, id );
	}

	public PerspectiveElement get( String id, boolean rootStation ){
		return null;
	}

	public String get( PerspectiveElement element ){
		for( FrontendPerspectiveCacheExtension extension : extensions ){
			String result = extension.get( element );
			if( result != null ){
				return result;
			}
		}
		
		if( element instanceof FrontendDockablePerspective ){
			return ((FrontendDockablePerspective) element).getId();
		}
		
		return identifiers.get( element );
	}

	public boolean isRootStation( PerspectiveStation station ){
		String id = get( station );
		if( id == null ){
			return false;
		}
		return frontend.getRoot( id ) != null;
	}
}
