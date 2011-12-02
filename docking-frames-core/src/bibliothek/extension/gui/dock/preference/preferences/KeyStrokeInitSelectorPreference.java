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

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockableSelector;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * Preference for the shortcut that allows selecting a new {@link Dockable}
 * with the keyboard.
 * @author Benjamin Sigg
 * @see DockableSelector#INIT_SELECTION
 */
public class KeyStrokeInitSelectorPreference extends DockPropertyPreference<KeyStroke>{
	/**
	 * Creates a new preference
	 * @param properties to read and write the value of this preference
	 */
	public KeyStrokeInitSelectorPreference( DockProperties properties ){
		super( properties, DockableSelector.INIT_SELECTION, Path.TYPE_KEYSTROKE_PATH, new Path( "dock.DockableSelector.INIT_SELECTION" ));
		
		setLabelId( "preference.shortcuts.init_selection.label" );
		setDescriptionId( "preference.shortcuts.init_selection.description" );
		
		setDefaultValue( KeyStroke.getKeyStroke( KeyEvent.VK_E, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK ) );
	}
}
