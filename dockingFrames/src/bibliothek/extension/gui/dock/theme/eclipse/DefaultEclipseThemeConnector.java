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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.action.DockAction;

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
	
	public TitleBar getTitleBarKind( Dockable dockable ) {
    	if( dockable.getDockParent() instanceof StackDockStation )
    		return TitleBar.NONE;
    	
        if( dockable.asDockStation() == null )
            return TitleBar.ECLIPSE;
        
        return TitleBar.NONE_HINTED;
    }
    
	public boolean isTabAction( Dockable dockable, DockAction action ){
		EclipseTabDockAction tab = action.getClass().getAnnotation( EclipseTabDockAction.class );
		return tab != null;
	}
}