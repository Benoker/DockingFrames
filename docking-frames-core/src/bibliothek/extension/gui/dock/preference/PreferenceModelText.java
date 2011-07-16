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
 * A text that is used by a {@link PreferenceModel}.
 * @author Benjamin Sigg
 */
public abstract class PreferenceModelText extends TextValue{
	/** what kind of {@link UIValue} this is */
	public static final Path KIND_PREFERENCE_MODEL = KIND_TEXT.append( "preference_model" );
	
	/** the model using this text */
	private PreferenceModel model;

	/**
	 * Creates a new text.
	 * @param id the unique identifier of this {@link UIValue}
	 * @param model the model that is using this text
	 */
	public PreferenceModelText( String id, PreferenceModel model ){
		super( id, KIND_PREFERENCE_MODEL );
		this.model = model;
	}
	
	/**
	 * Gets the model which is using this text.
	 * @return the model
	 */
	public PreferenceModel getModel(){
		return model;
	}
}
