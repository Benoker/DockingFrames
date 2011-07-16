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
package bibliothek.extension.gui.dock.preference;

import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * A text that is used by a {@link PreferenceOperation}.
 * @author Benjamin Sigg
 */
public abstract class PreferenceOperationText extends TextValue{
	/** what kind of {@link UIValue} this is */
	public static final Path KIND_OPERATION = KIND_TEXT.append( "operation" );
	
	/** the element that is using this text */
	private PreferenceOperation operation;
	
	/**
	 * Creates a new text.
	 * @param id the unique identifier of this text
	 * @param operation the operation that is using this text
	 */
	public PreferenceOperationText( String id, PreferenceOperation operation ){
		super( id, KIND_OPERATION );
		this.operation = operation;
	}
	
	/**
	 * Gets the element that is using this text.
	 * @return the element
	 */
	public PreferenceOperation getOperation(){
		return operation;
	}
}
