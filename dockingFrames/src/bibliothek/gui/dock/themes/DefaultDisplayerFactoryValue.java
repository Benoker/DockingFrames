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

package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * A wrapper for a {@link DisplayerFactory}. Every call to the Interface
 * is forwarded to a delegate. If no delegate is set, a default 
 * <code>DisplayerFactory</code> provided by the {@link DockTheme}
 * is used.
 * @author Benjamin Sigg
 */
public class DefaultDisplayerFactoryValue extends StationThemeItemValue<DisplayerFactory> implements DisplayerFactoryValue {
    
	/**
	 * Creates a new object.
	 * @param id the identifier used for retrieving a resource of {@link ThemeManager}
	 * @param station the owner of this object, not <code>null</code>
	 */
	public DefaultDisplayerFactoryValue( String id, DockStation station ){
		super( id, KIND_DISPLAYER_FACTORY, ThemeManager.DISPLAYER_FACTORY_TYPE, station );
	}
	
 
    /**
     * Uses the current factory to create a new {@link DockableDisplayer}. Falls back to a {@link BasicDockableDisplayer}
     * if no factory can be found.
     * @param dockable the element for which a displayer is required
     * @param title the title of the displayer, can be <code>null</code>
     * @return the new displayer
     */
    public DockableDisplayer create( Dockable dockable, DockTitle title ) {
    	DisplayerFactory factory = get();
    	if( factory == null ){
    		factory = new BasicDisplayerFactory();
    	}
    	
        return factory.create( getStation(), dockable, title );
    }
}
