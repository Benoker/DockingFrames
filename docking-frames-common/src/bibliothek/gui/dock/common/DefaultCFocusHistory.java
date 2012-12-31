/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.gui.dock.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.control.focus.FocusHistory;
import bibliothek.util.Filter;

/**
 * The default implementation of {@link CFocusHistory} does not offer any additional
 * functionality.
 * @author Benjamin Sigg
 */
public class DefaultCFocusHistory implements CFocusHistory{
	private CControl control;
	
	/**
	 * Creates a new history
	 * @param control the source of all {@link CDockable}s
	 */
	public DefaultCFocusHistory( CControl control ){
		this.control = control;
	}
	
	public CDockable[] getHistory(){
		FocusHistory history = control.getController().getFocusHistory();
		List<CDockable> result = new ArrayList<CDockable>();
		Dockable[] dockables = history.getHistory();
		for( int i = dockables.length-1; i >= 0; i-- ){
			Dockable dockable = dockables[i];
			if( dockable instanceof CommonDockable ){
				CDockable cdockable = ((CommonDockable)dockable).getDockable();
				result.add( cdockable );
			}
		}
		return result.toArray( new CDockable[ result.size() ] );
	}
	
	public CDockable getFirst( Filter<CDockable> filter ){
		CDockable[] history = getHistory();
		Set<CDockable> visited = new HashSet<CDockable>();
		
		for( CDockable dockable : history ){
			if( filter.includes( dockable )){
				return dockable;
			}
			visited.add( dockable );
		}
		
		for( CDockable dockable : control.getRegister().getDockables() ){
			if( !visited.contains( dockable )){
				if( filter.includes( dockable )){
					return dockable;
				}
			}
		}
		
		return null;
	}
}
