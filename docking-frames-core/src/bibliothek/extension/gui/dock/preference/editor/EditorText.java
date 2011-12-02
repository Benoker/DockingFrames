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
package bibliothek.extension.gui.dock.preference.editor;

import bibliothek.extension.gui.dock.preference.PreferenceEditor;
import bibliothek.gui.dock.util.UIValue;
import bibliothek.gui.dock.util.text.TextValue;
import bibliothek.util.Path;

/**
 * A text that is used by a {@link PreferenceEditor}.
 * @author Benjamin Sigg
 */
public abstract class EditorText extends TextValue{
	/** what kind of {@link UIValue} this is */
	public static final Path KIND_EDITOR = KIND_TEXT.append( "editor" );
	
	/** the editor using the text */
	private PreferenceEditor<?> editor;
	
	/**
	 * Creates a new text.
	 * @param id a unique identifier for this text
	 * @param editor the editor using this text
	 */
	public EditorText( String id, PreferenceEditor<?> editor ){
		super( id, KIND_EDITOR );
		this.editor = editor;
	}
	
	/**
	 * Gets the editor which is used by this text.
	 * @return the editor
	 */
	public PreferenceEditor<?> getEditor(){
		return editor;
	}
}
