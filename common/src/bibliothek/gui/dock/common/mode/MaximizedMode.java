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

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.action.predefined.CMaximizeAction;

/**
 * {@link Dockable}s are maximized if they take up the whole space a frame
 * or a screen offers.
 * @author Benjamin Sigg
 */
public class MaximizedMode extends AbstractLocationMode<MaximizedModeArea>{
	/** unique identifier for this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.maximized" );
	
	/**
	 * Creates a new mode
	 * @param control the control in whose realm this mode will work
	 * @param manager the manager which manages this mode
	 */
	public MaximizedMode( CControl control, ExtendedModeManager manager ){
		super( manager );
		setSelectModeAction( new CMaximizeAction( control ) );
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public void apply( Dockable dockable, Location history ){
		MaximizedModeArea area = null;
		if( history != null )
			area = get( history.getRoot() );
		if( area == null )
			area = getDefaultArea();
		
		area.setMaximized( dockable );
	}

	public Location leave( Dockable dockable ){
		MaximizedModeArea area = get( dockable );
		if( area == null )
			return null;
	
		area.setMaximized( null );
		return new Location( area.getUniqueId(), null );
	}

	public boolean isCurrentMode( Dockable dockable ){
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDefaultMode( Dockable dockable ){
		return false;
	}	
}
