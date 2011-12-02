/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.control.relocator;

/**
 * An adapter class to {@link VetoableDockRelocatorListener}, can be used to implement {@link VetoableDockRelocatorListener}
 * without the need to implement all methods.
 * @author Benjamin Sigg
 */
public abstract class VetoableDockRelocatorAdapter implements VetoableDockRelocatorListener{
	public void canceled( DockRelocatorEvent event ){
		// nothing
	}
	
	public void dragged( DockRelocatorEvent event ){
		// nothing
	}	
	
	public void dragging( DockRelocatorEvent event ){
		// nothing
	}

	public void searched( DockRelocatorEvent event ){
		// noting
	}
	
	public void dropped( DockRelocatorEvent event ){
		// nothing	
	}

	public void dropping( DockRelocatorEvent event ){
		// nothing
	}

	public void grabbed( DockRelocatorEvent event ){
		// nothing
	}

	public void grabbing( DockRelocatorEvent event ){
		// nothing
	}
}
