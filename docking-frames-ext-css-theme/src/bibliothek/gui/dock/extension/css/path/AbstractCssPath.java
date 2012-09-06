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
import bibliothek.gui.dock.extension.css.CssPath;

/**
 * This abstract implementation of a {@link CssPath} offers methods to store
 * and fire {@link CssPathListener}s.
 * @author Benjamin Sigg
 */
public abstract class AbstractCssPath implements CssPath{
	private List<CssPathListener> listeners = new ArrayList<CssPathListener>( 5 );
	
	@Override
	public void addPathListener( CssPathListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		if( listeners.isEmpty() ){
			bind();
		}
		listeners.add( listener );
	}
	
	@Override
	public void removePathListener( CssPathListener listener ){
		if( !listeners.isEmpty() ){
			listeners.remove( listener );
			if( listeners.isEmpty() ){
				unbind();
			}
		}
	}
	
	protected void firePathChanged(){
		for( CssPathListener listener : listeners.toArray( new CssPathListener[ listeners.size() ] )){
			listener.pathChanged( this );
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
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		
		for( int i = 0, n = getSize(); i<n; i++ ){
			CssNode node = getNode( i );
			if( builder.length() > 0 ){
				builder.append( ", " );
			}
			builder.append( node.getName() );
			if( node.getIdentifier() != null ){
				builder.append( "#" ).append( node.getIdentifier() );
			}
		}
		
		return builder.toString();
	}
}
