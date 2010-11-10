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

import bibliothek.gui.dock.common.MultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableLayout;

/**
 * An intermediate representation of the layout of a {@link CommonDockable}
 * that is connected with a {@link MultipleCDockable}.
 * @author Benjamin Sigg
 */
public class CommonMultipleDockableLayout {
    /** the layout of the {@link MultipleCDockable}  */
    private MultipleCDockableLayout layout;
    
    /** the unique id of the dockable */
    private String id;
    /** the area on which the dockable was, might be <code>null</code> */
    private String area;
    
    /**
     * Sets the description of the layout of a {@link MultipleCDockable}. The 
     * described dockable is connected with the {@link CommonDockable} for
     * which this layout was created.
     * @param layout the layout
     */
    public void setLayout( MultipleCDockableLayout layout ) {
        this.layout = layout;
    }
    
    /**
     * Gets the layout of the {@link MultipleCDockable} that is associated with
     * the {@link CommonDockable} for which this layout was created.
     * @return the layout
     */
    public MultipleCDockableLayout getLayout() {
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
     * Sets the unique id of the working-area the dockable was on.
     * @param area the id or <code>null</code>
     */
    public void setArea( String area ) {
        this.area = area;
    }
    
    /**
     * Gets the unique id of the working-area the dockable was on.
     * @return the id of an area or <code>null</code>
     */
    public String getArea() {
        return area;
    }
}
