/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.mode;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.support.mode.Mode;


/**
 * Abstract implementation of a {@link Mode} that works with {@link Location}s. This 
 * implementation offers the {@link #apply(Dockable, Location)} and {@link #leave(Dockable)} methode.
 * @author Benjamin Sigg
 * @param <M> the areas that are managed by this mode
 */
public abstract class DefaultLocationMode<A extends StationModeArea> extends AbstractLocationMode<A>{
	/**
	 * Creates a new mode.
	 * @param manager the manager that is using this mode
	 */
	public DefaultLocationMode( ExtendedModeManager manager ){
		super( manager );
	}
	
	/**
	 * This default implementation just returns the location of
	 * <code>dockable</code> but does change any properties.
	 */
	public Location leave( Dockable dockable ){
		A area = get( dockable );
		if( area == null )
			return null;
		DockableProperty location = area.getLocation( dockable );
		return new Location( area.getUniqueId(), location );
	}
	
	/**
	 * This default implementation uses the {@link DockStation#move(Dockable, DockableProperty)}
	 * and {@link DockStation#drop(Dockable, DockableProperty)} methods to put
	 * <code>dockable</code> at its location.
	 */
	public void apply( Dockable dockable, Location history ){
		A area = null;
		if( history != null ) 
			area = get( history.getRoot() );
		if( area == null )
			area = getDefaultArea();
		
		if( area == null )
			throw new IllegalStateException( "unable to find valid target" );
		
		DockableProperty location = history == null ? null : history.getLocation();
		area.setLocation( dockable, location );
	}

	public boolean isCurrentMode( Dockable dockable ){
		for( A area : this ){
			if( area.isChild( dockable )){
				return true;
			}
		}
		return false;
	}
}