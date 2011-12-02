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
 * A listener that is added to a {@link PreferenceOperation} and receives events
 * if the properties of the operation changes
 * @author Benjamin Sigg
 */
public interface PreferenceOperationViewListener {
	/**
	 * Called when the icon of <code>operation</code> changed.
	 * @param operation the operation whose icon changed
	 * @param oldIcon the old icon, may be <code>null</code>
	 * @param newIcon the new icon, may be <code>null</code>
	 */
	public void iconChanged( PreferenceOperationView operation, Icon oldIcon, Icon newIcon );
	
	/**
	 * Called when the description of <code>operation</code> changed.
	 * @param operation the operation whose text changed
	 * @param oldDescription the old text
	 * @param newDescription the new text
	 */
	public void descriptionChanged( PreferenceOperationView operation, String oldDescription, String newDescription );
}
