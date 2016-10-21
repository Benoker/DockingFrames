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
package bibliothek.gui.dock.themes;

import bibliothek.gui.DockStation;
import bibliothek.gui.dock.station.span.SpanFactory;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * An {@link UIValue} that is used to retrieve a {@link SpanFactory} from a {@link ThemeManager}.
 * @author Benjamin Sigg
 */
public interface SpanFactoryValue extends UIValue<SpanFactory>{
	/** the kind of {@link UIValue} this is */
	public static final Path KIND_SPAN_FACTORY = new Path( "dock", "span" );
	
	/**
	 * Gets the station for which this {@link UIValue} works.
	 * @return the owner
	 */
	public DockStation getStation();
}
