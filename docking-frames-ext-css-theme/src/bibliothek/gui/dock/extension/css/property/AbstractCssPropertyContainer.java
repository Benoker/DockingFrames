/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.extension.css.property;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;

/**
 * This implementation of {@link CssPropertyContainer} offers methods to store observers
 * and fire events to them.
 * @author Benjamin Sigg
 */
public abstract class AbstractCssPropertyContainer implements CssPropertyContainer{
	private List<CssPropertyContainerListener> listeners = new ArrayList<CssPropertyContainerListener>( 3 );
	
	/**
	 * Called if the number of observers increased from <code>0</code> to <code>1</code>.
	 */
	protected abstract void bind();
	
	/**
	 * Called if the number of observers decreased from <code>1</code> to <code>0</code>.
	 */
	protected abstract void unbind();
	
	/**
	 * Tells whether there is at least one observer
	 * @return whether at least one observer has been added
	 */
	protected boolean isBound(){
		return !listeners.isEmpty();
	}
	
	/**
	 * Calls {@link CssPropertyContainerListener#propertyAdded(CssPropertyContainer, String, CssProperty)} on
	 * all known observers.
	 * @param key the key of the new property
	 * @param property the new property
	 */
	protected void firePropertyAdded( String key, CssProperty<?> property ){
		for( CssPropertyContainerListener listener : listeners() ){
			listener.propertyAdded( this, key, property );
		}
	}
	
	/**
	 * CAlls {@link CssPropertyContainerListener#propertyRemoved(CssPropertyContainer, String, CssProperty)} on
	 * all known observers.
	 * @param key the key of the removed property
	 * @param property the removed property
	 */
	protected void firePropertyRemoved( String key, CssProperty<?> property ){
		for( CssPropertyContainerListener listener : listeners() ){
			listener.propertyRemoved( this, key, property );
		}
	}
	
	private CssPropertyContainerListener[] listeners(){
		return listeners.toArray( new CssPropertyContainerListener[ listeners.size() ] );
	}
	
	@Override
	public void addPropertyContainerListener( CssPropertyContainerListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			bind();
		}
		listeners.add( listener );
	}
	
	@Override
	public void removePropertyContainerListener( CssPropertyContainerListener listener ){
		if( !listeners.isEmpty() ){
			listeners.remove( listener );
			if( listeners.isEmpty() ){
				unbind();
			}
		}
	}
}
