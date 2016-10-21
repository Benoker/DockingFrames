/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2008 Benjamin Sigg
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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.gui.DockController;

/**
 * A choice is a set of entries from which the user can choose one. Note
 * that some components assume that a <code>Choice</code> is immutable.
 * @author Benjamin Sigg
 */
public interface Choice {
	/**
	 * Adds a listener to this choice. The listener is informed about changes
	 * of the entries of this choice.
	 * @param listener the new listener, not <code>null</code>
	 */
	public void addChoiceListener( ChoiceListener listener );
	
	/**
	 * Removes the listener <code>listener</code> from this choice.
	 * @param listener the listener to remove
	 */
	public void removeChoiceListener( ChoiceListener listener );
	
	/**
	 * Informs this {@link Choice} that it is now used for displaying items for
	 * <code>controller</code>.
	 * @param controller the controller in whose realm this {@link Choice} is currently
	 * used, can be <code>null</code>
	 */
	public void setController( DockController controller );
	
	/**
	 * Gets the number of available choices.
	 * @return the number of choices
	 */
	public int size();
	
	/**
	 * Gets a name for the <code>index</code>'th choice.
	 * @param index the index of the choice
	 * @return the name of that choice, should be human readable
	 */
	public String getText( int index );
	
	/**
	 * Gets a unique identifier for the <code>index</code>'th choice.
	 * @param index the index of the choice
	 * @return the unique identifier
	 */
	public String getId( int index );
	
	/**
	 * Gets the standard choice.
	 * @return the identifier of the standard choice, <code>null</code> is only
	 * allowed if {@link #isNullEntryAllowed()} returns <code>true</code>
	 */
	public String getDefaultChoice();
	
	/**
	 * Tells whether the user choose nothing.
	 * @return whether the user can choose the <code>null</code> identifier
	 */
	public boolean isNullEntryAllowed();
}
