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

package bibliothek.gui.dock.station.split;

import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;

/**
 * An implementation of {@link AbstractSplitDockGrid} that works with {@link Dockable}s.
 * @author Benjamin Sigg
 * @see #toTree()
 * @see SplitDockStation#dropTree(SplitDockTree)
 */
public class SplitDockGrid extends AbstractSplitDockGrid<Dockable>{
	/**
     * Creates a new, empty grid.
     */
    public SplitDockGrid(){
        // do nothing
    }
    
    /**
     * Creates a grid by reading a string which represents a grid.<br>
     * The argument <code>layout</code> is a string divided by newline
     * <code>"\n"</code>. Every line represents a y-coordinate, the position
     * of a character in a line represents a x-coordinate. The minimal and 
     * the maximal x- and y-coordinates for a character is searched, and
     * used to call {@link AbstractSplitDockGrid#addDockable(double, double, double, double, Object...) addDockable},
     * where the <code>Dockable</code>-array is taken from the {@link Map} 
     * <code>dockables</code>.
     * @param layout the layout, a string divided by newlines
     * @param dockables the Dockables to add, only entries whose character is
     * in the String <code>layout</code>.
     */
    public SplitDockGrid( String layout, Map<Character, Dockable[]> dockables ){
    	super( layout, dockables );
    }
    
    @Override
    protected Dockable[] unpack( Dockable dockable ){
	    DockStation station = dockable.asDockStation();
	    if( station == null ){
	    	return new Dockable[]{ dockable };
	    }
	    Dockable[] result = new Dockable[ station.getDockableCount() ];
	    for( int i = 0; i < result.length; i++ ){
	    	result[i] = station.getDockable( i );
	    }
	    return result;
    }
    
    @Override
    protected Dockable[] array( int size ){
    	return new Dockable[ size ];
    }
    
	/**
	 * Converts the current grid into a tree.
	 * @return the tree which represents this grid
	 * @see SplitDockStation#dropTree(SplitDockTree)
	 */
	public DockableSplitDockTree toTree(){
		DockableSplitDockTree tree = new DockableSplitDockTree();
		fillTree( tree );
		return tree;
	}
}
