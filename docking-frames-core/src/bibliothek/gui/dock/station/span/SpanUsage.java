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
package bibliothek.gui.dock.station.span;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;

/**
 * A description telling for what purpose a {@link Span} is used. Clients may define their own constants, but the
 * framework itself will only use the default purposes.
 * @author Benjamin Sigg
 */
public class SpanUsage {
	/** Marks a {@link Span} that usually has a size of <code>0</code> and is made larger for an invisible {@link DockStation} to show up */
	public static final SpanUsage HIDING = new SpanUsage();
	
	/** Marks a {@link Span} that shows up when inserting a {@link Dockable} at a specific place, e.g. between two existing {@link Dockable}s */
	public static final SpanUsage INSERTING = new SpanUsage();
}
