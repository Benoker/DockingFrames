/**
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

package bibliothek.gui.dock.facile.action;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.util.ClientOnly;

/**
 * An {@link ActionGuard} that adds a {@link ReplaceAction} to all elements
 * that are {@link Dockable} and {@link DockStation} at the same time.
 * @author Benjamin Sigg
 */
@ClientOnly
public class ReplaceActionGuard implements ActionGuard {
	/** The source that is added to dockables */
	private DefaultDockActionSource source;
	/** The action used to replace stations */
	private ReplaceAction action;
	
	/**
	 * Creates a new guard
     * @param controller The controller for which actions are created.
	 */
	public ReplaceActionGuard( DockController controller ){
        if( controller == null )
            throw new IllegalArgumentException( "Controller should not be null" );
        
		action = new ReplaceAction( controller );
		source = new DefaultDockActionSource();
		
		source.setHint( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.LEFT ));
		setVisible( true );
	}
	
	/**
	 * Sets the visibility of the action. The visibility can be changed at any
	 * time and has effect on all occurrences of the action.
	 * @param visible the new state
	 */
	public void setVisible( boolean visible ){
		if( visible != isVisible() ){
			if( visible )
				source.add( action );
			else
				source.remove( 0, source.getDockActionCount() );
		}
	}
	
	/**
	 * Tells whether the action of this guard can be seen or not
	 * @return <code>true</code> if the action can be seen
	 */
	public boolean isVisible(){
		return source.getDockActionCount() > 0;
	}
	
	public DockActionSource getSource( Dockable dockable ){
		return source;
	}

	public boolean react( Dockable dockable ){
		return dockable.asDockStation() != null;
	}
}
