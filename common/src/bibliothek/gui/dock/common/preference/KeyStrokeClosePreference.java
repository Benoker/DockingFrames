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
package bibliothek.gui.dock.common.preference;

import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.preferences.DockPropertyPreference;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.intern.CDockable;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * {@link KeyStroke} for closing a {@link CDockable}.
 * @author Benjamin Sigg
 */
public class KeyStrokeClosePreference extends DockPropertyPreference<KeyStroke>{
    /**
     * Creates a new preference
     * @param properties the properties to access
     */
    public KeyStrokeClosePreference( DockProperties properties ){
        super( properties, CControl.KEY_CLOSE, Path.TYPE_KEYSTROKE_PATH, new Path( "dock.common.control.close" ) );
        
        setLabelId( "preference.shortcut.close.label" );
        setDescriptionId( "preference.shortcut.close.description" );
        
        setDefaultValue( KeyStroke.getKeyStroke( KeyEvent.VK_F4, KeyEvent.CTRL_DOWN_MASK ));
    }
}
