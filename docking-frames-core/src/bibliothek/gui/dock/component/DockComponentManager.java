/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2013 Benjamin Sigg
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
package bibliothek.gui.dock.component;

import bibliothek.gui.DockController;
import bibliothek.util.Filter;

/**
 * Collects and manages the {@link DockComponentRoot}s of one {@link DockController}.
 * @author Benjamin Sigg
 */
public interface DockComponentManager {
	/**
	 * Sets the default configuration which is to be used if no other configuration is found.
	 * @param configuration the default configuration, may be <code>null</code>
	 */
	public void setDefaultConfiguration( DockComponentConfiguration configuration );
	
	/**
	 * Adds a new configuration, the configuration is only applied if <code>filter</code> includes a {@link DockComponentRoot}, and
	 * if no other configuration was applied
	 * @param filter the condition telling where to apply the configuration, not <code>null</code>
	 * @param configuration the new configuration, not <code>null</code>
	 */
	public void addConfiguration( Filter<DockComponentRoot> filter, DockComponentConfiguration configuration );
	
	/**
	 * Removes <code>configuration</code> from the list of configurations. If <code>configuration</code> was added more than once,
	 * then only the first occurrence will be removed.
	 * @param configuration the configuration that should no longer be applied
	 */
	public void removeConfiguration( DockComponentConfiguration configuration );
	
	/**
	 * Adds <code>root</code> to the list of known {@link DockComponentRoot}s, informs all listeners and applies a configuration
	 * to <code>root</code>.
	 * @param root the new root, not <code>null</code>
	 */
	public void register( DockComponentRoot root );
	
	/**
	 * Removes <code>root</code> from the list of known {@link DockComponentRoot}s.
	 * @param root the root to remove
	 */
	public void unregister( DockComponentRoot root );
}
