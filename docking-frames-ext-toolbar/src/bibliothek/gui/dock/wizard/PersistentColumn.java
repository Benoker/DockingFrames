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

import java.util.Map;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.wizard.WizardNodeMap.Column;
import bibliothek.util.FrameworkOnly;

/**
 * A column in the grid of a {@link WizardSplitDockStation}, the column offers information
 * about its current and preferred size. It also offers a list of all its cells.
 * @author Benjamin Sigg
 */
@FrameworkOnly
public class PersistentColumn{
	private int size;
	private int preferred;
	private Column source;
	private Map<Dockable, PersistentCell> cells;
	
	public PersistentColumn( int size, int preferred, Column source, Map<Dockable, PersistentCell> cells ){
		this.size = size;
		this.preferred = preferred;
		if( size <= 0 ){
			this.size = preferred;
		}
		this.source = source;
		this.cells = cells;
	}
	
	public Column getSource(){
		return source;
	}
	
	public void setSize( int size ){
		this.size = size;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getPreferredSize(){
		return preferred;
	}
	
	public Map<Dockable, PersistentCell> getCells(){
		return cells;
	}
}