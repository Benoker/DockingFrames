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
package bibliothek.gui.dock.station.stack.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * This layout manager creates a common interface to store information for each
 * {@link TabPane} individually.
 * @author Benjamin Sigg
 * @param <I> how information about {@link TabPane}s gets represented
 */
public abstract class AbstractTabLayoutManager<I extends AbstractTabLayoutManagerPane> implements TabLayoutManager{
	/** informations about {@link TabPane}s */
	private List<I> infos = new ArrayList<I>();
	
	public void install( TabPane pane ){
		I info = createInfoFor( pane );
		pane.addTabPaneListener( info );
		infos.add( info );
	}

	public void uninstall( TabPane pane ){
		ListIterator<I> iterator = infos.listIterator();
		while( iterator.hasNext() ){
			I next = iterator.next();
			if( next.getPane() == pane ){
				iterator.remove();
				pane.removeTabPaneListener( next );
				destroy( next );
				next.uninstalled();
			}
		}
	}
	
	/**
	 * Creates a new bag for information about <code>pane</code>.
	 * @param pane some panel
	 * @return the information for <code>pane</code>
	 */
	protected abstract I createInfoFor( TabPane pane );
	
	/**
	 * Called when the information <code>info</code> is no longer
	 * required.
	 * @param info the information bag to delete
	 */
	protected abstract void destroy( I info );
	
	/**
	 * Gets all the information that is stored for <code>pane</code>.
	 * @param pane some panel
	 * @return information about <code>pane</code> or <code>null</code>
	 */
	public I getInfo( TabPane pane ){
		for( I check : infos ){
			if( check.getPane() == pane ){
				return check;
			}
		}
		return null;
	} 
}
