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
package bibliothek.gui.dock.station.split;

import java.awt.Rectangle;

import bibliothek.gui.dock.SplitDockStation.Orientation;

/**
 * A {@link Divideable} offers the information needed to represent a divider between two
 * {@link SplitNode}s. The {@link Divideable} may itself be a {@link SplitNode}. The location of a divider
 * is encoded by a <code>double</code>, where <code>0</code> means top/left, and <code>1.0</code> means bottom/right. 
 * @author Benjamin Sigg
 */
public interface Divideable {
    /**
     * Calculates the value which the divider must have on condition that
     * the point <code>x/y</code> lies inside the {@link #getDividerBounds(double, Rectangle) divider bounds}.
     * @param x x-coordinate of the point in pixel
     * @param y y-coordinate of the point in pixel
     * @return The value that the divider should have. This value might not
     * be valid if the coordinates of the point are too extreme.
     */
	public double getDividerAt( int x, int y );

    /**
     * Calculates the location and the size of the area which represents the divider.
     * The user can grab this area with the mouse and drag it around.
     * @param divider The location of the divider, should be between 0 and 1.
     * @param bounds A rectangle in which the result will be stored. It can be <code>null</code>
     * @return Either <code>bounds</code> or a new {@link Rectangle} if <code>bounds</code>
     * was <code>null</code>
     */
	public Rectangle getDividerBounds( double divider, Rectangle bounds );

    /**
     * Gets the orientation of this divideable. The orientation tells how to layout
     * the children. If the orientation is {@link Orientation#VERTICAL}, one child
     * will be at the top and the other at the bottom.
     * @return the orientation
     */
	public Orientation getOrientation();

    /**
     * Gets the location of the divider.
     * @return the divider
     * @see #setDivider(double)
     */
	public double getDivider();

	/**
	 * Gets the {@link #getDivider() divider} as it is actually seen by the user. Usually this is equivalent
	 * to <code>validateDivider( getDivider() )</code>, subclasses may however consider other restrictions.
	 * @return the divider as seen by the user
	 */
	public double getActualDivider();
	
    /**
     * Sets the location of the divider. The area of the left child is the area
     * of the whole node multiplied with <code>divider</code>. 
     * @param divider the divider
     */
	public void setDivider( double divider );
	
	/**
	 * Validates the new location <code>divider</code>.
	 * @param divider the new divider
	 * @return a valid version of <code>divider</code>
	 */
	public double validateDivider( double divider );
}
