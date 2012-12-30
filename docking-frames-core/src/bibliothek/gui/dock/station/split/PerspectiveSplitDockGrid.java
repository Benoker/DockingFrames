/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.split;

import java.util.Map;

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveStation;

/**
 * An implementation of {@link AbstractSplitDockGrid} that works with {@link PerspectiveDockable}s.
 * @author Benjamin Sigg
 * @see SplitDockPerspective
 */
public class PerspectiveSplitDockGrid extends AbstractSplitDockGrid<PerspectiveDockable>{
	/**
     * Creates a new, empty grid.
     */
    public PerspectiveSplitDockGrid(){
        // do nothing
    }
    
    /**
     * Creates a grid by reading a string which represents a grid.<br>
     * The argument <code>layout</code> is a string divided by newline
     * <code>"\n"</code>. Every line represents a y-coordinate, the position
     * of a character in a line represents a x-coordinate. The minimal and 
     * the maximal x- and y-coordinates for a character is searched, and
     * used to call {@link #addDockable(double, double, double, double, Object...) addDockable},
     * where the <code>Dockable</code>-array is taken from the {@link Map} 
     * <code>dockables</code>.
     * @param layout the layout, a string divided by newlines
     * @param dockables the Dockables to add, only entries whose character is
     * in the String <code>layout</code>.
     */
    public PerspectiveSplitDockGrid( String layout, Map<Character, PerspectiveDockable[]> dockables ){
    	super( layout, dockables );
    }
    
    @Override
    protected PerspectiveDockable[] array( int size ){
    	return new PerspectiveDockable[ size ];
    }
    
	/**
	 * Converts the current grid into a tree.
	 * @return the tree which represents this grid
	 * @see SplitDockStation#dropTree(SplitDockTree)
	 */
	public PerspectiveSplitDockTree toTree(){
		PerspectiveSplitDockTree tree = new PerspectiveSplitDockTree();
		fillTree( tree );
		return tree;
	}

	@Override
	protected PerspectiveDockable[] unpack( PerspectiveDockable dockable ){
		PerspectiveStation station = dockable.asStation();
		if( station == null ){
			return new PerspectiveDockable[]{ dockable };
		}
		PerspectiveDockable[] result = new PerspectiveDockable[ station.getDockableCount() ];
		for( int i = 0; i < result.length; i++ ){
			result[i] = station.getDockable( i );
		}
		return result;
	}
}
