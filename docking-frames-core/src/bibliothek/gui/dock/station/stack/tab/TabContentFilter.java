/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.station.stack.StackDockComponent;
import bibliothek.gui.dock.station.stack.TabContent;
import bibliothek.gui.dock.station.stack.TabContentFilterListener;

/**
 * This interface tells a {@link StackDockStation} or a {@link StackDockComponent} how to fill the tabs
 * for its children. Note that if a {@link StackDockStation} is using a {@link StackDockComponent}, this
 * filter gets not informed about the existence of the {@link StackDockComponent}. However some other
 * modules using a {@link StackDockComponent} may decide to register them directly.
 * @author Benjamin Sigg
 * @see StackDockStation#TAB_CONTENT_FILTER
 */
public interface TabContentFilter {
	/**
	 * Informs this filter that it will be used by <code>station</code>.
	 * @param station a new client
	 */
	public void install( StackDockStation station );
	
	/**
	 * Informs this filter that it will be used by <code>component</code>. Note that this
	 * method may not be called if the <code>component</code> itself is used by a {@link StackDockStation}. 
	 * @param component a new client
	 */
	public void install( StackDockComponent component );
	
	/**
	 * Informs this filter that it is no longer used by <code>station</code>.
	 * @param station an old client
	 */
	public void uninstall( StackDockStation station );
	
	/**
	 * Informs this filter that it is no longer used by <code>component</code>.
	 * @param component the old component
	 */
	public void uninstall( StackDockComponent component );
	
	/**
	 * Adds a listener to this filter. The listener can be called if this filter
	 * changes its behavior.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addListener( TabContentFilterListener listener );
	
	/**
	 * Removes a listener from this filter.
	 * @param listener the listener to remove
	 */
	public void removeListener( TabContentFilterListener listener );
	
	/**
	 * Filters the contents of a tab.
	 * @param content the default content to use, not <code>null</code>
	 * @param station the station which calls this method
	 * @param dockable the element which is displayed
	 * @return the content to show, may be <code>null</code>
	 */
	public TabContent filter( TabContent content, StackDockStation station, Dockable dockable );
	
	/**
	 * Filters the contents of a tab.
	 * @param content the default content to use, not <code>null</code>
	 * @param component the component which calls this method
	 * @param dockable the element which is displayed
	 * @return the content to show, may be <code>null</code>
	 */
	public TabContent filter( TabContent content, StackDockComponent component, Dockable dockable );
}
