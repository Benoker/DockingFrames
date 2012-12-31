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
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.stack.StackDockProperty;

/**
 * A location describing an element in a stack (also known as tabbed-pane).
 * @author Benjamin Sigg
 *
 */
public class CStackLocation extends AbstractStackholdingLocation{
	/** the index of the tab of this location */
	private int index;
	/** location of the stack */
	private CLocation parent;
	
	/**
	 * Creates a new location
	 * @param parent the location of the stack
	 */
	public CStackLocation( CLocation parent ){
		this( parent, Integer.MAX_VALUE );
	}
	
	/**
	 * Creates a new location
	 * @param parent the location of the stack onto which this location builds
	 * @param index the location of the tab represented by this location
	 */
	public CStackLocation( CLocation parent, int index ){
		if( parent == null )
			throw new IllegalArgumentException( "Parent must not be null" );
		
		this.parent = parent;
		this.index = index;
	}
	
	/**
	 * Gets the parent location of this {@link CStackLocation}.
	 * @return the parent location, not <code>null</code>
	 */
	public CLocation getParent(){
		return parent;
	}
	
	/**
	 * Gets the index to which this location points on a stack.
	 * @return the index, may be {@link Integer#MAX_VALUE}
	 */
	public int getIndex(){
		return index;
	}
	
	@Override
	public String findRoot(){
		return parent.findRoot();
	}
	
	@Override
	public ExtendedMode findMode(){
		return parent.findMode();
	}
	
	@Override
	public DockableProperty findProperty( DockableProperty successor ){
		StackDockProperty stack = new StackDockProperty( index );
		stack.setSuccessor( successor );
		return parent.findProperty( stack );
	}
	
	/**
	 * @deprecated see {@link CLocation#aside()} for an explanation.
	 */
	@Deprecated
	@Override
	public CLocation aside() {
	    if( index == Integer.MAX_VALUE )
	        return this;
	    else
	        return new CStackLocation( parent, index+1 );
	}
	
	@Override
	public String toString() {
	    return String.valueOf( parent ) + " [stack " + index + "]";
	}
}
