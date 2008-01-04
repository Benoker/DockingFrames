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
package bibliothek.gui.dock.facile.location;

import bibliothek.gui.dock.facile.FLocation;
import bibliothek.gui.dock.facile.FWorkingArea;
import bibliothek.gui.dock.facile.intern.FDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;

/**
 * A location representing a {@link FWorkingArea}.
 * @author Benjamin Sigg
 *
 */
public class FWorkingAreaLocation extends FRootLocation{
    /** the area to which this location relates, can be <code>null</code> */
    private FWorkingArea area;
    
    public FWorkingAreaLocation( FWorkingArea area ){
        if( area == null )
            throw new NullPointerException( "area must not be null" );
        this.area = area;
    }
    
    /**
     * Gets the workingarea to which this location relates.
     * @return the area or <code>null</code> if the default center is meant.
     */
    public FWorkingArea getWorkingArea(){
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
    public FCenterRectangleLocation rectangle( double x, double y, double width, double height ){
        return new FCenterRectangleLocation( this, x, y, width, height );
    }
    
    /**
     * Creates a location describing a normalized element at the north of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public FCenterTreeLocationRoot north( double size ){
        return new FCenterTreeLocationRoot( this, size, Side.NORTH );
    }   

    /**
     * Creates a location describing a normalized element at the south of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public FCenterTreeLocationRoot south( double size ){
        return new FCenterTreeLocationRoot( this, size, Side.SOUTH );
    }
    
    /**
     * Creates a location describing a normalized element at the east of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public FCenterTreeLocationRoot east( double size ){
        return new FCenterTreeLocationRoot( this, size, Side.EAST );
    }

    /**
     * Creates a location describing a normalized element at the west of 
     * the normalized-area.
     * @param size the relative size of the element, a value between 0 (no space)
     * and 1 (all space).
     * @return the new location
     */
    public FCenterTreeLocationRoot west( double size ){
        return new FCenterTreeLocationRoot( this, size, Side.WEST );
    }

    @Override
    public FLocation aside() {
        return this;
    }
    
    @Override
    public String findRootNormal() {
        return area.getId();
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
        return area.getId();
    }
}
