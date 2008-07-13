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

import bibliothek.extension.gui.dock.util.Path;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.util.DockProperties;

/**
 * Preference for the shortcut used by the {@link DockFrontend} to hide
 * {@link Dockable}s.
 * @author Benjamin Sigg
 * @see DockFrontend#HIDE_ACCELERATOR
 */
public class KeystrokeHidePreference extends DockPropertyPreference<KeyStroke>{
	/**
	 * Creates a new preference
	 * @param properties to read and write the value of this preference
	 */
	public KeystrokeHidePreference( DockProperties properties ){
		super( properties, DockFrontend.HIDE_ACCELERATOR, Path.TYPE_KEYSTROKE_PATH, new Path( "dock.frontend.INIT_SELECTION" ));
		
		setLabel( DockUI.getDefaultDockUI().getString( "preference.shortcuts.hide_accelerator.label" ));
		setDescription( DockUI.getDefaultDockUI().getString( "preference.shortcuts.hide_accelerator.description" ));
		
		setDefaultValue( KeyStroke.getKeyStroke( KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK  ) );
	}
}
