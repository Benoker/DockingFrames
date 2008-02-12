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
package bibliothek.gui.dock.common.intern;

import bibliothek.gui.dock.common.FMultipleDockable;
import bibliothek.gui.dock.common.FMultipleDockableLayout;
import bibliothek.gui.dock.common.FWorkingArea;
import bibliothek.gui.dock.layout.DockLayout;

/**
 * An intermediate representation of the layout of a {@link FacileDockable}
 * that is connected with a {@link FMultipleDockable}.
 * @author Benjamin Sigg
 */
public class FacileDockableLayout implements DockLayout {
    /** the id of the factory that created this layout */
    private String factory;
    /** the layout of the {@link FMultipleDockable}  */
    private FMultipleDockableLayout layout;
    
    /** the unique id of the dockable */
    private String id;
    /** the area on which the dockable was, might be <code>null</code> */
    private String area;
    
    public String getFactoryID() {
        return factory;
    }

    public void setFactoryID( String id ) {
        this.factory = id;
    }

    /**
     * Sets the description of the layout of a {@link FMultipleDockable}. The 
     * described dockable is connected with the {@link FacileDockable} for
     * which this layout was created.
     * @param layout the layout
     */
    public void setLayout( FMultipleDockableLayout layout ) {
        this.layout = layout;
    }
    
    /**
     * Gets the layout of the {@link FMultipleDockable} that is associated with
     * the {@link FacileDockable} for which this layout was created.
     * @return the layout
     */
    public FMultipleDockableLayout getLayout() {
        return layout;
    }
    
    /**
     * Sets the unique id of the dockable.
     * @param id the unique id
     */
    public void setId( String id ) {
        this.id = id;
    }
    
    /**
     * Gets the unique id of the dockable.
     * @return the unique id
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the unique id of the {@link FWorkingArea} the dockable was on.
     * @param area the id or <code>null</code>
     */
    public void setArea( String area ) {
        this.area = area;
    }
    
    /**
     * Gets the unique id of the {@link FWorkingArea} the dockable was on.
     * @return the id of an area or <code>null</code>
     */
    public String getArea() {
        return area;
    }
}
