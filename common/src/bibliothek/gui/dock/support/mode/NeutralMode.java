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

package bibliothek.gui.dock.support.mode;

import bibliothek.gui.Dockable;

/**
 * The neutral mode allows complex transitions of {@link Dockable} from one
 * {@link Mode} into another. The neutral mode is applied to <i>all</i> {@link Dockable}s
 * before one dockable changes is mode. Neutral modes are organized in a 
 * list.<br>
 * The exact sequence of how {@link NeutralMode}s are applied looks as follows:<br>
 * <ul>
 * 	<li>On making dockables neutral: first all modes at position 0 in the list are applied,
 *  then all modes at position 1 in the list are applied, etc. So the state of all
 *  {@link Dockable}s advances in parallel.</li>
 *  <li>On making specific: the modes are applied in reverse order, the order in which
 *  the {@link Dockable}s are touched is unspecified.</li>
 *  <li>For a dockable changing its {@link Mode}: the element is brought into
 *  neutral mode, then the new mode applied, then made neutral again, then
 *  made specific together with all the other dockables.</li>
 * </ul>
 * @author Benjamin Sigg
 * @param <E> the data container this mode uses
 */
public interface NeutralMode<E> {
	/**
	 * Changes the settings of <code>dockable</code> such that it goes
	 * into neutral state.
	 * @param dockable some element
	 * @param callback to interact with other modes in the tree
	 * @return a data object containing information how to undo the 
	 * changes made by this mode, may be <code>null</code>
	 */
	public E toNeutral( Dockable dockable, NeutralModeCallback callback );
	
	/**
	 * Undoes the changes of this mode and returns <code>dockable</code>
	 * to its specific state.
	 * @param dockable some element
	 * @param data data object created by {@link #toNeutral(Dockable, NeutralModeCallback)},
	 * may be <code>null</code>
	 * @param callback to interact with other modes in this tree
	 */
	public void toSpecific( Dockable dockable, E data, NeutralModeCallback callback );
	
	/**
	 * After applying this mode to <code>dockable</code> gets
	 * the next mode to apply.
	 * @param dockable the element for which the mode is needed
	 * @param data the data that was calculated by {@link #toNeutral(Dockable, NeutralModeCallback)}
	 * @return the next mode or <code>null</code>
	 */
	public NeutralMode<?> getNext( Dockable dockable, E data );
}
