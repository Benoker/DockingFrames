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

import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.layout.DockablePropertyFactory;

/**
 * This factory creates new instances of {@link SplitDockFullScreenProperty}s.
 * @author Benjamin Sigg
 */
public class SplitDockFullScreenPropertyFactory implements DockablePropertyFactory{
    /** The id that is used for this factory */
    public static final String ID = "SplitDockFullScreenPropertyFactory";
    
    /** An instance of the factory that can be used at any location */
    public static final SplitDockFullScreenPropertyFactory FACTORY = new SplitDockFullScreenPropertyFactory();
    
	
	public DockableProperty createProperty(){
		return new SplitDockFullScreenProperty();
	}

	public String getID(){
		return ID;
	}

}
