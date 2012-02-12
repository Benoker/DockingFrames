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
package bibliothek.gui.dock.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bibliothek.gui.Dockable;

/**
 * An abstract implementation of {@link ButtonContentFilter} implementing all methods except
 * the filter algorithm itself.
 * @author Benjamin Sigg
 */
public abstract class AbstractButtonContentFilter implements ButtonContentFilter{
	/** all the listener that have been added to this filter */
	private List<ButtonContentFilterListener> listeners = new ArrayList<ButtonContentFilterListener>();
	
	/** all the action that are currently installed */
	private Map<DockAction, Map<Dockable, Integer>> count = new HashMap<DockAction, Map<Dockable,Integer>>();
	
	public void addListener( ButtonContentFilterListener listener ){
		listeners.add( listener );
	}
	
	public void removeListener( ButtonContentFilterListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Calls {@link ButtonContentFilterListener#showTextChanged(ButtonContentFilter, Dockable, DockAction)} on all
	 * listeners that are currently registered
	 * @param dockable the dockable for which the result of {@link #showText(Dockable, DockAction)} changed, can be <code>null</code>
	 * @param action the action for which the result of {@link #showText(Dockable, DockAction)} changed, can be <code>null</code>
	 */
	protected void fire( Dockable dockable, DockAction action ){
		for( ButtonContentFilterListener listener : listeners.toArray( new ButtonContentFilterListener[ listeners.size() ] )){
			listener.showTextChanged( this, dockable, action );
		}
	}

	/**
	 * Called the first time <code>dockable</code> and <code>action</code> are installed.
	 * @param action the newly installed action
	 * @param dockable the newly installed dockable
	 */
	protected void installed( DockAction action, Dockable dockable ){
		// ignore		
	}
	
	/**
	 * Called the first time <code>action</code> is installed.
	 * @param action the newly installed action
	 */
	protected void installed( DockAction action ){
		// ignore
	}
	
	/**
	 * Called after <code>dockable</code> and <code>action</code> have been uninstalled the last time. 
	 * @param action the action that has been uninstalled
	 * @param dockable the dockable that has been uninstalled
	 */
	protected void uninstalled( DockAction action, Dockable dockable ){
		// ignore		
	}
	
	/**
	 * Called after <code>action</code> was uninstalled the last time.
	 * @param action the action that has been uninstalled
	 */
	protected void uninstalled( DockAction action ){
		// ignore
	}
	
	public void install( Dockable dockable, DockAction action ){
		Map<Dockable, Integer> actionCount = count.get( action );
		if( actionCount == null ){
			actionCount = new HashMap<Dockable, Integer>();
			count.put( action, actionCount );
			installed( action );
		}
		
		Integer count = actionCount.get( dockable );
		if( count == null ){
			count = 1;
			installed( action, dockable );
		}
		else{
			count = count+1;
		}
		actionCount.put( dockable, count );
	}
	
	public void uninstall( Dockable dockable, DockAction action ){
		Map<Dockable, Integer> actionCount = count.get( action );
		Integer count = actionCount.get( dockable );
		if( count.intValue() == 1 ){
			actionCount.remove( dockable );
			uninstalled( action, dockable );
		}
		else{
			actionCount.put( dockable, count-1 );
		}
		
		if( actionCount.isEmpty() ){
			this.count.remove( action );
			uninstalled( action );
		}
	}
}
