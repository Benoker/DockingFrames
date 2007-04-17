/**
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * 
 * Wunderklingerstr. 59
 * 8215 Hallau
 * CH - Switzerland
 */


package bibliothek.gui.dock.event;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DockStation;
import bibliothek.gui.dock.Dockable;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This listener is added to the {@link DockController}. The listener receives
 * event when some {@link Dockable} or {@link DockStation} is added or removed
 * from the controller. The listener is also informed about changes of the
 * focus, about {@link DockTitle DockTitles} and their bindings, and which
 * {@link Dockable} is moved.
 * @author Benjamin Sigg
 */
public interface DockControllerListener {
    
    /**
     * Invoked right before the <code>dockable</code> is registered in the
     * <code>controller</code>.
     * @param controller the controller who does not yet know <code>dockable</code>.
     * @param dockable the dockable who does not yet know <code>controller</code>.
     */
    public void dockableRegistering( DockController controller, Dockable dockable );
    
    /**
     * Invoked right before the <code>station</code> is registered in
     * the <code>controller</code>.
     * @param controller the controller who does not yet know <code>station</code>.
     * @param station the station who does not yet know <code>controller</code>.
     */
    public void dockStationRegistering( DockController controller, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was registered. Note that this method can 
     * be called while a {@link Dockable} is dragged. See the method
     * {@link DockController}.{@link DockController#isOnMove() isOnMove()}.
     * @param controller the controller where <code>dockable</code> was added
     * @param dockable the {@link Dockable} that was added
     */
    public void dockableRegistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after a {@link DockStation} was registered. This method can
     * be called while a {@link Dockable} is dragged. See the method
     * {@link DockController}.{@link DockController#isOnMove() isOnMove()}.
     * @param controller the controller where <code>station</code> was added
     * @param station the {@link DockStation} that was added
     */
    public void dockStationRegistered( DockController controller, DockStation station );
        
    /**
     * Invoked after <code>dockable</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockController#isOnMove() isOnMove()}.
     * @param controller the controller from whom <code>dockable</code> was removed 
     * @param dockable the removed {@link Dockable}
     */
    public void dockableUnregistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after <code>station</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockController#isOnMove() isOnMove()}.
     * @param controller the controller from whom <code>dockable</code> was removed 
     * @param station the removed {@link DockStation}
     */
    public void dockStationUnregistered( DockController controller, DockStation station );
 
    /**
     * Invoked before a {@link Dockable} is moved around. This method is called
     * after the user has released the mouse which was dragging a {@link DockTitle}
     * around.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the future parent of <code>dockable</code>
     * @see #dockablePut(DockController, Dockable, DockStation)
     */
    public void dockableDrag( DockController controller, Dockable dockable, DockStation station );
    
    /**
     * Invoked after a {@link Dockable} was moved.
     * @param controller the origin of the event
     * @param dockable the {@link Dockable} which was dragged
     * @param station the new parent of <code>dockable</code>
     * @see #dockableDrag(DockController, Dockable, DockStation)
     */
    public void dockablePut( DockController controller, Dockable dockable, DockStation station );
    
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
