/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bibliothek.gui.dock.common.intern.CommonMultipleDockableFactory;
import bibliothek.gui.dock.perspective.PerspectiveElement;

/**
 * A helper class for {@link CommonMultipleDockableFactory}, used when interacting with a {@link CPerspective}.
 * @author Benjamin Sigg
 */
public class CPerspectiveMultipleIdentifierCollection{
	private CPerspective perspective;

	private Map<String, MultipleCDockablePerspective> ids = new HashMap<String, MultipleCDockablePerspective>();
	
	/**
	 * Creates a new object
	 * @param factoryId the unique identifier of the factory that is using this collection
	 * @param perspective the owner of this object, not <code>null</code>
	 */
	public CPerspectiveMultipleIdentifierCollection( String factoryId, CPerspective perspective ){
		this.perspective = perspective;
		
		Iterator<PerspectiveElement> iterator = perspective.elements();
		while( iterator.hasNext() ){
			PerspectiveElement element = iterator.next();
			if( element instanceof CommonElementPerspective ){
				CElementPerspective celement = ((CommonElementPerspective)element).getElement();
				if( celement instanceof MultipleCDockablePerspective ){
					MultipleCDockablePerspective dockable = (MultipleCDockablePerspective)celement;
					if( dockable.getFactoryID().equals( factoryId ) && dockable.getUniqueId() != null ){
						ids.put( dockable.getUniqueId(), dockable );
					}
				}
			}
		}
	}
	
	/**
	 * Gets the owner of this access object.
	 * @return the owner
	 */
	public CPerspective getPerspective(){
		return perspective;
	}
	
	/**
	 * Searches or creates a unique identifier that matches <code>element</code>.
	 * @param element the element whose identifier is searched
	 * @return the identifier, may be <code>null</code>
	 */
	public String getUniqueId( MultipleCDockablePerspective element ){
		String id = element.getUniqueId();
		if( id == null ){
			String factory = element.getFactoryID();
			int count = 0;
	        id = count + " " + factory;
	        while( ids.containsKey( id )){
	            count++;
	            id = count + " " + factory;
	        }
	        element.setUniqueId( id );
		}
		MultipleCDockablePerspective old = ids.put( id, element );
		if( old != null && old != element ){
			throw new IllegalStateException( "unique identifier collision, id='" + id + "', old item='" + old + "', new item='" + element + "'");
		}
		return id;
	}

	/**
	 * Stores the element <code>perspective</code> using the identifier <code>id</code>.
	 * @param id the unique identifier of <code>perspective</code>
	 * @param perspective the new perspective
	 */
	public void putDockable( String id, MultipleCDockablePerspective perspective ){
		ids.put( id, perspective );
	}
}
