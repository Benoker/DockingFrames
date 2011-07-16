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
package bibliothek.gui.dock.support.lookandfeel;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple implementation of {@link ComponentCollector} that just returns
 * always the same {@link Component}.
 * @author Benjamin Sigg
 */
public class SingleComponentCollector implements ComponentCollector{
	private Component component;
	
	public SingleComponentCollector( Component component ){
		if( component == null )
			throw new IllegalArgumentException( "component must not be null" );
		this.component = component;
	}
	
	public Collection<Component> listComponents(){
		List<Component> list = new ArrayList<Component>();
		list.add( component );
		return list;
	}
}
