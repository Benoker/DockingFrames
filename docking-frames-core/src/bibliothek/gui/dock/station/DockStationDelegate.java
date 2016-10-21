/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.location.AsideAnswer;
import bibliothek.gui.dock.layout.location.AsideRequest;
import bibliothek.gui.dock.station.support.DockablePlaceholderList;
import bibliothek.gui.dock.station.support.PlaceholderListItem;
import bibliothek.gui.dock.station.support.PlaceholderMap;
import bibliothek.util.Path;

/**
 * A set of algorithms that are useful for {@link DockStation}s. Rather then using a utility class
 * with static methods, this class allows to override and modify the algorithms if necessary.
 * @author Benjamin Sigg
 */
public class DockStationDelegate {
    
    /**
     * Can be called by {@link DockStation#aside(AsideRequest)} if <code>request</code> contains a 
     * location that points toward <code>item</code>. The method first tries to call
     * {@link DockStation#aside(AsideRequest)} of the {@link Dockable} represented by
     * <code>item</code>, and if that fails it tries to call {@link Combiner#aside(AsideRequest)}.
     * @param item the item which should contain the new location
     * @param combiner the {@link Combiner} to ask if there is no {@link DockStation} to ask, not <code>null</code>
     * @param request information about the location to create
     */
    public <T extends PlaceholderListItem<Dockable>> void combine( DockablePlaceholderList<T>.Item item, Combiner combiner, AsideRequest request ){
    	PlaceholderListItem<Dockable> handle = item.getDockable();
    	Path placeholder = request.getPlaceholder();
    	if( placeholder != null ){
    		item.add( placeholder );
    	}
    	if( handle != null ){
    		DockStation station = handle.asDockable().asDockStation();
    		if( station != null ){
    			request.forward( station );
    			return;
    		}
    	}    		
    	PlaceholderMap childLayout = item.getPlaceholderMap();
    	AsideAnswer answer = request.forward( combiner, childLayout );
    	if( !answer.isCanceled() ){
    		item.setPlaceholderMap( answer.getLayout() );
    	}
    }
}
