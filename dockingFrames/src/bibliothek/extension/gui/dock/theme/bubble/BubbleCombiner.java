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

package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.Combiner;
import bibliothek.gui.dock.station.StackDockStation;

/**
 * A {@link Combiner} normally used by the {@link BubbleTheme} to replace the
 * default combiner.
 * @author Benjamin Sigg
 */
public class BubbleCombiner implements Combiner{
	/** The theme which uses this combiner */
	private BubbleTheme theme;
	
	/**
	 * Creates a new combiner
	 * @param theme the theme using this combiner
	 */
	public BubbleCombiner( BubbleTheme theme ){
		if( theme == null )
			throw new IllegalArgumentException( "Theme must not be null" );
		
		this.theme = theme;
	}
	
    public Dockable combine( Dockable old, Dockable drop, DockStation parent ) {
        StackDockStation stack = createStackDockStation( parent.getTheme() );
        
        stack.setStackComponent( new BubbleStackDockComponent( theme ));
        stack.drop( old );
        stack.drop( drop );
        
        return stack;
    }
    
    /**
     * Creates a new {@link StackDockStation} which will be populated
     * with two {@link Dockable Dockables}.
     * @param theme The theme that the station will have, might be <code>null</code>
     * @return the new station
     */
    protected StackDockStation createStackDockStation( DockTheme theme ){
        return new StackDockStation( theme );
    }
}
