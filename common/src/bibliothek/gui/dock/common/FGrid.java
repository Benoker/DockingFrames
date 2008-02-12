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
import bibliothek.gui.dock.common.intern.FDockable;
import bibliothek.gui.dock.common.intern.FacileDockable;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.split.SplitDockTree;

/**
 * A {@link FGrid} is a mechanism to layout a set of {@link FDockable} on
 * a {@link SplitDockStation} like the one used in the center of the
 * {@link FContentArea} or on the {@link FWorkingArea}. <code>FGrid</code>s
 * also can register new {@link FDockable}s to a {@link FControl}.<br>
 * A <code>FGrid</code> is a rectangle containing several {@link FDockable}s. All
 * these dockables have boundaries, within the rectangle of the grid.<br>
 * A grid can be deployed on {@link FWorkingArea}s or {@link FContentArea}s. 
 * As soon as a grid is deployed, all its dockables get visible.
 * 
 * @author Benjamin Sigg
 *
 */
public class FGrid {
    /** the internal representation of this grid */
    private SplitDockGrid grid = new SplitDockGrid();
    
    /** the control for which this grid is used */
    private FControl control;
    
    /**
     * Creates a new grid.
     */
    public FGrid(){
        // do nothing
    }
    
    /**
     * Creates a new grid. New {@link FDockable}s will be registered at
     * <code>control</code>.
     * @param control the control where this grid should register new
     * {@link FDockable}s.
     */
    public FGrid( FControl control ){
        this.control = control;
    }
    
    /**
     * Creates and returns a tree which contains the {@link FacileDockable}s
     * of this {@link FGrid}. The branches of the tree are put in a way, that
     * this boundaries of the {@link FacileDockable}s are respected as good
     * as possible.
     * @return the contents of this grid as tree
     */
    public SplitDockTree toTree(){
        return grid.toTree();
    }
    
    /**
     * Adds a new set of {@link FDockable}s to this grid. The {@link FDockable}s
     * are also added to the {@link FControl} of this <code>FGrid</code>.
     * @param x the x-coordinate of the dockables
     * @param y the y-coordinate of the dockables
     * @param width the width of the dockables
     * @param height the height of the dockables
     * @param dockables a list of {@link FSingleDockable}s and {@link FMultipleDockable}s.
     */
    public void add( double x, double y, double width, double height, FDockable... dockables ){
        Dockable[] intern = new Dockable[ dockables.length ];
        for( int i = 0; i < intern.length; i++ ){
            FDockable dockable = dockables[i];
            if( control != null ){
                if( dockable instanceof FSingleDockable ){
                    if( dockable.getControl() == null )
                        control.add( (FSingleDockable)dockable );
                }
                else if( dockable instanceof FMultipleDockable ){
                    if( dockable.getControl() == null )
                        control.add( (FMultipleDockable)dockable );
                }
            }
            
            intern[i] = dockable.intern();
        }
        
        grid.addDockable( x, y, width, height, intern );
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
     * Informs this grid about a horizontal divider that should be inserted
     * into the layout. There are no guarantees that the divider really is inserted.
     * @param x the x coordinate of the divider
     * @param y1 the first y coordinate of the divider
     * @param y2 the second y coordinate of the divider
     */
    public void addVerticalDivider( double x, double y1, double y2 ){
        grid.addVerticalDivider( x, y1, y2 );
    }
}











