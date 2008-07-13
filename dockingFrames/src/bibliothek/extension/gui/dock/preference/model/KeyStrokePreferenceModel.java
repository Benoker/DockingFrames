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
package bibliothek.extension.gui.dock.preference.model;

import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeHidePreference;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeInitSelectorPreference;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeMaximizePreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskNoCombinationPreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskScreenOnlyPreference;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A model that lists the keystrokes which are used in the framework.
 * @author Benjamin Sigg
 */
public class KeyStrokePreferenceModel extends DefaultPreferenceModel{
    /**
     * Creates a new model.
     * @param properties the properties to read and write from
     */
    public KeyStrokePreferenceModel( DockProperties properties ){
        if( properties == null )
            throw new IllegalArgumentException( "properties must not be null" );
        
        add( new KeyStrokeInitSelectorPreference( properties ));
        add( new KeyStrokeMaximizePreference( properties ));
        add( new KeyStrokeHidePreference( properties ));
        add( new ModifierMaskNoCombinationPreference( properties ));
        add( new ModifierMaskScreenOnlyPreference( properties ));
    }
}
