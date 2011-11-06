/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2011 Benjamin Sigg
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
package bibliothek.gui.dock.station.layer;

import java.awt.Component;

import bibliothek.gui.DockStation;

/**
 * A {@link DockStationDropLayer} describes an area of the screen where a drag and drop operation 
 * can end with the "drop event". Several levels can be active at the same time, and there is a strong
 * order in which level is more important than the other level, the first level hit by the mouse wins.<br>
 * 
 * The order of two {@link DockStationDropLayer}s is calculated by applying the following steps, the first conclusive
 * result is the final result.
 * <ul>
 * 	<li>If either of the levels return <code>true</code> on {@link #canCompare(DockStationDropLayer)}, then 
 *  the {@link #compare(DockStationDropLayer)} method is called for comparison. If both levels implement
 *  the <code>compare</code> method but the results contradict each other, then this step
 *  is ignored.</li>
 *  <li>The {@link LayerPriority}s are compared, the one level where {@link LayerPriority#getPriority()} is higher
 *  also has a higher priority.</li>
 *  <li>If the result of {@link LayerPriority#isReverse()} does not match, the level where <code>reverse</code> is
 *  <code>false</code> has higher priority.</li>
 *  <li>Depending on the result of {@link LayerPriority#isReverse()} the result of the remaining steps is inverted.</li>
 *  <li>If the {@link #getStation() station} of one level is an ancestor of the other level, then the child station
 *  has higher priority.</li>
 *  <li>Otherwise one level randomly has a higher priority than the other.</li>
 * </ul>
 * Levels whose {@link #contains(int, int)} method returns <code>false</code> for the current position of the mouse
 * are ignored for the order, but not when calling {@link #modify(DockStationDropLayer) modify}.
 * 
 * @author Benjamin Sigg
 */
public interface DockStationDropLayer {
	/**
	 * Gets the {@link DockStation} which created this level.
	 * @return the creator and owner of this level
	 */
	public DockStation getStation();
	
	/**
	 * Gets a {@link Component} which is associated with this level. The {@link Component} can be used to order
	 * levels because {@link Component}s can overlap each other. This is an optional method, a result of
	 * <code>null</code> is perfectly valid.
	 * @return some component or <code>null</code>
	 */
	public Component getComponent();

	/**
	 * This method is called for any {@link DockStationDropLayer} whose {@link #getStation() station}
	 * is a child to the station of <code>this</code> level. This method may modify the level, e.g. increase or
	 * decrease its priority. This method is called before <code>this</code> level itself gets modified by its
	 * parents. This method is called independent of whether {@link #contains(int, int)} returns <code>true</code>
	 * or <code>false</code>.
	 * @param child the child to modify, this method may either directly modify <code>child</code>, create a wrapper or
	 * a copy of <code>child</code>
	 * @return either <code>child</code> or a new {@link DockStationDropLayer} replacing <code>child</code>, not <code>null</code>
	 */
	public DockStationDropLayer modify( DockStationDropLayer child );

	/**
	 * Tells whether this <code>level</code> contains the point <code>x/y</code>, which is the position
	 * of the mouse on the screen.
	 * @param x the x-coordinate of the mouse on the screen
	 * @param y the y-coordinate of the mouse on the screen
	 * @return <code>true</code> if <code>this</code> level contains <code>x/y</code>, <code>false</code>
	 * otherwise
	 */
	public boolean contains( int x, int y );

	/**
	 * Gets the basic priority of this level.
	 * @return the basic priority, must not be <code>null</code>
	 */
	public LayerPriority getPriority();

	/**
	 * Sets a new priority for this level.
	 * @param priority the new priority, must not be <code>null</code>
	 */
	public void setPriority( LayerPriority priority );

	/**
	 * Tells whether this level contains code to tell whether it has a higher or lower priority as
	 * <code>level</code>. For most implementations the result of this method should be <code>false</code>.<br>
	 * If this method returns <code>true</code>, then the ordering defined by the custom algorithm supercedes
	 * any other conditions. 
	 * @param level some other level to check
	 * @return <code>true</code> if this {@link DockStationDropLayer} contains code to compare <code>level</code> with
	 * <code>this</code>
	 */
	public boolean canCompare( DockStationDropLayer level );

	/**
	 * Compares this level with <code>level</code>. This method is only called if {@link #canCompare(DockStationDropLayer)}
	 * returned <code>true</code> for <code>level</code>. This method works like {@link Comparable#compareTo(Object)}.
	 * @param level another level to compare
	 * @return a number less/equal/higher than zero, if this level has
	 * higher/equal/lesser priority than <code>level</code>.
	 */
	public int compare( DockStationDropLayer level );
}
