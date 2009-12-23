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
import bibliothek.gui.dock.common.action.predefined.CNormalizeAction;
import bibliothek.gui.dock.common.intern.CDockable;

/**
 * {@link CDockable}s are in {@link NormalMode} if they are a child
 * of a {@link NormalModeArea}.
 * @author Benjamin Sigg
 *
 */
public class NormalMode extends DefaultLocationMode<NormalModeArea>{
	/** The unique identifier of this mode */
	public static final Path IDENTIFIER = new Path( "dock.mode.normal" );
	
	/**
	 * Creates a new normal mode.
	 * @param control the owner of this mode
	 * @param manager responsible for this mode
	 */
	public NormalMode( CControl control, ExtendedModeManager manager ){
		super( manager );
		setSelectModeAction( new CNormalizeAction( control ) );
	}
	
	public Path getUniqueIdentifier(){
		return IDENTIFIER;
	}

	public boolean isCurrentMode( Dockable dockable ){
		for( NormalModeArea area : this ){
			if( area.isNormalModeChild( dockable )){
				return true;
			}
		}
		
		return false;
	}

	public boolean isDefaultMode( Dockable dockable ){
		return true;
	}
}
