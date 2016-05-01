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
package bibliothek.extension.gui.dock.preference;

import javax.swing.Icon;

import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.icon.DockIcon;
import bibliothek.util.Path;

/**
 * A {@link PreferenceOperationIcon} represents an {@link Icon} that is used
 * by a {@link PreferenceOperation}.
 * @author Benjamin Sigg
 */
public abstract class PreferenceOperationIcon extends DockIcon{
	/** The kind of {@link UIValue} this is */
	public static final Path KIND_PREFERENCE_OPERATION = KIND_ICON.append( "preference" );

	/** the operation for which the icon is used */
	private PreferenceOperation operation;
	
	/**
	 * Creates a new icon.
	 * @param id the identifier of this icon
	 * @param operation the operation which uses this icon
	 */
	public PreferenceOperationIcon( String id, PreferenceOperation operation ){
		super( id, KIND_PREFERENCE_OPERATION );
		this.operation = operation;
	}
	
	/**
	 * Gets the operation for which this icon is used.
	 * @return the operation
	 */
	public PreferenceOperation getOperation(){
		return operation;
	}
}
