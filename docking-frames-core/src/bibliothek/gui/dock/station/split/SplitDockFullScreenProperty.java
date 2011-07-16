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
package bibliothek.gui.dock.station.split;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.AbstractDockableProperty;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.util.ClientOnly;
import bibliothek.util.xml.XElement;

/**
 * This {@link DockableProperty} points to the currently maximized child of a {@link SplitDockStation}. 
 * {@link SplitDockStation} itself will never create this property, it exists only for clients to easily
 * drop a maximized {@link Dockable}.
 * @author Benjamin Sigg
 */
@ClientOnly
public class SplitDockFullScreenProperty extends AbstractDockableProperty {
	public DockableProperty copy(){
		SplitDockFullScreenProperty property = new SplitDockFullScreenProperty();
		copy( property );
		return property;
	}

	public String getFactoryID(){
		return SplitDockFullScreenPropertyFactory.ID;
	}

	public void load( DataInputStream in ) throws IOException{
		// ignore
	}

	public void load( XElement element ){
		// ignore		
	}

	public void store( DataOutputStream out ) throws IOException{
		// ignore
	}

	public void store( XElement element ){
		// ignore
	}
}
