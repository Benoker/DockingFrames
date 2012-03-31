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

package bibliothek.gui.dock.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import bibliothek.gui.dock.DockElement;
import bibliothek.gui.dock.util.DockUtilities;

/**
 * An object that can hold some {@link LocatedListener}s and order and filter
 * them regarding to one element of the tree.
 * @param <L> the type of listeners in this list
 * @author Benjamin Sigg
 */
public class LocatedListenerList<L extends LocatedListener> {
	/** the listener of listeners known to this list */
	private List<L> listeners = new ArrayList<L>();

	/**
	 * Adds a listener to the list of listeners.
	 * @param listener the new listener
	 */
	public void addListener( L listener ){
		listeners.add( listener );
	}
	
	/**
	 * Removes a list of the list of listeners.
	 * @param listener the listener to remove
	 */
	public void removeListener( L listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets the number of listeners in this list.
	 * @return the number of listeners
	 */
	public int size(){
		return listeners.size();
	}
	
    /**
     * Creates a list of all {@link LocatedListener}s which are affected
     * by an event which occurs on <code>element</code>. The list is ordered
     * by the distance of the listeners to <code>element</code>.
     * @param element the element which is the source of an event
     * @return the ordered list of observers
     */
    public List<L> affected( DockElement element ){
        List<L> list = new LinkedList<L>();
        for( L listener : listeners ){
            DockElement location = listener.getTreeLocation();
            if( location == null )
                list.add( listener );
            else if( element != null && DockUtilities.isAncestor( location, element ))
                list.add( listener );
        }
        
        Collections.sort( list, new Comparator<L>(){
            public int compare( L o1, L o2 ) {
                DockElement a = o1.getTreeLocation();
                DockElement b = o2.getTreeLocation();
                
                if( a == b )
                    return 0;
                
                if( a == null )
                    return 1;
                
                if( b == null )
                    return -1;
                
                if( DockUtilities.isAncestor( a, b ))
                    return 1;
                
                return -1;
            }
        });
        
        return list;
    }
}
