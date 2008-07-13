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

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.Preference;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeInitSelectorPreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskNoCombinationPreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskScreenOnlyPreference;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A set or {@link Preference}s for the {@link KeyStroke}s that are used
 * in common.
 * @author Benjamin Sigg
 */
public class CKeyStrokePreferenceModel extends DefaultPreferenceModel{
    /**
     * Creates a new model.
     * @param properties the set of properties to read and write
     */
    public CKeyStrokePreferenceModel( DockProperties properties ){
        add( new KeyStrokeInitSelectorPreference( properties ));
        add( new ModifierMaskNoCombinationPreference( properties ));
        add( new ModifierMaskScreenOnlyPreference( properties ));
        
        add( new KeyStrokeMaximizeChangePreference( properties ) );
        add( new KeyStrokeMaximizePreference( properties ));
        add( new KeyStrokeNormalizePreference( properties ));
        add( new KeyStrokeMinimizePreference( properties ));
        add( new KeyStrokeExternalizePreference( properties ));
    }
}
