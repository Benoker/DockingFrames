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
package bibliothek.gui.dock.extension.css.util;

import java.util.HashMap;
import java.util.Map;

import bibliothek.gui.dock.extension.css.CssProperty;
import bibliothek.gui.dock.extension.css.CssPropertyContainer;
import bibliothek.gui.dock.extension.css.CssPropertyContainerListener;
import bibliothek.gui.dock.extension.css.CssPropertyKey;

/**
 * This observer monitors a tree of {@link CssPropertyContainer}s.
 * @author Benjamin Sigg
 */
public abstract class CssPropertyTreeObserver {
	private CssPropertyContainer root;
	private Listener listener;
	
	/**
	 * Creates a new observer.
	 * @param root the root node of the tree, not <code>null</code>
	 */
	public CssPropertyTreeObserver( CssPropertyContainer root ){
		if( root == null ){
			throw new IllegalArgumentException( "root must not be null" );
		}
		this.root = root;
	}
	
	/**
	 * Activates or deactivates this observer. If the state changes
	 * {@link #onAdded(CssPropertyKey, CssProperty)} and {@link #onRemoved(CssPropertyKey)} will be called
	 * during the execution of this method.
	 * @param listening whether to observer the root container
	 */
	public void setListening( boolean listening ){
		if( listening ){
			if( listener == null ){
				listener = new Listener( null, root );
			}
		}
		else{
			if( listener != null ){
				listener.destroy();
				listener = null;
			}
		}
	}
	
	/**
	 * Tells whether the root container is currently observed
	 * @return whether this observer is active
	 */
	public boolean isListening(){
		return listener != null;
	}
	
	/**
	 * Called if a new <code>property</code> has been added.
	 * @param key the name of the property relative to the root (does not include the name of the root)
	 * @param property the new property
	 */
	protected abstract void onAdded( CssPropertyKey key, CssProperty<?> property );
	
	/**
	 * Called if a property has been removed.
	 * @param key the name of the property relative to the root (does not include the name of the root)
	 */
	protected abstract void onRemoved( CssPropertyKey key );
	
	private class Listener implements CssPropertyContainerListener{
		private CssPropertyContainer container;
		private CssPropertyKey key;
		private Map<String, Listener> children;
		
		public Listener( CssPropertyKey key, CssPropertyContainer container ){
			this.key = key;
			this.container = container;
			container.addPropertyContainerListener( this );
			for( String child : container.getPropertyKeys() ){
				propertyAdded( container, child, container.getProperty( child ) );
			}
		}
		
		public void destroy(){
			container.removePropertyContainerListener( this );
			if( children != null ){
				for( Listener child : children.values() ){
					child.destroy();
				}
			}
			if( key != null ){
				onRemoved( key );
			}
		}
		
		@Override
		public void propertyAdded( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( children == null ){
				children = new HashMap<String, Listener>();
			}
			CssPropertyKey next;
			if( this.key == null ){
				next = new CssPropertyKey( key );
			}
			else{
				next = this.key.append( key );
			}
			onAdded( next, property );
			Listener listener = new Listener( next, property );
			children.put( key, listener );
		}
		
		@Override
		public void propertyRemoved( CssPropertyContainer source, String key, CssProperty<?> property ){
			if( children != null ){
				Listener listener = children.remove( key );
				listener.destroy();
				onRemoved( listener.key );
			}
		}
	}
}
