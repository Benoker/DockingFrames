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

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This listener is added to the {@link DockController}. The listener receives
 * event when some {@link Dockable} or {@link DockStation} is added or removed
 * from the controller. The listener is also informed about changes of the
 * focus, about {@link DockTitle DockTitles} and their bindings, and which
 * {@link Dockable} is moved.
 * @author Benjamin Sigg
 */
public interface DockControllerListener extends DockRegisterListener, DockRelocatorListener{
    /**
     * Called when <code>title</code> was {@link Dockable#bind(DockTitle) binded}
     * to <code>dockable</code>.
     * This method is called after the {@link DockTitle#bind()}-method
     * was invoked.
     * @param controller the origin of the event
     * @param title the {@link DockTitle} that was binded
     * @param dockable the owner of <code>title</code>
     */
    public void titleBinded( DockController controller, DockTitle title, Dockable dockable );
    
    /**
     * Called when <code>title</code> was {@link Dockable#unbind(DockTitle) unbinded}
     * from <code>dockable</code>.
     * This method is called after the {@link DockTitle#bind()}-method
     * was invoked.
     * @param controller the origin of the event
     * @param title the {@link DockTitle} which was unbinded
     * @param dockable the old owner of <code>title</code>
     */
    public void titleUnbinded( DockController controller, DockTitle title, Dockable dockable );
    
    /**
     * Invoked when <code>dockable</code> has gained the focus. 
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which is now focused
     */
    public void dockableFocused( DockController controller, Dockable dockable );
}
