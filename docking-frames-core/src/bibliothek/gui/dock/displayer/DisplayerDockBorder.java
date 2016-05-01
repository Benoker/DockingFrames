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
import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.themes.border.DockBorder;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * An {@link UIValue} observing the {@link BorderModifier} that is used for
 * a {@link DockableDisplayer}.
 * @author Benjamin Sigg
 */
public interface DisplayerDockBorder extends DockBorder {
	/** The type of this {@link UIValue} */
	public static final Path KIND = DockBorder.KIND.append( "displayer" );
	
	/**
	 * Gets the displayer for which this border is used.
	 * @return the displayer, not <code>null</code>
	 */
	public DockableDisplayer getDisplayer();
}
