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
package bibliothek.extension.gui.dock.preference.preferences.choice;

/**
 * A listener that is added to a {@link Choice}.
 * @author Benjamin Sigg
 */
public interface ChoiceListener {
	/**
	 * This method is called if some new entries have been added to <code>choice</code>
	 * @param choice the source of the event
	 * @param indexStart the index of the first affected entry
	 * @param indexEnd the index of the last affected entry
	 */
	public void inserted( Choice choice, int indexStart, int indexEnd );
	
	/**
	 * This method is called if some new entries have been removed from <code>choice</code>
	 * @param choice the source of the event
	 * @param indexStart the index of the first affected entry
	 * @param indexEnd the index of the last affected entry
	 */
	public void removed( Choice choice, int indexStart, int indexEnd );

	/**
	 * This method is called if some entries have been updated, e.g. if the
	 * text of some entries changed
	 * @param choice the source of the event
	 * @param indexStart the index of the first affected entry
	 * @param indexEnd the index of the last affected entry
	 */
	public void updated( Choice choice, int indexStart, int indexEnd );
}
