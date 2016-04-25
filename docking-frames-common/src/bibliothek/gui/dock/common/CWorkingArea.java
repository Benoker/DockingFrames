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
import bibliothek.gui.dock.common.intern.AbstractCDockable;
import bibliothek.gui.dock.common.intern.CControlAccess;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.location.CWorkingAreaLocation;
import bibliothek.gui.dock.common.perspective.CWorkingPerspective;
import bibliothek.gui.dock.station.split.DockableSplitDockTree;
import bibliothek.util.Path;

/**
 * A working area is an element which is always visible and contains some
 * {@link CDockable}s which can't be dragged out of it. Also no {@link CDockable}
 * can be dropped in a {@link CWorkingArea}.<br>
 * There can be more than one {@link CWorkingArea}, and the working areas
 * can be nested.
 * @author Benjamin Sigg
 */
public class CWorkingArea extends CGridArea{
	/** The result of {@link #getTypeId()} */
	public static final Path TYPE_ID = new Path( "dock", "CWorkingArea" );
    
    /**
     * Creates a new area.
     * @param control the owner of this station
     * @param uniqueId a unique identifier
     */
    public CWorkingArea( CControl control, String uniqueId ){
        super( control, uniqueId );
        setMaximizingArea( false );
    }
    
    @Override
    public boolean isWorkingArea(){
    	return true;
    }
    
    @Override
    public CLocation getStationLocation(){
    	return new CWorkingAreaLocation( this );
    }
    
    @Override
    public CWorkingPerspective createPerspective(){
    	return new CWorkingPerspective( getUniqueId(), getTypeId() );
    }
    
    /**
     * Exchanges all the {@link CDockable}s on this area with the
     * elements of <code>grid</code>. This method also calls
     * {@link CDockable#setWorkingArea(CStation)} for each
     * dockable in <code>grid</code>.
     * @param grid a grid containing some new {@link Dockable}s
     */
    public void deploy( CGrid grid ){
        DockableSplitDockTree tree = grid.toTree();
        
        for( Dockable dockable : tree.getDockables() ){
            if( dockable instanceof CommonDockable ){
                CommonDockable cdock = (CommonDockable)dockable;
                cdock.getDockable().setWorkingArea( this );
            }
        }
        
        getStation().dropTree( tree );
    }

    /**
     * First {@link #add(SingleCDockable) adds} <code>dockable</code> to the 
     * {@link CControl} of this {@link CWorkingArea}, then makes it visible at a good position. A good position
     * is {@link CLocation#aside()} the latest focused {@link Dockable} that is child of this station.<br>
     * This method does <b>not</b> force a focus transfer, clients need to call {@link AbstractCDockable#toFront()}
     * if the require a focus switch to <code>dockable</code>. 
     * @param dockable the element to show
     * @return <code>dockable</code>
     */
    public <F extends SingleCDockable> F show( F dockable ){
    	add( dockable );
    	dockable.setLocationsAsideFocused();
    	dockable.setVisible( true );
    	return dockable;
    }

    /**
     * First {@link #add(MultipleCDockable) adds} <code>dockable</code> to the 
     * {@link CControl} of this {@link CWorkingArea}, then makes it visible at a good position. A good position
     * is {@link CLocation#aside()} the latest focused {@link Dockable} that is child of this station. <br>
     * This method does <b>not</b> force a focus transfer, clients need to call {@link AbstractCDockable#toFront()}
     * if the require a focus switch to <code>dockable</code>.
     * @param dockable the element to show
     * @return <code>dockable</code>
     */
    public <F extends MultipleCDockable> F show( F dockable ){
    	add( dockable );
    	dockable.setLocationsAsideFocused();
    	dockable.setVisible( true );
    	return dockable;
    }
    
    /**
     * Ensures that <code>this</code> is the parent of <code>dockable</code>
     * and adds <code>dockable</code> to the {@link CControl} which is associated
     * with this {@link CWorkingArea}. If there is no <code>CControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends SingleCDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        CControlAccess access = control();
        if( access != null ){
            access.getOwner().addDockable( dockable );
        }
        return dockable;
    }
    
    /**
     * Ensures that <code>this</code> is the parent of <code>dockable</code>
     * and adds <code>dockable</code> to the {@link CControl} which is associated
     * with this {@link CWorkingArea}. If there is no <code>CControl</code>, then
     * the <code>dockable</code> is added nowhere.
     * @param <F> the type of element to add
     * @param dockable the new element
     * @return <code>dockable</code>
     */
    public <F extends MultipleCDockable> F add( F dockable ){
        dockable.setWorkingArea( this );
        CControlAccess access = control();
        if( access != null ){
            access.getOwner().addDockable( dockable );
        }
        return dockable;
    }
	
	@Override
	public Path getTypeId(){
		return TYPE_ID;
	}
}
