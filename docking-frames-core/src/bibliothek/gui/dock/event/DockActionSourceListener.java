/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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

package bibliothek.gui.dock.event;

import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;

/**
 * A listener that is added to {@link DockActionSource}. The listener 
 * receives events whenever {@link DockAction DockActions} are added
 * or removed from the source.
 * @author Benjamin Sigg
 */
public interface DockActionSourceListener {
    /**
     * Invoked when one or more actions are added to the <code>source</code>.
     * @param source the origin of the event
     * @param firstIndex the index of the first new action
     * @param lastIndex the index of the last new action. This value 
     * must be greater or equal to <code>firstIndex</code>.
     */
    public void actionsAdded( DockActionSource source, int firstIndex, int lastIndex );
    
    /**
     * Invoked if one or more actions are removed from the <code>source</code>.
     * @param source the origin of the event.
     * @param firstIndex the index of the first action that was removed
     * @param lastIndex the index of the last action that was removed. This
     * argument is greater or equal to <code>firstIndex</code>.
     */
    public void actionsRemoved( DockActionSource source, int firstIndex, int lastIndex );
}
