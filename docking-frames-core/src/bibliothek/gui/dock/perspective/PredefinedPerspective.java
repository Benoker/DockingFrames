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
package bibliothek.gui.dock.perspective;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.layout.PredefinedDockSituation;

/**
 * A {@link Perspective} that works together with {@link PredefinedDockSituation}. These perspectives
 * cannot be created directly, clients need to call {@link PredefinedDockSituation#createPerspective()} to
 * obtain an instance.<br>
 * Clients need to call {@link #put(String, PerspectiveElement)} in order to register the {@link PerspectiveElement}s
 * that are "predefined".
 * @author Benjamin Sigg
 */
public abstract class PredefinedPerspective extends Perspective{
	private Map<String, PerspectiveElement> stringToElement = new HashMap<String, PerspectiveElement>();
	private Map<PerspectiveElement, String> elementToString = new HashMap<PerspectiveElement, String>();
	
	private List<PredefinedMap> maps = new ArrayList<PredefinedMap>();
	
	/**
	 * Creates a new perspective using <code>situation</code> to read and write elements.
	 * @param situation the set of factories to use
	 */
	public PredefinedPerspective( PredefinedDockSituation situation ){
		super( situation );
	}
	
	@Override
	public PredefinedDockSituation getSituation() {
		return (PredefinedDockSituation)super.getSituation();
	}
	
	/**
	 * Adds an additional set of items to this perspective.
	 * @param map the set of known items, not <code>null</code>
	 */
	public void put( PredefinedMap map ){
		if( map == null ){
			throw new IllegalArgumentException( "map must no be null" );
		}
		maps.add( map );
	}
	
	/**
	 * Registers <code>element</code> on this {@link PredefinedPerspective}. When writing the layout the
	 * identifier <code>key</code> will be written instead of <code>element</code>.
	 * @param key the key of the element
	 * @param element the new element
	 */
	public void put( String key, PerspectiveElement element ){
		PerspectiveElement old = stringToElement.remove( key );
		if( old != null ){
			elementToString.remove( old );
		}
		
		stringToElement.put( key, element );
		elementToString.put( element, key );
	}
	
	/**
	 * Gets all the keys known to this perspective.
	 * @return all the keys
	 */
	public String[] getKeys(){
		return stringToElement.keySet().toArray( new String[ stringToElement.size() ] );
	}

	/**
	 * Gets the key for <code>element</code>.
	 * @param element some element whose key is searched, not <code>null</code>
	 * @return the key or <code>null</code>
	 */
	public String get( PerspectiveElement element ){
		String result = elementToString.get( element );
		if( result == null ){
			for( PredefinedMap map : maps ){
				result = map.get( element );
				if( result != null ){
					return result;
				}
			}
		}
		return result;
	}
	
	/**
	 * Gets the element which was stored using <code>key</code>.
	 * @param key the key of some element
	 * @return the element or <code>null</code>
	 */
	public PerspectiveElement get( String key ){
		PerspectiveElement result = stringToElement.get( key );
		
		if( result == null ){
			for( PredefinedMap map : maps ){
				result = map.get( key );
				if( result != null ){
					return result;
				}
			}
		}
		return result;
	}
}
