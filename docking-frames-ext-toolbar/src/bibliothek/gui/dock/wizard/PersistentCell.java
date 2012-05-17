/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.wizard;

import bibliothek.util.FrameworkOnly;

/**
 * A cell in the grid of a {@link WizardSplitDockStation}, the cell offers information about
 * its current and its preferred size.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class PersistentCell{
	private int size;
	private int preferred;
	
	public PersistentCell( int size, int preferred ){
		this.size = size;
		this.preferred = preferred;
		if( size <= 0 ){
			this.size = preferred;
		}
	}
	
	/**
	 * Changes the size of this cell.
	 * @param size the new size of this cell
	 */
	public void setSize( int size ){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getPreferredSize(){
		return preferred;
	}
}