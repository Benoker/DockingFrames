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
package bibliothek.gui.dock.station.stack.tab;

import bibliothek.gui.dock.station.stack.tab.layouting.Size;

/**
 * Provides customizable algorithms used by the {@link MenuLineLayout}, these algorithms
 * tell how exactly to lay out the items of a {@link TabPane}.
 * @author Benjamin Sigg
 */
public interface MenuLineLayoutStrategy {
	/**
	 * Calculates how good a layout with the given sizes is. To calculate the score
	 * the method may call {@link Size#getScore()}. {@link MenuLineLayout} will create
	 * different combinations of {@link Size}, call this method, and use the one combination
	 * with the highest score.
	 * @param possibility a description of the layout that might be applied 
	 * @param menuSize the size of the menu, can be <code>null</code> if the menu is now shown
	 * @param infoSize the size of the info-component, can be <code>null</code> if the component is not shown
	 * @param tabSize the size of the tabs, can be <code>null</code> if there are no tabs
	 * @return the score, a value between <code>0.0</code> and <code>1.0</code>
	 */
	public double getScore( MenuLineLayoutPossibility possibility, Size menuSize, Size infoSize, Size tabSize );
}
