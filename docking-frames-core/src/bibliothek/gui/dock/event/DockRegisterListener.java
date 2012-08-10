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
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRegister;
import bibliothek.gui.dock.control.DockRelocator;

/**
 * A listener receiving events from a {@link bibliothek.gui.dock.control.DockRegister}.<br>
 * {@link DockRegister} can be {@link DockRegister#setStalled(boolean) stalled}
 * and some events may have a great delay when arriving. Subclasses should
 * be aware that the current state of the dock-tree, and the state one would
 * get when only monitoring with {@link DockRegisterListener}, may be different.
 * @author Benjamin Sigg
 *
 */
public interface DockRegisterListener {
	/**
	 * Called if the {@link DockRegister} from <code>controller</code> has been
	 * {@link DockRegister#isStalled() stalled}.
	 * @param controller the controller whose register is stalled
	 */
	public void registerStalled( DockController controller );
	
	/**
	 * Called if the {@link DockRegister} from <code>controller</code> is no
	 * longer {@link DockRegister#isStalled() stalled}, this method is called
	 * before the pending events are fired.
	 * @param controller the controller whose register is no longer stalled
	 */
	public void registerUnstalled( DockController controller );
	
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
     * {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller where <code>dockable</code> was added
     * @param dockable the {@link Dockable} that was added
     */
    public void dockableRegistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after a {@link DockStation} was registered. This method can
     * be called while a {@link Dockable} is dragged. See the method
     * {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller where <code>station</code> was added
     * @param station the {@link DockStation} that was added
     */
    public void dockStationRegistered( DockController controller, DockStation station );
        
    /**
     * Invoked after <code>dockable</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller from where <code>dockable</code> was removed 
     * @param dockable the removed {@link Dockable}
     */
    public void dockableUnregistered( DockController controller, Dockable dockable );
    
    /**
     * Invoked after <code>station</code> has been unregistered from <code>controller</code>.
     * Note that this method can be invoked while a {@link Dockable} is dragged,
     * use the method {@link DockController}.{@link DockRelocator#isOnMove() isOnMove()}.
     * @param controller the controller from where <code>dockable</code> was removed 
     * @param station the removed {@link DockStation}
     */
    public void dockStationUnregistered( DockController controller, DockStation station );
    
    /**
     * Invoked when <code>dockable</code> was added and removed from the <code>controller</code>, or
     * was removed and added again to <code>controller</code>. This method is only
     * invoked if a call to {@link #dockableRegistered(DockController, Dockable)} and
     * {@link #dockableUnregistered(DockController, Dockable)} was suppressed. It
     * is unknown whether <code>dockable</code> is now registered at <code>controller</code>.
     * @param controller the controller whose register <code>dockable</code> cycled
     * @param dockable some {@link Dockable}
     */
    public void dockableCycledRegister( DockController controller, Dockable dockable );
}
