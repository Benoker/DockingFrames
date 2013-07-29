/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.common.intern.station;

import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.util.CDockUtilities;
import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.screen.window.ScreenDockWindowClosingStrategy;

/**
 * This strategy searches for a {@link CDockable} when the user wants to close an externalized dockable. It calls
 * {@link CDockable#setExtendedMode(ExtendedMode)} with {@link ExtendedMode#NORMALIZED}.
 * @author Benjamin Sigg
 */
public class CScreenDockStationWindowClosingStrategy implements ScreenDockWindowClosingStrategy{
	public void closing( ScreenDockWindow window ){
		CDockable dockable = CDockUtilities.getFirstDockable( window.getDockable() );
		if( dockable != null ){
			if( dockable.isNormalizeable() ){
				dockable.setExtendedMode( ExtendedMode.NORMALIZED );
			}
		}
	}
}
