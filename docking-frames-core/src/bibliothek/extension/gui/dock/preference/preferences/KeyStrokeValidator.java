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
package bibliothek.extension.gui.dock.preference.preferences;

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.editor.KeyStrokeEditor;
import bibliothek.util.Path;

/**
 * Information for {@link Path#TYPE_KEYSTROKE_PATH} about how a keystroke
 * has to be chosen.
 * @author Benjamin Sigg
 */
public interface KeyStrokeValidator {
	/** a validator that accepts any stroke */
	public static final KeyStrokeValidator EVERYTHING = new KeyStrokeValidator(){
		public boolean isValid(KeyStroke keyStroke) {
			return true;
		}
		public boolean isCompleteable(KeyStroke keyStroke) {
			return false;
		}
	};
	
	/** A validator that does not allow modifier keystrokes like pressing a single 'shift' */
	public static final KeyStrokeValidator NO_MODIFIER = new KeyStrokeValidator(){
		public boolean isValid( KeyStroke keyStroke ){
			return !KeyStrokeEditor.isModifierKey( keyStroke.getKeyCode() );
		}
		
		public boolean isCompleteable(KeyStroke keyStroke) {
			return true;
		}
	};
	
	/** a validator that allows only modifier keys */
	public static final KeyStrokeValidator MODIFIER = new KeyStrokeValidator(){
		public boolean isValid(KeyStroke keyStroke) {
			return KeyStrokeEditor.isModifierKey( keyStroke.getKeyCode() );
		}
		public boolean isCompleteable(KeyStroke keyStroke) {
			return false;
		}
	};
	
	/**
	 * Checks whether <code>keyStroke</code> is valid.
	 * @param keyStroke the keystroke to check
	 * @return <code>true</code> if valid
	 */
	public boolean isValid( KeyStroke keyStroke );
	
	/**
	 * Tells whether the invalid <code>keyStroke</code> can become valid
	 * by adding additional keys.
	 * @param keyStroke some invalid KeyStroke
	 * @return <code>true</code> if the keystroke can be completed
	 */
	public boolean isCompleteable( KeyStroke keyStroke );

}
