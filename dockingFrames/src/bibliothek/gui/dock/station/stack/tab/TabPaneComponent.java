/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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
package bibliothek.gui.dock.station.stack.tab;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;

/**
 * A {@link TabPaneComponent} is a child of a {@link TabPane}. It is painted
 * onto the screen and has some boundaries.
 * @author Benjamin Sigg
 */
public interface TabPaneComponent {
	/**
	 * Gets the parent of this component.
	 * @return the parent
	 */
	public TabPane getTabParent();
	
	/**
	 * Gets the current location and size of this component.
	 * @return the size and location
	 */
	public Rectangle getBounds();
	
	/**
	 * Sets the current location and size of this component.
	 * @param bounds the size and location
	 */
	public void setBounds( Rectangle bounds );
	
	/**
	 * Sets the z order of this component. The z order tells which component to
	 * paint first, as higher the order as earlier a component is to be painted.
	 * If two components overlap, then the one with the lower z order appears
	 * in front of the one with the higher z order. If two components have the same
	 * z order, then it is unspecified which component is painted first. 
	 * @param order the order, can be any integer.
	 */
	public void setZOrder( int order );
	
	/**
	 * Gets the value of the z order.
	 * @return the z order
	 * @see #setZOrder(int)
	 */
	public int getZOrder();
	
	/**
	 * Tells how much of this component may be overlapped by another component <code>other</code>.
	 * @param other another component which may be painted over this component
	 * @return the border of this component that may not be visible, not <code>null</code>
	 */
	public Insets getOverlap( TabPaneComponent other );
	
	/**
	 * Gets the minimal size this component should have
	 * @return the minimal size
	 */
	public Dimension getMinimumSize();
	
	/**
	 * Gets the preferred size of this component
	 * @return the preferred size
	 */
	public Dimension getPreferredSize();
	
	/**
	 * Gets the maximal size this component should have
	 * @return the maximal size
	 */
	public Dimension getMaximumSize();
}
