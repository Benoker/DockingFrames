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

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A listener added to a {@link DockController}, receives notifications when
 * a {@link DockTitle} is bound or unbound.
 * @author Benjamin Sigg
 */
public interface DockTitleBindingListener {
    /**
     * Called when <code>title</code> was {@link Dockable#bind(DockTitle) bound}
     * to <code>dockable</code>.
     * This method is called after the {@link DockTitle#bind()}-method
     * was invoked.
     * @param controller the origin of the event
     * @param title the {@link DockTitle} that was bound
     * @param dockable the owner of <code>title</code>
     */
    public void titleBound( DockController controller, DockTitle title, Dockable dockable );
    
    /**
     * Called when <code>title</code> was {@link Dockable#unbind(DockTitle) unbound}
     * from <code>dockable</code>.
     * This method is called after the {@link DockTitle#bind()}-method
     * was invoked.
     * @param controller the origin of the event
     * @param title the {@link DockTitle} which was unbound
     * @param dockable the old owner of <code>title</code>
     */
    public void titleUnbound( DockController controller, DockTitle title, Dockable dockable );
}
