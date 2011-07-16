/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * An event telling whether a {@link DockTitle} should be focused or not.
 * @author Benjamin Sigg
 */
public class ActivityDockTitleEvent extends DockTitleEvent{
	 private boolean active, preferred;
	
	 /**
     * Constructs a new event.
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     * @param active <code>true</code> if <code>dockable</code> is the
     * selected and focused child, <code>false</code> otherwise
     */
	 public ActivityDockTitleEvent( Dockable dockable, boolean active ){
		 this( null, dockable, active );
	 }
	 
    /**
     * Constructs a new event. This constructor should only be called
     * if a {@link DockStation} itself sends the event. Other components
     * should use {@link #ActivityDockTitleEvent(Dockable, boolean)}.
     * @param station the station on which the target-title is displayed
     * @param dockable the {@link Dockable} for which the target-title
     * is rendered
     * @param active <code>true</code> if <code>dockable</code> is the
     * selected and focused child, <code>false</code> otherwise
     */
	 public ActivityDockTitleEvent( DockStation station, Dockable dockable, boolean active ){
		 super( station, dockable );
		 this.active = active;
	 }

    /**
     * Returns whether the target-title should be painted in a "focused"-state.
     * @return <code>true</code> if the {@link Dockable} is focused,
     * <code>false</code> otherwise.
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Tells whether the {@link Dockable} is preferred in some way by the station.
     * If so, a {@link DockTitle} can be drawn slightly different than
     * a normal title.
     * @return <code>true</code> if the {@link Dockable} is a very special
     * {@link Dockable}
     */
    public boolean isPreferred() {
        return preferred;
    }
    
    /**
     * Sets whether the {@link Dockable} is preferred.
     * @param preferred <code>true</code> if the target-title should be
     * painted in a special way
     * @see #isPreferred()
     */
    public void setPreferred( boolean preferred ) {
        this.preferred = preferred;
    }
}
