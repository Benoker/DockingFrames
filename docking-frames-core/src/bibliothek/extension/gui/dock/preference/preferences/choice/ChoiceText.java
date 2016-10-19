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
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * Represents a text that is used for a {@link Choice}.
 * @author Benjamin Sigg
 */
public abstract class ChoiceText extends TextValue{
	/** the kind of this {@link UIValue} */
	public static final Path KIND_CHOICE = KIND_TEXT.append( "choice" );
	
	/** the choice using this text */
	private Choice choice;

	/**
	 * Creates a new {@link ChoiceText}.
	 * @param id the unique identifier of this {@link UIValue}
	 * @param choice the choice for which the text is required
	 */
	public ChoiceText( String id, Choice choice ){
		super( id, KIND_CHOICE );
		this.choice = choice;
	}
	
	/**
	 * Creates a new {@link ChoiceText}.
	 * @param id the unique identifier of this {@link UIValue}
	 * @param choice the choice for which the text is required
	 * @param kind what kind of {@link UIValue} this is
	 */
	public ChoiceText( String id, Choice choice, Path kind ){
		super( id, kind );
		this.choice = choice;
	}

	/**
	 * Gets the choice which is using this text.
	 * @return the choice
	 */
	public Choice getChoice(){
		return choice;
	}
}
