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
package bibliothek.extension.gui.dock.preference;

import javax.swing.Icon;

/**
 * A view of a {@link PreferenceOperation} tailored to use the properties that
 * are related to a {@link PreferenceModel}.
 * @author Benjamin Sigg
 */
public interface PreferenceOperationView {
	/**
	 * Gets the operation which is represented by this view.
	 * @return the operation, not <code>null</code>
	 */
	public PreferenceOperation getOperation();
	
	/**
	 * Gets the current icon of this view.
	 * @return the icon
	 */
	public Icon getIcon();
	
	/**
	 * Gets the description of this view.
	 * @return the description
	 */
	public String getDescription();
	
	/**
	 * Adds a listener to this view, the listener is to be informed about
	 * changes on this view.
	 * @param listener the new listener
	 */
	public void addListener( PreferenceOperationViewListener listener );
	
	/**
	 * Removes the listener <code>listener</code> from this view.
	 * @param listener the listener to remove
	 */
	public void removeListener( PreferenceOperationViewListener listener );
	
	/**
	 * Informs this view that it is no longer required and can safely free resources.
	 */
	public void destroy();
}
