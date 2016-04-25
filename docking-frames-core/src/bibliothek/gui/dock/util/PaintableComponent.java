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


/**
 * A wrapper around a {@link Component} whose paint algorithm may be
 * modified by a {@link BackgroundPaint}. The provider of this method may
 * paint some parts automatically if the appropriate method is not called. The usual
 * order in which the paint methods should be executed is:
 * <ol>
 * 	<li> {@link #paintBackground(Graphics)} </li>
 *  <li> {@link #paintForeground(Graphics)} </li>
 *  <li> {@link #paintBorder(Graphics)} </li>
 *  <li> {@link #paintChildren(Graphics)} </li>
 *  <li> {@link #paintOverlay(Graphics)} </li>
 * </ol>
 * @author Benjamin Sigg
 */
public interface PaintableComponent {
	/**
	 * Gets the {@link Component} which is to be painted.
	 * @return the item to paint
	 */
	public Component getComponent();
	
	/**
	 * Tells how much of this component is actually painted.
	 * @return the transparency effects of this component, not <code>null</code>
	 */
	public Transparency getTransparency();
	
	/**
	 * Invokes the standard algorithm that paints the background
	 * of the component. This method should be called at most once. 
	 * @param g the graphics context to use, <code>null</code> to just inform
	 * this component that the background should not be painted automatically
	 */
	public void paintBackground( Graphics g );
	
	/**
	 * Invokes the standard algorithm that paints the foreground
	 * of the component. This method should be called at most once.
	 * @param g the graphics context to use, <code>null</code> to just inform
	 * this component that the foreground should not be painted automatically
	 */
	public void paintForeground( Graphics g );

	/**
	 * Invokes the standard algorithm that paints the border
	 * of the component. This method should be called at most once. 
	 * @param g the graphics context to use, <code>null</code> to just inform
	 * this component that the background should not be painted automatically
	 */
	public void paintBorder( Graphics g );
	
	/**
	 * Invokes the standard algorithm that paints the children
	 * of the component. This method should be called at most once. 
	 * @param g the graphics context to use, <code>null</code> to just inform
	 * this component that the background should not be painted automatically
	 */
	public void paintChildren( Graphics g );
	
	/**
	 * Invokes the standard algorithm that paints an overlay over the children
	 * of the component. This method should be called at most once. 
	 * @param g the graphics context to use, <code>null</code> to just inform
	 * this component that the background should not be painted automatically
	 */
	public void paintOverlay( Graphics g );
}
