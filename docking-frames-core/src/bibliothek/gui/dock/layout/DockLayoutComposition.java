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
package bibliothek.gui.dock.layout;

import java.util.List;

import bibliothek.gui.dock.DockElement;

/**
 * A {@link DockLayoutComposition} describes all relations and positions of a
 * set of {@link DockElement}s. The <code>DockLayoutComposition</code> does not
 * have any references to <code>DockElement</code>s, but with the help of a
 * {@link DockSituation} new trees of <code>DockElement</code>s can be created.
 * @author Benjamin Sigg
 */
public class DockLayoutComposition {
    /** a description of the {@link DockElement} that is represented by this composition */
    private DockLayoutInfo layout;
    /** additional information about the element */
    private List<DockLayout<?>> adjacent;
    /** the list of known children of this element */
    private List<DockLayoutComposition> children;
    /** tells that the children of this station should be ignored */
    private boolean ignoreChildren;
    
    /**
     * Creates a new composition.
     * @param layout the content of the element that is represented by this composition,
     * can be <code>null</code>
     * @param adjacent additional information about the element, can be <code>null</code>
     * @param children the children of the station represented by this composition
     * @param ignoreChildren whether the children should be ignored or not
     */
    public DockLayoutComposition( DockLayoutInfo layout, List<DockLayout<?>> adjacent, List<DockLayoutComposition> children, boolean ignoreChildren ){
        if( children == null )
            throw new IllegalArgumentException( "children must not be null" );
        
        this.layout = layout;
        this.adjacent = adjacent;
        this.children = children;
        this.ignoreChildren = ignoreChildren;
    }
    
    /**
     * Gets the layout which describes the element of this composition.
     * @return the layout, can be <code>null</code> to indicate that this composition
     * was not loaded properly
     */
    public DockLayoutInfo getLayout() {
        return layout;
    }
    
    /**
     * Gets the additional information about the element.
     * @return the additional information or <code>null</code>
     */
    public List<DockLayout<?>> getAdjacent() {
        return adjacent;
    }
    
    /**
     * Searches for the {@link DockLayout} whose factory is set to <code>factoryId</code>.
     * @param factoryId the name of some {@link AdjacentDockFactory}.
     * @return the matching layout or <code>null</code> if not found
     */
    public DockLayout<?> getAdjacent( String factoryId ){
    	if( adjacent != null ){
	    	for( DockLayout<?> layout : adjacent ){
	    		if( layout.getFactoryID().equals( factoryId )){
	    			return layout;
	    		}
	    	}
    	}
    	return null;
    }
    
    /**
     * Gets the list of all known children of this composition.
     * @return the list of children
     */
    public List<DockLayoutComposition> getChildren() {
        return children;
    }
    
    /**
     * Tells that the children of this composition were ignored.
     * @return <code>true</code> if the children are to be ignored
     */
    public boolean isIgnoreChildren() {
        return ignoreChildren;
    }
}
