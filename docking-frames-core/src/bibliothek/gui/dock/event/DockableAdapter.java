/**
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

import javax.swing.Icon;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * An abstract class implementing the {@link DockableListener}. All
 * methods in this class are empty. The class can be used instead of
 * {@link DockableListener}, and only a few selected methods have to
 * be implemented again.
 * @author Benjamin Sigg
 *
 */
public abstract class DockableAdapter implements DockableListener {
    public void titleBound( Dockable dockable, DockTitle title ) {
        // do nothing
    }

    public void titleUnbound( Dockable dockable, DockTitle title ) {
        // do nothing
    }

    public void titleTextChanged( Dockable dockable, String oldTitle,
            String newTitle ) {
        // do nothing
    }

    public void titleToolTipChanged( Dockable dockable, String oldTooltip,
            String newTooltip ) {
        // do nothing
    }
    
    public void titleIconChanged( Dockable dockable, Icon oldIcon, Icon newIcon ) {
        // do nothing
    }
    
    public void titleExchanged( Dockable dockable, DockTitle title ) {
        // do nothing
    }
}
