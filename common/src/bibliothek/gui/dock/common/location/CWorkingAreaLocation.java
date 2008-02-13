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
package bibliothek.gui.dock.common.location;

import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.CWorkingArea;
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A location representing a {@link CWorkingArea}.
 * @author Benjamin Sigg
 *
 */
public class CWorkingAreaLocation extends CRootLocation{
    /** the area to which this location relates, can be <code>null</code> */
    private CWorkingArea area;
    
    public CWorkingAreaLocation( CWorkingArea area ){
        if( area == null )
            throw new NullPointerException( "area must not be null" );
        this.area = area;
    }
    
    /**
     * Gets the workingarea to which this location relates.
     * @return the area or <code>null</code> if the default center is meant.
     */
    public CWorkingArea getWorkingArea(){
        return area;
    }
    
    /**
     * Creates a location describing a normalized element at a given location.
     * Note that the normalized area is seen as a rectangle of size 1/1.
     * @param x the x-coordinate, a value between 0 and 1
     * @param y the y-coordinate, a value between 0 and 1
     * @param width the width, <code>x + width</code> should be less or equal to 1
     * @param height the height, <code>y + height</code> should be less or equal to 1
     * @return the new location
     */
    public CContentAreaRectangleLocation rectangle( double x, double y, double width, double height ){
        return new CContentAreaRectangleLocation( this, x, y, width, height );
    }
    
    /**
     * Creates a location describing a normalized element at the north of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public CContentAreaTreeLocationRoot north( double size ){
        return new CContentAreaTreeLocationRoot( this, size, Side.NORTH );
    }   

    /**
     * Creates a location describing a normalized element at the south of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public CContentAreaTreeLocationRoot south( double size ){
        return new CContentAreaTreeLocationRoot( this, size, Side.SOUTH );
    }
    
    /**
     * Creates a location describing a normalized element at the east of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public CContentAreaTreeLocationRoot east( double size ){
        return new CContentAreaTreeLocationRoot( this, size, Side.EAST );
    }

    /**
     * Creates a location describing a normalized element at the west of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public CContentAreaTreeLocationRoot west( double size ){
        return new CContentAreaTreeLocationRoot( this, size, Side.WEST );
    }

    @Override
    public CLocation aside() {
        return this;
    }
    
    @Override
    public String findRootNormal() {
        return area.getUniqueId();
    }

    @Override
    public ExtendedMode findMode() {
        return ExtendedMode.NORMALIZED;
    }

    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
        return null;
    }

    @Override
    public String findRoot() {
        return area.getUniqueId();
    }
}
