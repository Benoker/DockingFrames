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

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.support.PlaceholderMap;

/**
 * The {@link DefaultAsideAnswer} is just a container for properties but does not modify them
 * in any way.
 * @author Benjamin Sigg
 */
public class DefaultAsideAnswer implements AsideAnswer{
	private boolean canceled;
	private DockableProperty location;
	private PlaceholderMap layout;
	
	/**
	 * Creates a new answer.
	 * @param canceled whether the request was canceled
	 * @param location the location of the new item
	 * @param layout the layout of the non-existing station
	 */
	public DefaultAsideAnswer( boolean canceled, DockableProperty location, PlaceholderMap layout ){
		this.canceled = canceled;
		this.location = location;
		this.layout = layout;
	}
	
	public boolean isCanceled(){
		return canceled;
	}
	
	public DockableProperty getLocation(){
		return location;
	}
	
	public PlaceholderMap getLayout(){
		return layout;
	}
}
