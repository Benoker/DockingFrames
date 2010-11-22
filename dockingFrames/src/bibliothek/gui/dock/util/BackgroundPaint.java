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
package bibliothek.gui.dock.util;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.gui.DockTheme;

/**
 * A {@link BackgroundPaint} is used to paint the background of various {@link Component}s of 
 * this framework.<br>
 * Please note that some items provided by a {@link DockTheme} do not use a {@link BackgroundPaint} 
 * because they already paint their background in a specific way.
 * @author Benjamin Sigg
 */
public interface BackgroundPaint {
	/**
	 * Informs this paint that is will be used by <code>component</code>.
	 * @param component the component that is going to use this paint, not <code>null</code>
	 */
	public void install( BackgroundComponent component );
	
	/**
	 * Informs this paint that it is no longer used by <code>component</code>.
	 * @param component the component that no longer uses this paint, not <code>null</code>
	 */
	public void uninstall( BackgroundComponent component );
	
	/**
	 * Paints the background <code>component</code> using the graphics context <code>g</code>. The
	 * exact behavior of this method may depend on the type of <code>component</code>.
	 * @param background the component to paint, is installed on this paint, not <code>null</code>
	 * @param component the component that is currently painted, may be either <code>background</code> or a child
	 * of <code>background</code>. Any other value can lead to an {@link IllegalArgumentException}.
	 * @param g the graphics context to use, not <code>null</code>
	 * @throws IllegalArgumentException if <code>component</code> is not equal to <code>background</code> or not
	 * a child of <code>background</code>
	 * @return <code>true</code> if this paint painted a background, <code>false</code> if this paint refuses
	 * to paint <code>component</code> (in which case <code>component</code> falls back to its original 
	 * algorithm to paint a background)
	 */
	public boolean paint( BackgroundComponent background, Component component, Graphics g );
}
