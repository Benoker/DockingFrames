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
package bibliothek.gui.dock.layout.location;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * This factory creates new instances of {@link DefaultAsideRequest}.
 * @author Benjamin Sigg
 */
public class DefaultAsideRequestFactory implements AsideRequestFactory{
	private DockProperties properties;
	
	/**
	 * Creates the new factory
	 * @param properties required to access the {@link PlaceholderStrategy}
	 */
	public DefaultAsideRequestFactory( DockProperties properties ){
		if( properties == null ){
			throw new IllegalArgumentException( "properties must not be null" );
		}
		this.properties = properties;
	}
	
	public AsideRequest createAsideRequest( DockableProperty location, Dockable dockable ){
		Path placeholder = null;
		PlaceholderStrategy strategy = properties.get( PlaceholderStrategy.PLACEHOLDER_STRATEGY );
		if( strategy != null ){
			placeholder = strategy.getPlaceholderFor( dockable );
		}
		return new DefaultAsideRequest( location, placeholder );
	}
}
