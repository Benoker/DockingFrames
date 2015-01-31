/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.common.grouping;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.common.mode.CLocationMode;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.event.DockHierarchyListener;
import bibliothek.gui.dock.facile.mode.Location;

/**
 * A {@link DockableGrouping} is an algorithm that rewrites the location of a {@link Dockable},
 * allowing clients to group several dockables together.<br>
 * While the name of the interface implies that dockables should be grouped together, nothing
 * prevents a client from writing an implementation that does the opposite, e.g. tries to distribute
 * {@link Dockable}s.<br>
 * The two most common events that may be interesting for a {@link DockableGrouping}, when a 
 * {@link #hierarchyChanged(Dockable) the path of a dockable changes}, and
 * {@link #focusGained(Dockable) when a dockable gains focus}, are automatically forwarded to this interface.
 * Strategies that require more events, have to add the observers they need themselves to the right objects.
 * @author Benjamin Sigg
 */
public interface DockableGrouping {
	/**
	 * Tries to find out how to display <code>dockable</code> initially.
	 * @param dockable an invisible dockable that is about to be made visible, and that does not have a location defined.
	 * @return the initial mode of <code>dockable</code>, or <code>null</code>
	 */
	public ExtendedMode getInitialMode( Dockable dockable );
	
	/**
	 * Called right after an old location of <code>dockable</code> for <code>mode</code> was read. The method
	 * may change the location of <code>dockable</code> by returning a new {@link Location} object.<br>
	 * If this method is called, then {@link #getValidatedLocation(Dockable, CLocationMode, Location)} will be called
	 * before <code>dockable</code> actually is moved to a new location.
	 * @param dockable the dockable whose location is about to be changed
	 * @param mode the target mode for <code>dockable</code>
	 * @param history the old location of <code>dockable</code>, may be <code>null</code>
	 * @return either <code>history</code>, or a newly created location. Also <code>null</code> is valid result.
	 * This method is allowed to return an invalid location, invalid locations however will be replaced with valid location.
	 */
	public Location getStoredLocation( Dockable dockable, CLocationMode mode, Location history );
	
	/**
	 * Called right before <code>dockable</code> is moved to a new location. 
	 * @param dockable the {@link Dockable} whose location is about to be changed
	 * @param mode the target mode for <code>dockable</code>
	 * @param validatedHistory a validated location. This may be the result of {@link #getStoredLocation(Dockable, CLocationMode, Location)} 
	 * if that result already was a valid location, or it may be a new location, may be <code>null</code>
	 * @return either <code>validatedHistory</code>, or a newly created location. Also <code>null</code> is a valid result.
	 * This method should return only valid locations, invalid locations will lead the framework to place the dockable at some
	 * default location
	 */
	public Location getValidatedLocation( Dockable dockable, CLocationMode mode, Location validatedHistory );
	
	/**
	 * Always called after <code>dockable</code> has changed its location. This method will be called for any {@link Dockable} that
	 * changed its location, even if {@link #getStoredLocation(Dockable, CLocationMode, Location)} or {@link #getValidatedLocation(Dockable, CLocationMode, Location)} 
	 * was never called.<br>
	 * Note: this method acts as if a {@link DockHierarchyListener} would have been added to <code>dockable</code>
	 * @param dockable a {@link Dockable} that has a new place
	 */
	public void hierarchyChanged( Dockable dockable );

	/**
	 * Called after <code>dockable</code> has gained the focus.
	 * @param dockable the element that just gained the focus
	 */
	public void focusGained( Dockable dockable );
}
