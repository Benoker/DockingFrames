/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.station.split;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.Node;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.util.Todo;
import bibliothek.util.Todo.Compatibility;
import bibliothek.util.Todo.Priority;
import bibliothek.util.Todo.Version;

/**
 * This manager knows the preferred size of some {@link Dockable}s, compared
 * to the size of their parent node in a {@link SplitDockStation}.
 * 
 * @author Parag Shah
 * @author Benjamin Sigg
 * 
 * @deprecated Due to the new placeholder mechanism this class/interface has become obsolete, it is no longer used
 * anywhere. Clients should now use a {@link PlaceholderStrategy} to assign identifiers to the {@link Dockable}s, with
 * these identifiers the location and size of a {@link Dockable} is stored in a much more consistent way than using the
 * {@link LbSplitLayoutManager}. This class/interface will be removed in a future release.
 */
@Deprecated
@Todo(compatibility=Compatibility.BREAK_MINOR, priority=Priority.ENHANCEMENT, target=Version.VERSION_1_1_1,
		description="remove this class")
public interface SizeManager{
    /**
     * Gets the size that <code>dockable</code> should have compared to the
     * size of its parent {@link Node} in a {@link SplitDockStation}. The result 
     * of this method is multiplied with the size of the node in order to get
     * the final size of <code>dockable</code>.
     * @param dockable some element whose size is requested
     * @return a value between 0 and 1, or below 0 to indicate that
     * no size is available
     */
    public double getSize( Dockable dockable );
}
