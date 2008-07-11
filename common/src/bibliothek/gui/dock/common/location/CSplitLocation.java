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

import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.intern.CDockable.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.split.SplitDockPathProperty;
import bibliothek.gui.dock.station.split.SplitDockProperty;

/**
 * This location is used to describe a {@link SplitDockStation}.
 * @author Benjamin Sigg
 */
public abstract class CSplitLocation extends CLocation{
    @Override
    public CLocation aside() {
        return this;
    }

    /**
     * Creates a location which occupies a sub-rectangle of the {@link SplitDockStation}
     * which is described by this location. All arguments of this method must
     * be between 0 and 1, and <code>x+width</code> and <code>y+height</code> should
     * be smaller or equal to 1.
     * @param x the relative x coordinate
     * @param y the relative y coordinate
     * @param width the relative width
     * @param height the relative height
     * @return the location describing a rectangle
     */
    public CRectangleLocation rectangle( double x, double y, double width, double height ){
        return new CRectangleLocation( this, x, y, width, height );
    }
    
    /**
     * Creates a location that describes a space in the upper part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot north( double size ){ 
        return new TreeLocationRoot( this, size, Side.NORTH );
    }
    
    /**
     * Creates a location that describes a space in the lower part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot south( double size ){ 
        return new TreeLocationRoot( this, size, Side.SOUTH );
    }
    
    /**
     * Creates a location that describes a space in the right part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot east( double size ){ 
        return new TreeLocationRoot( this, size, Side.EAST );
    }
    
    /**
     * Creates a location that describes a space in the left part of
     * the {@link SplitDockStation}.
     * @param size the size of the space, between 0 and 1
     * @return the new location
     */
    public TreeLocationRoot west( double size ){ 
        return new TreeLocationRoot( this, size, Side.WEST );
    }
    
    @Override
    public CLocation expandProperty( DockableProperty property ) {
        CLocation location = null;
        
        if( property instanceof SplitDockProperty ){
            SplitDockProperty split = (SplitDockProperty)property;
            location = rectangle( split.getX(), split.getY(), split.getWidth(), split.getHeight() );
        }
        else if( property instanceof SplitDockPathProperty ){
            SplitDockPathProperty path = (SplitDockPathProperty)property;
            if( path.size() > 0 ){
                AbstractTreeLocation tree = null;
                SplitDockPathProperty.Node node = path.getNode( 0 );
                switch( node.getLocation() ){
                    case BOTTOM: 
                        tree = south( node.getSize() ); 
                        break;
                    case LEFT:
                        tree = west( node.getSize() );
                        break;
                    case RIGHT:
                        tree = east( node.getSize() );
                        break;
                    case TOP:
                        tree = north( node.getSize() );
                        break;
                }
                
                for( int i = 1, n = path.size(); i<n; i++ ){
                    node = path.getNode( i );
                    switch( node.getLocation() ){
                        case BOTTOM:
                            tree = tree.south( node.getSize() );
                            break;
                        case LEFT:
                            tree = tree.west( node.getSize() );
                            break;
                        case RIGHT:
                            tree = tree.east( node.getSize() );
                            break;
                        case TOP:
                            tree = tree.north( node.getSize() );
                            break;
                    }
                }
                location = tree;
            }
            else{
                location = rectangle( 0, 0, 1, 1 );
            }
        }
        
        DockableProperty successor = property.getSuccessor();
        if( successor == null )
            return location;
        else
            return location.expandProperty( successor );
    }

    @Override
    public ExtendedMode findMode() {
        return ExtendedMode.NORMALIZED;
    }

    @Override
    public DockableProperty findProperty( DockableProperty successor ) {
        SplitDockProperty property = new SplitDockProperty( 0, 0, 1, 1 );
        property.setSuccessor( successor );
        return property;
    }
}
