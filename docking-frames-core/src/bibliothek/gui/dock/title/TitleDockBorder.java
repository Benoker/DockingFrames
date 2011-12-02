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
package bibliothek.gui.dock.title;

import bibliothek.gui.dock.themes.border.BorderModifier;
import bibliothek.gui.dock.themes.border.DockBorder;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * A {@link BorderModifier} used by a {@link DockTitle}.
 * @author Benjamin Sigg
 */
public interface TitleDockBorder extends DockBorder{
	/** the type of this {@link UIValue} */
	public static final Path KIND = DockBorder.KIND.append( "title" );
	
	/**
	 * Gets the title for which this border is used.
	 * @return the title
	 */
	public DockTitle getTitle();
}
