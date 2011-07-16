/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.gui.dock.layout;

import bibliothek.gui.dock.DockFactory;

/**
 * A layout that stores another layout and maybe also an identifier
 * for a preloaded element. This class is used by the {@link PredefinedDockSituation} to wrap
 * the data that is created by {@link DockFactory}s.
 * @author Benjamin Sigg
 */
public class PredefinedLayout{
    /** the layout that stores the content */
    private DockLayoutInfo delegate;
    /** the id of the element which was predefined */
    private String predefined;

    /**
     * Creates a new layout.
     * @param predefined the element which was preloaded, not <code>null</code>
     * @param delegate the delegate which stores the content
     */
    public PredefinedLayout( String predefined, DockLayoutInfo delegate ){
    	if( predefined == null ){
    		throw new IllegalArgumentException( "argument 'preload' must not be null" );
    	}
    	
        this.predefined = predefined;
        this.delegate = delegate;
    }

    /**
     * Gets the id of the element which was predefined.
     * @return the identifier
     */
    public String getPredefined() {
        return predefined;
    }

    /**
     * Gets the layout which stores the contents of the predefined element.
     * @return the content
     */
    public DockLayoutInfo getDelegate() {
        return delegate;
    }
}