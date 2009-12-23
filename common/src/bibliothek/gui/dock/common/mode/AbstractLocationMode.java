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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * {@link Mode} that manages a set of key-value pairs.
 * @author Benjamin Sigg
 * @param <A> the values managed by this mode
 */
public abstract class AbstractLocationMode<A extends ModeArea> implements Iterable<A>, LocationMode{
	/** The areas managed by this mode */
	private Map<String, A> areas = new HashMap<String, A>();
	
	/** default location to use when a key is not found in {@link #areas} */
	private A defaultArea;
	
	/** the action to be triggered for activating this mode */
	private CAction selectModeAction;
	
	/** action source containing only {@link #selectModeAction} */
	private DefaultDockActionSource selectModeSource = new DefaultDockActionSource();
	
	/** the manager responsible for this mode */
	private ExtendedModeManager manager;

	
	/**
	 * Creates a new mode.
	 * @param manager the owner of this mode
	 */
	public AbstractLocationMode( ExtendedModeManager manager ){
		if( manager == null )
			throw new IllegalArgumentException( "manager must not be null" );
		this.manager = manager;
	}
	
	/**
	 * Gets the owner of this mode.
	 * @return the owner, not <code>null</code>
	 */
	public ExtendedModeManager getManager(){
		return manager;
	}
	
	/**
	 * Adds an area to this mode.
	 * @param area the new area, not <code>null</code>
	 */
	public void add( A area ){
		if( area == null )
			throw new IllegalArgumentException( "area must not be null" );
		
		String key = area.getUniqueId();
		if( areas.containsKey( key ))
			throw new IllegalArgumentException( "key '" + key + "' already in use" );
		areas.put( key, area );
	}
	
	/**
	 * Removes the area with identifier <code>key</code> from this
	 * mode.
	 * @param key the identifier of the area
	 * @return the removed area or <code>null</code>
	 */
	public A remove( String key ){
		A area = areas.remove( key );
		if( defaultArea == area ){
			defaultArea = null;
		}
		return area;
	}
	
	public Iterator<A> iterator(){
		return areas.values().iterator();
	}
	
	/**
	 * Sets the default area of this mode. The default area is used when
	 * {@link #get(Dockable)} returns <code>null</code> for some key.
	 * @param defaultArea the default area, can be <code>null</code>. Must be
	 * registered using {@link #add(ModeArea)} first.
	 */
	public void setDefaultArea( A defaultArea ){
		if( defaultArea != null ){
			if( !areas.containsKey( defaultArea.getUniqueId() ))
				throw new IllegalArgumentException( "default area must be registered, call 'add' first" );
		}
		this.defaultArea = defaultArea;
	}
	
	/**
	 * Gets the default area of this mode, can be <code>null</code>
	 * @return the default area
	 */
	public A getDefaultArea(){
		return defaultArea;
	}
	
	/**
	 * Gets the area with the specified id.
	 * @param key the name of the area
	 * @return the area or <code>null</code>
	 */
	public A get( String key ){
		return areas.get( key );
	}
	
	/**
	 * Recursively searches through all stations of <code>dockable</code>
	 * until a station is found that is registered at this mode.
	 * @param dockable the element whose root is searched
	 * @return the root or <code>null</code>, never <code>dockable</code> itself
	 */
	public A get( Dockable dockable ){
		while( dockable != null ){
			for( A area : areas.values() ){
				if( area.isChild( dockable ) ){
					return area;
				}
			}
			DockStation station = dockable.getDockParent();
			if( station == null )
				return null;
			dockable = station.asDockable();
		}
		return null;
	}
	
	/**
	 * Sets the action which must be triggered in order to activate this mode. This
	 * action will be returned by {@link #getActionsFor(Dockable, Mode)} if the mode
	 * is not <code>this</code>. Changes to this property are applied to all visible
	 * {@link Dockable}.
	 * @param selectModeAction the action or <code>null</code>
	 */
	public void setSelectModeAction( CAction selectModeAction ){
		if( this.selectModeAction != selectModeAction ){
			if( this.selectModeAction != null )
				selectModeSource.remove( this.selectModeAction.intern() );
			this.selectModeAction = selectModeAction;
			if( this.selectModeAction != null )
				selectModeSource.add( this.selectModeAction.intern() );
		}
	}
	
	/**
	 * Gets the action which must be triggered in order to activate this mode.
	 * @return the action or <code>null</code>
	 */
	public CAction getSelectModeAction(){
		return selectModeAction;
	}
	
	public DockActionSource getActionsFor( Dockable dockable, Mode<Location> mode ){
		if( mode == this )
			return null;
		return selectModeSource;
	}
}
