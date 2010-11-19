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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.DynamicPropertyFactory;
import bibliothek.util.FrameworkOnly;

/**
 * This class is used to set up the {@link PropertyKey}s of a {@link DockTheme}.
 * @author Benjamin Sigg
 * @param <A> The kind of item this factory generates
 */
@FrameworkOnly
public abstract class StationThemeItemFactory<A> extends DynamicPropertyFactory<StationThemeItem<A>>{
	public StationThemeItem<A> getDefault( PropertyKey<StationThemeItem<A>> key, DockProperties properties ){
		final ThemeManager theme = properties.getController().getThemeManager();
		return new StationThemeItem<A>(){
			public A get( DockStation station ){
				return StationThemeItemFactory.this.get( theme, station );
			}
		};
	}
	
	/**
	 * Creates a new item.
	 * @param theme the manager that stores the current theme
	 * @param station the station for which the item will be used
	 * @return the item, may be <code>null</code> (depends on the property)
	 */
	protected abstract A get( ThemeManager theme, DockStation station );
}
