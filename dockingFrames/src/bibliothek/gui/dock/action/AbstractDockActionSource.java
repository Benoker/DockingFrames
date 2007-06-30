/**
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

package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.event.DockActionSourceListener;

/**
 * A simple implementation of {@link DockActionSource} that just handles
 * the {@link DockActionSourceListener listeners} for sources.
 * @author Benjamin Sigg
 */
public abstract class AbstractDockActionSource implements DockActionSource {
    /**
     * The listeners which are registered on this source
     */
    protected List<DockActionSourceListener> listeners = new ArrayList<DockActionSourceListener>();
    
    public void addDockActionSourceListener( DockActionSourceListener listener ) {
        listeners.add( listener );
    }

    public void removeDockActionSourceListener( DockActionSourceListener listener ) {
        listeners.remove( listener );
    }

    /**
     * Invokes the {@link DockActionSourceListener#actionsAdded(DockActionSource, int, int) actionsAdded}-method
     * on all registered {@link DockActionSourceListener DockActionSourceListeners}.
     * @param firstIndex The index of the first action that was added
     * @param lastIndex The index of the last action that was added
     */
    protected void fireAdded( int firstIndex, int lastIndex ){
        for( DockActionSourceListener listener : listeners.toArray( new DockActionSourceListener[ listeners.size() ] ))
            listener.actionsAdded( this, firstIndex, lastIndex );
    }
    
    /**
     * Invokes the {@link DockActionSourceListener#actionsRemoved(DockActionSource, int, int) actionRemoved}-method
     * on all registered {@link DockActionSourceListener DockActionSourceListeners}.
     * @param firstIndex The old index of the first action that was removed
     * @param lastIndex The old index of the last action that was removed
     */
    protected void fireRemoved( int firstIndex, int lastIndex ){
        for( DockActionSourceListener listener : listeners.toArray( new DockActionSourceListener[ listeners.size() ] ))
            listener.actionsRemoved( this, firstIndex, lastIndex );
    }
}
