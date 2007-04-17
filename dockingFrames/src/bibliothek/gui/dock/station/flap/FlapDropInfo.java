/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.station.flap;

import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.station.FlapDockStation;

/**
 * Information where to insert a {@link Dockable} into a {@link FlapDockStation}.
 * @author Benjamin Sigg
 */
public class FlapDropInfo {
    /** location between buttons */
    private int index;
    
    /** <code>true</code> if a combination with the current popup should be made */
    private boolean combine = false;
    
    /** <code>true</code> if information should be painted */
    private boolean draw = false;
    
    /** The {@link Dockable} which is inserted */
    private Dockable dockable;
    
    /**
     * Constructs a new info.
     * @param dockable the {@link Dockable} which will be inserted
     */
    public FlapDropInfo( Dockable dockable ){
        this.dockable = dockable;
    }

    /**
     * Returns the <code>combine</code>  property.
     * @return the property
     * @see #setCombine(boolean)
     */
    public boolean isCombine() {
        return combine;
    }

    /**
     * Sets the <code>combine</code> property. If this property is <code>true</code>,
     * the station will combine the new {@link Dockable} with the current 
     * front-dockable.
     * @param combine <code>true</code> if a combination should be made
     */
    public void setCombine( boolean combine ) {
        this.combine = combine;
    }

    /**
     * Gets the {@link Dockable} which will be dropped or moved on the station.
     * @return the source
     */
    public Dockable getDockable() {
        return dockable;
    }

    /**
     * Gets the <code>draw</code> property
     * @return the property
     * @see #setDraw(boolean)
     */
    public boolean isDraw() {
        return draw;
    }

    /**
     * Sets the <code>draw</code> property. If this property is true, the
     * station will paint some information where the {@link #getDockable() Dockable}
     * will be inserted. This information is only a help to the user.
     * @param draw <code>true</code> if the station should paint something
     */
    public void setDraw( boolean draw ) {
        this.draw = draw;
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
     * inserted in respect to the list of buttons. This property will
     * be ignored if {@link #isCombine() combine} is <code>true</code>.
     * @param index the location
     */
    public void setIndex( int index ) {
        this.index = index;
    }
    
    
}