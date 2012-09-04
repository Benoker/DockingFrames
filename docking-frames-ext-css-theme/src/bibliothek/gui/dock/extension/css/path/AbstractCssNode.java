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
package bibliothek.gui.dock.extension.css.path;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.dock.extension.css.CssNode;
import bibliothek.gui.dock.extension.css.CssNodeListener;

/**
 * This abstract implementation of a {@link CssNode} offers methods to store
 * and fire {@link CssNodeListener}. Further more several methods are implemented
 * telling that this node has no special properties.
 * @author Benjamin Sigg
 */
public abstract class AbstractCssNode implements CssNode{
	private List<CssNodeListener> listeners = new ArrayList<CssNodeListener>( 5 );
	
	@Override
	public void addNodeListener( CssNodeListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			bind();
		}
		listeners.add( listener );
	}
	
	@Override
	public void removeNodeListener( CssNodeListener listener ){
		if( !listeners.isEmpty() ){
			listeners.remove( listener );
			if( listeners.isEmpty() ){
				unbind();
			}
		}
	}
	
	protected void fireNodeChanged(){
		for( CssNodeListener listener : listeners.toArray( new CssNodeListener[ listeners.size() ] )){
			listener.nodeChanged( this );
		}
	}
	
	/**
	 * Called if the amount of observers is increased from <code>0</code> to <code>1</code>.
	 */
	protected abstract void bind();
	
	/**
	 * Called if the amount of observers is decreased from <code>1</code> to <code>0</code>.
	 */
	protected abstract void unbind();
	
	/**
	 * Tells whether at least one observer is registered.
	 * @return whether this object is monitored
	 */
	protected boolean isBound(){
		return !listeners.isEmpty();
	}
	
	public String getIdentifier(){
		return null;
	}
	
	public String getProperty( String key ){
		return null;
	}
	
	public boolean hasClass( String className ){
		return false;
	}
	
	public boolean hasPseudoClass( String className ){
		return false;
	}
}
