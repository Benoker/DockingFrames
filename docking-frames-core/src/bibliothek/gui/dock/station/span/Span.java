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
 * A {@link Span} is an empty space that show up on a {@link DockStation} mostly during drag and drop operations. Every
 * {@link DockStation} can contain multiple spans which can change their size at any time.<br>
 * {@link Span}s are created by the customizable {@link SpanFactory}, and interact with their {@link DockStation} using
 * a {@link SpanCallback}.
 * @author Benjamin Sigg
 */
public interface Span {
	/**
	 * Tells this {@link Span} to mutate its size according to the settings made for <code>mode</code>. If
	 * there are no settings for <code>mode</code>, then the default values from <code>mode</code> itself should
	 * be utilized. Whether the {@link Span} changes its size instantly, or uses some kind of animation, is up to
	 * the span. In any case the mutation should not take any longer than 1 second.
	 * @param mode the mode into which this {@link Span} should go
	 */
	public void mutate( SpanMode mode );
	
	/**
	 * Like {@link #mutate(SpanMode)}, but the mutation into <code>mode</code> has to be instantly.
	 * @param mode the mode to apply instantly
	 */
	public void set( SpanMode mode );
	
	/**
	 * Tells this {@link Span} that <code>mode</code> is associated with a size of <code>size</code> pixels. If this
	 * {@link Span} currently is in <code>mode</code>, then it should apply the new size as if {@link #set(SpanMode)} would
	 * be called.
	 * @param mode the mode for which to set a size
	 * @param size the size in pixels, at least 0
	 */
	public void configureSize( SpanMode mode, int size );
	
	/**
	 * Gets the current size of this {@link Span}.
	 * @return the current size, at least 0
	 */
	public int getSize();
}
