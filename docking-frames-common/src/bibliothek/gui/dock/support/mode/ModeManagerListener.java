/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.support.mode;

import bibliothek.gui.Dockable;

/**
 * This observer is added to a {@link ModeManager} and is informed when
 * {@link Mode}s or {@link Dockable}s change.
 * @author Benjamin Sigg
 * @param <A> the kind of properties used by a {@link ModeManager}
 * @param <M> the kind of {@link Mode}s used by a {@link ModeManager}
 */
public interface ModeManagerListener<A, M extends Mode<A>> {
	/**
	 * Called when a {@link Dockable} is added or replaced in {@code manager}.
	 * If it was replaced, then {@link #dockableRemoved(ModeManager, Dockable)} is
	 * called first.
	 * @param manager the source of the event 
	 * @param dockable the new element
	 */
	public void dockableAdded( ModeManager<? extends A, ? extends M> manager, Dockable dockable );
	
	/**
	 * Called when a {@link Dockable} is removed or replaced from {@code manager}.
	 * If it was replaced, then {@link #dockableAdded(ModeManager, Dockable)} is
	 * called afterwards.
	 * @param manager the source of the event
	 * @param dockable the new element
	 */
	public void dockableRemoved( ModeManager<? extends A, ? extends M> manager, Dockable dockable );
	
	/**
	 * Called if the mode of {@code dockable} changed. Note: this method may be
	 * called in rapid succession. Some mode-changes may not be reported, for
	 * example if a {@link Dockable} changes {@code a -> b -> c} then the
	 * event can be {@code a -> c}.
	 * @param manager the source of the event
	 * @param dockable the affected element
	 * @param oldMode the old mode, may be {@code null}
	 * @param newMode the new mode, may be {@code null}
	 */
	public void modeChanged( ModeManager<? extends A, ? extends M> manager, Dockable dockable, M oldMode, M newMode );
	
	/**
	 * Called when a new mode has been added to {@code manager}.
	 * @param manager the source of the event
	 * @param mode the new mode
	 */
	public void modeAdded( ModeManager<? extends A, ? extends M> manager, M mode );
	
	/**
	 * Called when a mode has been removed from {@code manager}.
	 * @param manager the source of the event
	 * @param mode the removed mode
	 */
	public void modeRemoved( ModeManager<? extends A, ? extends M> manager, M mode );
}
