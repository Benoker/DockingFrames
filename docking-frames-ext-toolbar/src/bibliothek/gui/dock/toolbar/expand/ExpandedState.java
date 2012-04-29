/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.Dockable;

/**
 * Describes the state of a {@link Dockable} as seen by the
 * {@link ExpandableToolbarItemStrategyListener} and by
 * {@link ExpandableToolbarItem}.
 * 
 * @author Benjamin Sigg
 */
public enum ExpandedState{
	/** an expanded {@link Dockable} has the largest possible size */
	EXPANDED,
	/** a stretched {@link Dockable} has a medium size */
	STRETCHED,
	/** a shrunk {@link Dockable} has the smallest possible size */
	SHRUNK;
	
	/**
	 * Orders the {@link ExpandedState}s according to their size, and returns the
	 * <code>size</code>'th state.
	 * @param size the size of the state, where 0 is the smallest state.
	 * @return the state, not <code>null</code>
	 * @throws IllegalArgumentException if <code>size</code> is smaller than 0 or bigger
	 * than the number of available states
	 */
	public static ExpandedState getOrdered( int size ){
		switch( size ){
			case 0: return SHRUNK;
			case 1: return STRETCHED;
			case 2: return EXPANDED;
			default: throw new IllegalArgumentException( "size out of bounds: " + size );
		}
	}
	
	/**
	 * Gets the order of this state. The order tells how big this state is compared to the
	 * other states, 0 is the smallest state. This formula always holds true:
	 * <code>x == ExpandedState.getOrdered( x.getOrder() );</code>
	 * @return the order of this state
	 */
	public int getOrder(){
		switch( this ){
			case SHRUNK: return 0;
			case STRETCHED: return 1;
			case EXPANDED: return 2;
			default: throw new IllegalStateException( "never happens" ); 
		}
	}
	
	/**
	 * Gets the next smaller state.
	 * @return the next smaller state or <code>this</code> if there is no smaller state
	 */
	public ExpandedState smaller(){
		int order = getOrder()-1;
		if( order < 0 ){
			return this;
		}
		return getOrdered( order );
	}
	
	/**
	 * Gets the next larger state.
	 * @return the next larger state or <code>null</code> if there is no larger state
	 */
	public ExpandedState larger(){
		int order = getOrder()+1;
		if( order >= values().length ){
			return this;
		}
		return getOrdered( order );
	}
}
