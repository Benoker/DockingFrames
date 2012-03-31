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

/**
 * The link between {@link Span} and {@link DockStation}.
 * @author Benjamin Sigg
 */
public interface SpanCallback {
	/**
	 * Gets the {@link DockStation} that is using this {@link Span}.
	 * @return the station, never <code>null</code>
	 */
	public DockStation getStation();
	
	/**
	 * Tells whether the {@link Span} influences some width.
	 * @return whether the {@link Span} is horizontal, the opposite of {@link #isVertical()}
	 */
	public boolean isHorizontal();
	
	/**
	 * Tells whether the {@link Span} influences some height.
	 * @return whether the {@link Span} is vertical, the opposite of {@link #isHorizontal()}
	 */
	public boolean isVertical();
	
	/**
	 * To be called by the {@link Span} every time when its size changes. This method should be called
	 * from the <code>EventDispatcherThread</code>.
	 */
	public void resized();
	
	/**
	 * Tells the {@link Span} how it is used.
	 * @return the usage
	 */
	public SpanUsage getUsage();
}
