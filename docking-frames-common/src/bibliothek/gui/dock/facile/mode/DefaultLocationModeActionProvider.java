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
package bibliothek.gui.dock.facile.mode;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.support.mode.Mode;

/**
 * This default implementation always returns the same {@link DockActionSource} to
 * all {@link Dockable}s. This source contains an exchangeable {@link DockAction}.
 * @author Benjamin Sigg
 *
 */
public class DefaultLocationModeActionProvider implements LocationModeActionProvider{

	/** a source containing the current {@link #selectModeAction} */
	private DefaultDockActionSource selectModeSource = new DefaultDockActionSource( new LocationHint( LocationHint.RIGHT ) );
	
	/** the default action to activate this mode */
	private DockAction selectModeAction;
	
	/**
	 * Creates a new empty provider
	 */
	public DefaultLocationModeActionProvider(){
		// nothing
	}

	/**
	 * Creates a new provider.
	 * @param action the action of this provider, may be <code>null</code>
	 * @see #setSelectModeAction(CAction)
	 */
	public DefaultLocationModeActionProvider( CAction action ){
		setSelectModeAction( action );
	}
	
	/**
	 * Creates a new provider.
	 * @param action the action of this provider, may be <code>null</code>
	 * @see #setSelectModeAction(DockAction)
	 */
	public DefaultLocationModeActionProvider( DockAction action ){
		setSelectModeAction( action );
	}
	
	/**
	 * Calls {@link #setSelectModeAction(DockAction)}.
	 * @param action the new action or <code>null</code>
	 * @see #setSelectModeAction(DockAction)
	 */
	public void setSelectModeAction( CAction action ){
		setSelectModeAction( action == null ? null : action.intern() );
	}
	
	/**
	 * Sets the action which must be triggered in order to activate this mode. This
	 * action will be returned by {@link #getActions(Dockable, Mode, DockActionSource)} if the mode
	 * is not <code>this</code>. Changes to this property are applied to all visible
	 * {@link Dockable}.
	 * @param selectModeAction the action or <code>null</code>
	 */
	public void setSelectModeAction( DockAction selectModeAction ){
		if( this.selectModeAction != selectModeAction ){
			if( this.selectModeAction != null )
				selectModeSource.remove( this.selectModeAction );
			this.selectModeAction = selectModeAction;
			if( this.selectModeAction != null )
				selectModeSource.add( this.selectModeAction );
		}
	}
	
	/**
	 * Gets the action which must be triggered in order to activate this mode.
	 * @return the action or <code>null</code>
	 */
	public DockAction getSelectModeAction(){
		return selectModeAction;
	}
	
	public DockActionSource getActions( Dockable dockable, Mode<Location> mode, DockActionSource source ){
		return selectModeSource;
	}
	
	public void destroy( Dockable dockable, DockActionSource source ){
		// nothing to do	
	}
}
