/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import java.util.ArrayList;
import java.util.List;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockAction;

/**
 * This default implementation of {@link EclipseThemeConnector} draws the typical eclipse-header over {@link Dockable}s
 * and neither border nor title over {@link DockStation}s. It checks each {@link DockAction} for
 * the annotation {@link EclipseTabDockAction} and the actions with the annotation are painted 
 * on tabs.
 */
public class DefaultEclipseThemeConnector implements EclipseThemeConnector {
	private List<EclipseThemeConnectorListener> listeners = new ArrayList<EclipseThemeConnectorListener>();
	
	public void addEclipseThemeConnectorListener( EclipseThemeConnectorListener listener ){
		listeners.add( listener );
	}
	
	public void removeEclipseThemeConnectorListener( EclipseThemeConnectorListener listener ){
		listeners.remove( listener );
	}
	
	/**
	 * Gets all listeners that are currently registered at this connector.
	 * @return all listeners
	 */
	protected EclipseThemeConnectorListener[] listeners(){
		return listeners.toArray( new EclipseThemeConnectorListener[ listeners.size() ] );
	}
	
	/**
	 * Tells whether <code>this</code> has any registered listeners.
	 * @return <code>true</code> if there is at least one listener registered.
	 */
	protected boolean hasListeners(){
		return !listeners.isEmpty();
	}
	
	public TitleBar getTitleBarKind( DockStation parent, Dockable dockable ){
    	if( parent instanceof StackDockStation )
    		return TitleBar.NONE;
    	
        if( dockable.asDockStation() == null )
            return TitleBar.ECLIPSE;
        
        return TitleBar.NONE_HINTED;
    }
    
	public boolean shouldShowOnSide( DockAction action, EclipseTabStateInfo tab ){
		return getLocation( action, tab ).isSide();
	}
	
	public boolean shouldShowOnTab( DockAction action, EclipseTabStateInfo tab ){
		return getLocation( action, tab ).isTab();
	}
	
	/**
	 * Gets the location of <code>action</code>.
	 * @param action the action whose location is searched
	 * @param tab the state of a tab
	 * @return the location of <code>action</code>, not <code>null</code>
	 */
	protected EclipseTabDockActionLocation getLocation( DockAction action, EclipseTabStateInfo tab ){
		EclipseTabDockAction annotation = action.getClass().getAnnotation( EclipseTabDockAction.class );
		return getLocation( annotation, tab );
	}
	
	/**
	 * Gets the location encoded in <code>annotation</code>.
	 * @param annotation an annotation, can be <code>null</code>
	 * @param tab the state of a tab
	 * @return the location a {@link DockAction} should have according to <code>annotation</code>
	 */
	protected EclipseTabDockActionLocation getLocation( EclipseTabDockAction annotation, EclipseTabStateInfo tab ){
		if( annotation == null ){
			return EclipseTabDockActionLocation.SIDE;
		}
		if( tab.isFocused() ){
			return annotation.focused();
		}
		if( tab.isSelected() ){
			return annotation.selected();
		}
		return annotation.normal();
	}
}