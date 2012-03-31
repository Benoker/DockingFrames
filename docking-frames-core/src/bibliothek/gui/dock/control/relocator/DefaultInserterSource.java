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

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.StationDropItem;
import bibliothek.gui.dock.station.StationDropOperation;

/**
 * Default implementation of {@link InserterSource}. 
 * @author Benjamin Sigg
 */
public class DefaultInserterSource implements InserterSource{
	private DockStation parent;
	private StationDropOperation operation;
	private StationDropItem item;
	
	/**
	 * Creates a new {@link InserterSource}.
	 * @param parent the future parent
	 * @param item detailed information about the dropping child
	 */
	public DefaultInserterSource( DockStation parent, StationDropItem item ){
		this.parent = parent;
		this.item = item;
	}
	
	public DockStation getParent(){
		return parent;
	}

	public StationDropItem getItem(){
		return item;
	}

	/**
	 * Sets the result of {@link #getOperation()}.
	 * @param operation the operation that might be executed, can be <code>null</code>
	 */
	public void setOperation( StationDropOperation operation ){
		this.operation = operation;
	}
	
	public StationDropOperation getOperation(){
		return operation;
	}
	
}
