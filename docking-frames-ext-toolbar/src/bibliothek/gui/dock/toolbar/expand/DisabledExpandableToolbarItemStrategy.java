/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */
package bibliothek.gui.dock.toolbar.expand;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;

/**
 * This implementation of {@link ExpandableToolbarItemStrategy} does not do anything, it just disables
 * any feature related to expandable items.
 * @author Benjamin Sigg
 */
public class DisabledExpandableToolbarItemStrategy implements ExpandableToolbarItemStrategy{
	@Override
	public void install( DockController controller ){
		// ignore
	}

	@Override
	public void uninstall( DockController controller ){
		// ignore		
	}

	@Override
	public boolean isEnabled( Dockable item, ExpandedState state ){
		return false;
	}

	@Override
	public ExpandedState getState( Dockable item ){
		return null;
	}

	@Override
	public void setState( Dockable item, ExpandedState state ){
		// ignore		
	}

	@Override
	public void addExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		// ignore
	}

	@Override
	public void removeExpandedListener( ExpandableToolbarItemStrategyListener listener ){
		// ignore
	}
}
