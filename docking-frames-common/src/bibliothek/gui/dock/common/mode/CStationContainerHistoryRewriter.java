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
package bibliothek.gui.dock.common.mode;

import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CStation;
import bibliothek.gui.dock.common.CStationContainer;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.intern.CommonDockable;
import bibliothek.gui.dock.common.intern.station.CommonDockStation;
import bibliothek.gui.dock.common.util.CDockUtilities;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.support.mode.HistoryRewriter;

/**
 * The {@link CStationContainerHistoryRewriter} tries to ensure that a {@link CDockable} stays on the
 * same {@link CStationContainer} when changing the {@link ExtendedMode}.
 * @author Benjamin Sigg
 */
public class CStationContainerHistoryRewriter implements HistoryRewriter<Location, CLocationMode>{
	/** provides the list of {@link CStationContainer}s to check */
	private CControl control;
	
	/**
	 * Creates a new rewriter
	 * @param control provides the list of {@link CStationContainer}s to check
	 */
	public CStationContainerHistoryRewriter( CControl control ){
		this.control = control;
	}
	
	/**
	 * Gets the {@link CControl} that is using this rewriter.
	 * @return the control
	 */
	public CControl getControl(){
		return control;
	}
	
	/**
	 * Given the current location of <code>dockable</code> and the root station it is going
	 * to use, this method decides which {@link CStation} is actually going to be the new parent
	 * root station.
	 * @param dockable the element that is going to be moved around
	 * @param root the new root station
	 * @return a replacement for <code>root</code>, a value of <code>null</code> means that
	 * <code>root</code> should be used.
	 */
	protected CStation<?> getMatchingStation( Dockable dockable, String root ){
		// search root
		CStation<?> rootStation = control.getStation( root );
		if( rootStation == null ){
			return null;
		}
		CStationContainer rootContainer = getContainer( rootStation );
		if( rootContainer == null ){
			return rootStation;
		}
		
		// search current container
		CStation<?> station = getParent( dockable );
		CStationContainer container = getContainer( station );
		
		if( container != null ){
			if( rootContainer == container ){
				return rootStation;
			}
			return container.getMatchingStation( rootContainer, rootStation );
		}
		else{
			List<Location> history = control.getLocationManager().getPropertyHistory( dockable );
			for( int i = history.size()-1; i >= 0; i-- ){
				String historyRoot = history.get( i ).getRoot();
				CStation<?> historyStation = control.getStation( historyRoot );
				if( historyStation != null ){
					container = getContainer( historyStation );
					if( container != null ){
						CStation<?> result = container.getMatchingStation( rootContainer, rootStation );
						if( result != null ){
							return result;
						}
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Searches a {@link CStation} which could be the new parent of <code>dockable</code>
	 * if it should be in mode <code>mode</code>.
	 * @param dockable the element for which a new parent is searched
	 * @param mode the mode <code>element</code> should be in
	 * @return the new parent or <code>null</code>
	 */
	protected CStation<?> getMatchingStation( Dockable dockable, ExtendedMode mode ){
		CStation<?> station = getParent( dockable );
		CStation<?> workingArea = getWorkingArea( dockable );
		
		if( station != null && workingArea != null && !isValidParent( station, workingArea )){
			return workingArea;
		}
		
		CStationContainer container = getContainer( station );
		if( container != null ){
			return container.getDefaultStation( mode );
		}
		
		List<Location> history = control.getLocationManager().getPropertyHistory( dockable );
		for( int i = history.size()-1; i >= 0; i-- ){
			String historyRoot = history.get( i ).getRoot();
			CStation<?> rootStation = control.getStation( historyRoot );
			if( rootStation != null ){
				container = getContainer( rootStation );
				if( container != null ){
					CStation<?> result = container.getDefaultStation( mode );
					if( result != null && isValidParent( result, workingArea )){
						return result;
					}
				}
			}
		}
		
		return null;
	}
	
	private boolean isValidParent( CStation<?> parent, CStation<?> workingArea ){
		return parent != null && CDockUtilities.getFirstWorkingArea( parent ) == workingArea;
	}
	
	/**
	 * Gets the {@link CStation} which is set as {@link CDockable#getWorkingArea()}
	 * for <code>dockable</code>.
	 * @param dockable the element whose working area is searched
	 * @return the working area or <code>null</code>
	 */
	protected CStation<?> getWorkingArea( Dockable dockable ){
		if( dockable instanceof CommonDockable ){
			return ((CommonDockable)dockable).getDockable().getWorkingArea();
		}
		return null;
	}
	
	/**
	 * Gets the first parent {@link CStation} of <code>dockable</code>.
	 * @param dockable some dockable whose parent station is searched
	 * @return the parent station or <code>null</code> if not found
	 */
	protected CStation<?> getParent( Dockable dockable ){
		DockStation dockStation = dockable.getDockParent();
		
		while( dockStation != null ){
			if( dockStation instanceof CommonDockStation<?,?>){
				return ((CommonDockStation<?,?>)dockStation).getStation();
			}
			if( dockStation.asDockable() == null ){
				dockStation = null;
			}
			else{
				dockStation = dockStation.asDockable().getDockParent();
			}
		}
		
		return null;
	}
	
	/**
	 * Searches the {@link CStationContainer} which is the parent of <code>child</code>.
	 * @param child some {@link CStation} whose parent is searched
	 * @return the parent of <code>child</code> or <code>null</code>
	 */
	protected CStationContainer getContainer( CStation<?> child ){
		if( child == null ){
			return null;
		}
		DockStation station = child.getStation();
		
		while( station != null ){
			if( station instanceof CommonDockStation<?,?>){
				CStation<?> next = ((CommonDockStation<?,?>)station).getStation();
				CStationContainer container = control.getRegister().getContainer( next );
				if( container != null ){
					return container;
				}
			}
			
			Dockable dockable = station.asDockable();
			if( dockable == null ){
				return null;
			}
			station = dockable.getDockParent();
			if( station == null ){
				return null;
			}			
		}
		
		return null;
	}
	
	public Location rewrite( Dockable dockable, CLocationMode mode, Location history ){
		CStation<?> replacement = null;
		
		if( history != null ){
			replacement = getMatchingStation( dockable, history.getRoot() );
		}
		
		if( replacement == null ){
			replacement = getMatchingStation( dockable, mode.getExtendedMode() );
		}
		
		if( replacement == null ){
			return history;
		}
		else{
			if( history == null ){
				return new Location( mode.getUniqueIdentifier(), replacement.getUniqueId(), null, false );
			}
			else{
				return new Location( mode.getUniqueIdentifier(), replacement.getUniqueId(), history.getLocation(), history.isApplicationDefined() );
			}
		}
	}
}
