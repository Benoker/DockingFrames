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
package bibliothek.gui.dock.extension.css;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.dock.extension.css.property.AbstractCssPropertyContainer;

/**
 * The default {@link CssItem} offers methods to easily add and remove
 * {@link CssProperty}s.
 * @author Benjamin Sigg
 */
public class DefaultCssItem extends AbstractCssPropertyContainer implements CssItem{
	private List<CssItemListener> listeners = new ArrayList<CssItemListener>();
	
	private CssPath path;
	
	private Map<String, CssProperty<?>> properties = new HashMap<String, CssProperty<?>>();
	
	/**
	 * Creates a new item
	 * @param path the path of this item, not <code>null</code>
	 */
	public DefaultCssItem( CssPath path ){
		setPath( path );
	}
	
	@Override
	protected void bind(){
		// ignore
	}
	
	@Override
	protected void unbind(){
		// ignore	
	}
	
	@Override
	public CssPath getPath(){
		return path;
	}
	
	public void setPath( CssPath path ){
		if( path == null ){
			throw new IllegalArgumentException( "path must not be null" );
		}
		this.path = path;
		for( CssItemListener listener : listeners() ){
			listener.pathChanged( this );
		}
	}

	@Override
	public String[] getPropertyKeys(){
		return properties.keySet().toArray( new String[ properties.size() ] );
	}

	@Override
	public CssProperty<?> getProperty( String key ){
		return properties.get( key );
	}
	
	public void putProperty( String key, CssProperty<?> property ){
		CssProperty<?> oldProperty = properties.remove( key );
		if( oldProperty != null ){
			firePropertyRemoved( key, oldProperty );
		}
		if( property != null ){
			properties.put( key, property );
			firePropertyAdded( key, property );
		}
	}
	
	private CssItemListener[] listeners(){
		return listeners.toArray( new CssItemListener[ listeners.size() ] );
	}

	@Override
	public void addItemListener( CssItemListener listener ){
		if( listener == null ){
			throw new IllegalArgumentException( "listener must not be null" );
		}
		listeners.add( listener );
	}

	@Override
	public void removeItemListener( CssItemListener listener ){
		listeners.remove( listener );
	}

}
