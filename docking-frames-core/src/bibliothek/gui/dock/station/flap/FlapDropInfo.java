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

package bibliothek.gui.dock.station.flap;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.station.support.CombinerSource;
import bibliothek.gui.dock.station.support.CombinerTarget;

/**
 * Information where to insert a {@link Dockable} into a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public abstract class FlapDropInfo implements CombinerSource {
    /** location between buttons */
    private int index;
    
    /** The {@link Dockable} which is inserted */
    private Dockable dockable;
    
    /** the owner of this info */
    private FlapDockStation station;
    
    /** Tells how to combine {@link #dockable} with an existing Dockable */
    private CombinerTarget combineTarget = null;
    
    
    /**
     * Constructs a new info.
     * @param station the owner of this info
     * @param dockable the {@link Dockable} which will be inserted
     */
    public FlapDropInfo( FlapDockStation station, Dockable dockable ){
    	this.station = station;
        this.dockable = dockable;
    }
    
    /**
     * Tells how to combine {@link #getDockable()} with the existing {@link Dockable}.
     * @return the combination, can be <code>null</code>
     */
    public CombinerTarget getCombineTarget(){
		return combineTarget;
	}
    
    /**
     * Sets how to combine {@link #getDockable()} with the existing {@link Dockable}.
     * @param combineTarget the combination, can be <code>null</code>
     */
    public void setCombineTarget( CombinerTarget combineTarget ){
		this.combineTarget = combineTarget;
	}
    
    /**
     * Gets the {@link Dockable} which will be dropped or moved on the station.
     * @return the source
     */
    public Dockable getDockable() {
        return dockable;
    }

    /**
     * Gets the <code>index</code> property.
     * @return the property
     * @see #setIndex(int)
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the location where the {@link #getDockable() Dockable} will be
     * inserted in respect to the list of buttons.
     * @param index the location
     */
    public void setIndex( int index ) {
        this.index = index;
    }

	public Dockable getNew(){
		return getDockable();
	}

	public DockStation getParent(){
		return station;
	}
}