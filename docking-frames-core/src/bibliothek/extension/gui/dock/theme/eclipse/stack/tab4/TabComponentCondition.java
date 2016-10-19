/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse.stack.tab4;

import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabComponent;

/**
 * A {@link TabComponentCondition} is a layer around a {@link TabComponent} that offers access to an
 * implicitly defined condition, e.g. whether the mouse is over the {@link TabComponent}.
 * @author Benjamin Sigg
 */
public abstract class TabComponentCondition {
	/** the observed component */
	private TabComponent component;
	
	/** the current state of the condition */
	private boolean condition = false;
	
	/** whether the value of {@link #condition} is valid */
	private boolean valid = false;
	
	/**
	 * Creates a new condition
	 * @param component the component to observe, not <code>null</code>
	 */
	public TabComponentCondition( TabComponent component ){
		if( component == null ){
			throw new IllegalArgumentException( "component must not be null" );
		}
		this.component = component;
	}
	
	/**
	 * Gets the {@link TabComponent} which is observed by this condition.
	 * @return the observed component
	 */
	public TabComponent getComponent(){
		return component;
	}
	
	/**
	 * Reads the condition. This method is only called if {@link #invalidate()} was called. Otherwise the previously
	 * stored value is returned.
	 * @return <code>true</code> if the condition is fulfilled, <code>false</code> otherwise
	 */
	protected abstract boolean checkCondition();
	
	/**
	 * Tells this condition that it is no longer valid and that its state has to be
	 * calculated again.
	 */
	public void invalidate(){
		valid = false;
	}
	
	/**
	 * Makes sure the state of this condition is valid. This method does nothing if
	 * {@link #invalidate()} was not called.
	 */
	public void validate(){
		if( !valid ){
			condition = checkCondition();
			valid = true;
		}
	}
	
	/**
	 * Tells whether the condition is fulfilled or not.
	 * @return the state of this condition
	 */
	public boolean getCondition(){
		return condition;
	}
}
