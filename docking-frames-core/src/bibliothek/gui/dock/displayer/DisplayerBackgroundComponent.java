/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.displayer;

import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A {@link BackgroundComponent} represents a {@link DockableDisplayer}.
 * @author Benjamin Sigg
 */
public interface DisplayerBackgroundComponent extends BackgroundComponent{
	/** the kind of {@link UIValue} this is */
	public static final Path KIND = BackgroundComponent.KIND.append( "displayer" );
	
	/**
	 * Gets the displayer which is represented by this component.
	 * @return the displayer, not <code>null</code>
	 */
	public DockableDisplayer getDisplayer();
}
