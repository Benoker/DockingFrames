/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.common.behavior;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.util.Filter;

/**
 * A factory that creates new {@link ExternalizingCGridArea}s.
 * @author Benjamin Sigg
 */
public class ExternalizingCGridAreaFactory implements SingleCDockableFactory{
	/** the control in whose realm the dockables exist */
	private CControl control;
	
	/** The pattern that is used to automatically generate new {@link ExternalizingCGridArea} */
	public static final Filter<String> PATTERN = new Filter<String>(){
		public boolean includes( String item ){
			return item.startsWith( ExternalizingCGridArea.UNIQUE_ID_PREFIX );
		}
	}; 
	
	/**
	 * Creates a new factory
	 * @param control the control in whose realm the new stations are used
	 */
	public ExternalizingCGridAreaFactory( CControl control ){
		if( control == null ){
			throw new IllegalArgumentException( "control must not be null" );
		}
		this.control = control;
	}
	
	public SingleCDockable createBackup( String id ){
		return new ExternalizingCGridArea( control, id );
	}

}
