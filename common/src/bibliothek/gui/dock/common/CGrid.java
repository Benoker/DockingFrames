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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Version;

/**
 * A {@link CGrid} is a mechanism to layout a set of {@link CDockable} on
 * a {@link SplitDockStation} like the one used in the center of the
 * {@link CContentArea} or on the {@link CWorkingArea}. <code>CGrid</code>s
 * also can register new {@link CDockable}s to a {@link CControl}.<br>
 * <br>
 * <b>Usage:</b><br>
 * A <code>CGrid</code> consists of a set of <code>entries</code> where each
 * <code>entry</code> consists of a set of {@link CDockable}s and a rectangle
 * (position= <code>x/y</code>, size=<code>width/height</code>). The rectangle
 * tells where the <code>CDockable</code>s and how big they are compared to the
 * other <code>entries</code>.<br>
 * A client first fills a <code>CGrid</code> with such entries. Then the client calls
 * {@link CGridArea#deploy(CGrid)}, {@link CWorkingArea#deploy(CGrid)} or 
 * {@link #toTree()}. This triggers the <code>CGrid</code> to convert its entries
 * into a tree of <code>Dockable</code>s. This tree can then be given to a 
 * {@link SplitDockStation}. The <code>CGrid</code> can be trashed after it created
 * the tree - changes to the grid will not be forwarded to the tree.<br>
 * <br>
 * If the <code>CGrid</code> was created using the constructor {@link #CGrid(CControl)},
 * then the method {@link CControl#add(SingleCDockable)} or {@link CControl#add(MultipleCDockable)}
 * is called for any new {@link CDockable}.
 * @author Benjamin Sigg
 *
 */
@Todo( priority=Todo.Priority.MAJOR, compatibility=Compatibility.COMPATIBLE, target=Version.VERSION_1_1_0,
		description="Allow using identifiers instead of CDockables to fill up the grid, create the dockables lazily" )
public class CGrid {
    /** the internal representation of this grid */
    private SplitDockGrid grid = new SplitDockGrid();
    
    /** the control for which this grid is used */
    private CControl control;
    
    /**
     * Creates a new grid.
     */
    public CGrid(){
        // do nothing
    }
    
    /**
     * Creates a new grid. New {@link CDockable}s will be registered at
     * <code>control</code>.
     * @param control the control where this grid should register new
     * {@link CDockable}s.
     */
    public CGrid( CControl control ){
        this.control = control;
    }
    
    /**
     * Creates and returns a tree which contains the {@link CommonDockable}s
     * of this {@link CGrid}. The branches of the tree are put in a way, that
     * the boundaries of the {@link CommonDockable}s are respected as good
     * as possible.
     * @return the contents of this grid as tree
     */
    public DockableSplitDockTree toTree(){
        return grid.toTree();
    }
    
    /**
     * Adds a new set of {@link CDockable}s to this grid. The {@link CDockable}s
     * are also added to the {@link CControl} of this <code>CGrid</code>.
     * @param x the x-coordinate of the dockables
     * @param y the y-coordinate of the dockables
     * @param width the width of the dockables
     * @param height the height of the dockables
     * @param dockables a list of {@link SingleCDockable}s and {@link MultipleCDockable}s.
     */
    public void add( double x, double y, double width, double height, CDockable... dockables ){
        Dockable[] intern = new Dockable[ dockables.length ];
        for( int i = 0; i < intern.length; i++ ){
            CDockable dockable = dockables[i];
            if( control != null ){
                if( dockable instanceof SingleCDockable ){
                    control.addDockable( (SingleCDockable)dockable );
                }
                else if( dockable instanceof MultipleCDockable ){
                	if( dockable.getControl() == null ){
                		control.addDockable( (MultipleCDockable)dockable );
                	}
                }
            }
            
            intern[i] = dockable.intern();
        }
        
        grid.addDockable( x, y, width, height, intern );
    }
    
    /**
     * Marks <code>dockable</code> as beeing selected in the stack that
     * has the boundaries of <code>x, y, width, height</code>.
     * @param x the x coordinate of the stack
     * @param y the y coordinate of the stack
     * @param width the width of the stack
     * @param height the height of the stack
     * @param dockable the element to select, not <code>null</code>
     * @throws IllegalArgumentException if <code>dockable</code> is not registered at location <code>x/y/width/height</code>
     */
    public void select( double x, double y, double width, double height, CDockable dockable ){
    	grid.setSelected( x, y, width, height, dockable.intern() );
    }
    
    /**
     * Informs this grid about a horizontal divider that should be inserted
     * into the layout. There are no guarantees that the divider really is inserted.
     * @param x1 the first x coordinate of the divider
     * @param x2 the second x coordinate of the divider
     * @param y the y coordinate of the divider
     */
    public void addHorizontalDivider( double x1, double x2, double y ){
        grid.addHorizontalDivider( x1, x2, y );
    }

    /**
     * Informs this grid about a vertical divider that should be inserted
     * into the layout. There are no guarantees that the divider really is inserted.
     * @param x the x coordinate of the divider
     * @param y1 the first y coordinate of the divider
     * @param y2 the second y coordinate of the divider
     */
    public void addVerticalDivider( double x, double y1, double y2 ){
        grid.addVerticalDivider( x, y1, y2 );
    }
}


