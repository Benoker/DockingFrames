/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.common.mode;

import java.util.LinkedList;
import java.util.List;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.facile.action.KeyedActionSource;
import bibliothek.gui.dock.facile.mode.DefaultLocationModeActionProvider;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.facile.mode.LocationModeActionProvider;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * This {@link LocationModeActionProvider} returns a {@link KeyedActionSource} for
 * each {@link CDockable} it encounters.
 * @author Benjamin Sigg
 *
 */
public class KeyedLocationModeActionProvider extends DefaultLocationModeActionProvider{
	/** key used for {@link CDockable#getAction(String)} */
	private String key;
	
	/** all the sources that are currently in use and created by this provider */
	private List<KeyedActionSource> sources = new LinkedList<KeyedActionSource>();
	
	/**
	 * Creates a new provider.
	 * @param key the key for {@link CDockable#getAction(String)}, must not be <code>null</code>
	 */
	public KeyedLocationModeActionProvider( String key ){
		if( key == null )
			throw new IllegalArgumentException( "key must not be null" );
		this.key = key;
	}
	
	/**
	 * Creates a new provider.
	 * @param key the key for {@link CDockable#getAction(String)}, must not be <code>null</code>
	 * @param defaultAction the default action, may be <code>null</code>
	 */	
	public KeyedLocationModeActionProvider( String key, CAction defaultAction ){
		this( key );
		setSelectModeAction( defaultAction );
	}
	
	/**
	 * Creates a new provider.
	 * @param key the key for {@link CDockable#getAction(String)}, must not be <code>null</code>
	 * @param defaultAction the default action, may be <code>null</code>
	 */
	public KeyedLocationModeActionProvider( String key, DockAction defaultAction ){
		this( key );
		setSelectModeAction( defaultAction );
	}
	
	@Override
	public void setSelectModeAction( DockAction selectModeAction ){
		super.setSelectModeAction( selectModeAction );
		for( KeyedActionSource source : sources ){
			source.setDefaultAction( selectModeAction );
		}
	}
	
	public DockActionSource getActions( Dockable dockable, Mode<Location> currentMode, DockActionSource currentSource ){
		if( currentSource instanceof KeyedActionSource ){
			if( ((KeyedActionSource)currentSource).getKey().equals( key )){
				return currentSource;
			}
		}
			
		
		if( dockable instanceof CommonDockable ){
			CDockable cdockable = ((CommonDockable)dockable).getDockable();
			KeyedActionSource source = new KeyedActionSource( cdockable, key );
			sources.add( source );
			source.setDefaultAction( getSelectModeAction() );
			source.setVisible( true );
			return source;
		}
		else{
			return super.getActions( dockable, currentMode, currentSource );
		}
	}
	
	public void destroy( Dockable dockable, DockActionSource source ){
		if( sources.remove( source )){
			((KeyedActionSource)source).destroy();
		}
	}
}
