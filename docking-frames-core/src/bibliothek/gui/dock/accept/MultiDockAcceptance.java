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

package bibliothek.gui.dock.accept;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A {@link DockAcceptance} which consists of other acceptances, and returns
 * only <code>true</code> if all children of this acceptance return <code>true</code>.
 * @author Benjamin Sigg
 *
 */
public class MultiDockAcceptance implements DockAcceptance {
    private List<DockAcceptance> acceptances = new ArrayList<DockAcceptance>();
    
    /**
     * Adds a {@link DockAcceptance} to the list of acceptances, which must be
     * asked, before an <code>accept</code>-method returns <code>true</code>.
     * @param acceptance the acceptance to ask
     */
    public void add( DockAcceptance acceptance ){
        if( acceptance == null )
            throw new IllegalArgumentException( "Acceptance must not be null" );
        acceptances.add( acceptance );
    }
    
    /**
     * Removes a {@link DockAcceptance} which was earlier {@link #add(DockAcceptance) added}
     * to this <code>MultiDockAcceptance</code>.
     * @param acceptance the acceptance to remove
     */
    public void remove( DockAcceptance acceptance ){
        acceptances.remove( acceptance );
    }
    
    public boolean accept( DockStation parent, Dockable child ){
        for( DockAcceptance acceptance : acceptances ){
            if( !acceptance.accept( parent, child ))
                return false;
        }
        
        return true;
    }

    public boolean accept( DockStation parent, Dockable child, Dockable next ){
        for( DockAcceptance acceptance : acceptances ){
            if( !acceptance.accept( parent, child, next ))
                return false;
        }
        
        return true;
    }
}
