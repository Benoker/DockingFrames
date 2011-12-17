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

import bibliothek.gui.dock.station.screen.ScreenDockWindow;
import bibliothek.gui.dock.station.stack.tab.Tab;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.util.Path;

/**
 * A {@link BackgroundComponent} is a {@link Component} whose background
 * is painted by a {@link BackgroundPaint}.
 * @author Benjamin Sigg
 */
public interface BackgroundComponent extends UIValue<BackgroundPaint>{
	/** the type of this {@link UIValue} */
	public static final Path KIND = new Path( "dock", "background" );
	
	/**
	 * Gets the {@link Component} which is represented by <code>this</code>
	 * @return the component, may not be <code>null</code>
	 */
	public Component getComponent();
	
	/**
	 * Informs this component whether it should be transparent or not. A transparent
	 * component does not paint a background, while a non-transparent component paints
	 * every part of its background. This property is optional and not every component can fully support transparency:
	 * <ul>
	 * 	<li>Root-components like a {@link ScreenDockWindow} usually do not support any transparency at all.</li>
	 *  <li>Decorative components, like a {@link DockTitle} or a {@link Tab}, usually paint some parts of their background even if transparent.</li>
	 * </ul>
	 * It should be noted, that together with a {@link BackgroundPaint}, any component can be made to look as if transparent.
	 * @param transparency whether to paint a background or not
	 */
	public void setTransparency( Transparency transparency );
	
	/**
	 * Tells whether this component is transparent or not.
	 * @return how the background is painted
	 * @see #setTransparency(Transparency)
	 */
	public Transparency getTransparency();
	
	/**
	 * Informs this component that it should be repainted.
	 */
	public void repaint();
}
