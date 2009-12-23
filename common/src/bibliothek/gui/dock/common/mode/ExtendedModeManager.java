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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.support.mode.ModeManager;

/**
 * {@link ModeManager} for setting the {@link CDockable.ExtendedMode} of a {@link CDockable}.
 * @author Benjamin Sigg
 */
public class ExtendedModeManager extends ModeManager<Location, LocationMode>{
	private NormalMode normalMode;
	private MaximizedMode maximizedMode;
	private MinimizedMode minimizedMode;
	private ExternalizedMode externalizedMode;
	
	/**
	 * Creates a new manager.
	 * @param control the control in whose realm this manager will work
	 */
	public ExtendedModeManager( CControl control ){
		super( control );
		
		normalMode = new NormalMode( control, this );
		maximizedMode = new MaximizedMode( control, this );
		minimizedMode = new MinimizedMode( control, this );
		externalizedMode = new ExternalizedMode( control, this );
		
		putMode( normalMode );
		putMode( maximizedMode );
		putMode( minimizedMode );
		putMode( externalizedMode );
	}
	
	/**
	 * Direct access to the mode handling "normal" {@link Dockable}s.
	 * @return the mode
	 */
	public NormalMode getNormalMode(){
		return normalMode;
	}

	/**
	 * Direct access to the mode handling "maximized" {@link Dockable}s.
	 * @return the mode
	 */
	public MaximizedMode getMaximizedMode(){
		return maximizedMode;
	}
	
	/**
	 * Direct access to the mode handling "minimized" {@link Dockable}s.
	 * @return the mode
	 */
	public MinimizedMode getMinimizedMode(){
		return minimizedMode;
	}
	
	/**
	 * Direct access to the mode handling "externalized" {@link Dockable}s.
	 * @return the mode
	 */
	public ExternalizedMode getExternalizedMode(){
		return externalizedMode;
	}
	
	@Override
	protected LocationMode getCurrentMode( Dockable dockable ){
		while( dockable != null ){
			for( LocationMode mode : modes() ){
				if( mode.isCurrentMode( dockable ))
					return mode;
			}
			DockStation station = dockable.getDockParent();
			dockable = station == null ? null : station.asDockable();
		}
		
		return null;
	}
}
