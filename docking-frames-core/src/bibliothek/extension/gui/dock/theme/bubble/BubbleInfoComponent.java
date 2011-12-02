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
package bibliothek.extension.gui.dock.theme.bubble;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.station.stack.DockActionCombinedInfoComponent;
import bibliothek.gui.dock.station.stack.action.DockActionDistributor.Target;
import bibliothek.gui.dock.station.stack.action.DockActionDistributorSource;

/**
 * This component shows a number of {@link DockAction}s and is used by the {@link BubbleStackDockComponent}
 * to be shown alongside the tabs.
 * @author Benjamin Sigg
 */
public class BubbleInfoComponent extends DockActionCombinedInfoComponent{
	public BubbleInfoComponent( BubbleStackDockComponent pane ){
		super( pane );
	}

	@Override
	protected DockActionSource createActionSource( Dockable dockable ){
		return new DockActionDistributorSource( Target.INFO_COMPONENT, BubbleTheme.ACTION_DISTRIBUTOR, dockable );
	}
}
