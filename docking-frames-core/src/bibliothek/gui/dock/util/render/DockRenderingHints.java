/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2015 Benjamin Sigg
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
package bibliothek.gui.dock.util.render;

import java.awt.Component;
import java.awt.Graphics;

import bibliothek.gui.dock.util.PropertyKey;
import bibliothek.gui.dock.util.property.ConstantPropertyFactory;

/**
 * Called by some {@link Component}s before painting, this interface is intended to setup application wide
 * rendering hints, like for example whether to use anti-aliasing.
 * @author Benjamin Sigg
 */
public interface DockRenderingHints {
	public static final PropertyKey<DockRenderingHints> RENDERING_HINTS = 
			new PropertyKey<DockRenderingHints>( "rendering hints", 
					new ConstantPropertyFactory<DockRenderingHints>( new DefaultDockRenderingHints() ), true );
	
	/**
	 * Called before <code>g</code> is used to paint stuff.
	 * @param g the Graphics that will be used for painting
	 */
	public void setupGraphics( Graphics g );
}
