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

package bibliothek.gui.dock.station.support;

import bibliothek.gui.DockTheme;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.Combiner;

/**
 * A <code>CombinerWrapper</code> encloses a {@link Combiner} and uses
 * the combiner as delegate. If the wrapper has no delegate, it uses
 * the {@link DockUI} to get a combiner from the current {@link DockTheme}.
 * @author Benjamin Sigg
 *
 */
public class CombinerWrapper implements Combiner {
    /** The delegate that is used if not <code>null</code> */
    private Combiner delegate;
    
    /**
     * Gets the delegate of this wrapper.
     * @return the delegate or <code>null</code>
     */
    public Combiner getDelegate() {
        return delegate;
    }
    
    /**
     * Sets the delegate for this wrapper. The delegate will be used
     * whenever possible.
     * @param delegate the delegate or <code>null</code> if the default combiner
     * should be used
     */
    public void setDelegate( Combiner delegate ) {
        this.delegate = delegate;
    }
    
    public CombinerTarget prepare( CombinerSource source, boolean force ){
    	return DockUI.getCombiner( delegate, source.getParent() ).prepare( source, force );
    }
    
    public Dockable combine( CombinerSource source, CombinerTarget target ){
        return DockUI.getCombiner( delegate, source.getParent() ).combine( source, target );
    }
}
