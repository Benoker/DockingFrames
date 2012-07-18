/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;

/**
 * This factory creates {@link TabConfiguration}s, which can be used to fine tune the look
 * of various tabs.<br>
 * Please note that only a subset of tabs actually supports this configuration, some 
 * {@link DockTheme}s will simply ignore this factory.
 * @see TabConfiguration
 * @see StackDockStation#TAB_CONFIGURATIONS
 * @author Benjamin Sigg
 */
public interface TabConfigurations {
	/** the default configuration, which does nothing but create new {@link TabConfiguration}s */
	public static final TabConfigurations DEFAULT = new TabConfigurations(){
		public TabConfiguration getConfiguration( Dockable dockable ){
			return new TabConfiguration();
		}
	};
	
	/**
	 * Gets the configuration for a tab that represents {@link Dockable}. This method may
	 * create a new {@link TabConfiguration} every time it is called, or reuse the same configuration
	 * multiple times.
	 * @param dockable the element whose tab will be shown
	 * @return the configuration, not <code>null</code>
	 */
	public TabConfiguration getConfiguration( Dockable dockable );
}
