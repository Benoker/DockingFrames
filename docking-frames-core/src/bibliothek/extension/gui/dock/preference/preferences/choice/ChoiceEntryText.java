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

import bibliothek.gui.dock.util.UIValue;
import bibliothek.util.Path;

/**
 * Represents a text that is used for an entry in a {@link Choice}.
 * @author Benjamin Sigg
 */
public abstract class ChoiceEntryText extends ChoiceText{
	/** What kind of {@link UIValue} this is */
	public static final Path KIND_CHOICE_ENTRY = KIND_CHOICE.append( "entry" );
	
	/**
	 * Creates a new text {@link UIValue}
	 * @param id the unique identifier of this {@link UIValue}
	 * @param choice the choice which is using the text
	 */
	public ChoiceEntryText( String id, Choice choice ){
		super( id, choice );
	}
	
	/**
	 * Gets the unique identifier of the entry which is represented by this text.
	 * @return the unique identifier
	 */
	public abstract String getEntryId();
	
	/**
	 * Gets the value which is described by the text
	 * @return the value, may be <code>null</code>
	 */
	public abstract Object getEntryValue();
}
