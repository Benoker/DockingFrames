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

import javax.swing.KeyStroke;

import bibliothek.extension.gui.dock.preference.DefaultPreferenceModel;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeHidePreference;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeInitSelectorPreference;
import bibliothek.extension.gui.dock.preference.preferences.KeyStrokeMaximizePreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskNoCombinationPreference;
import bibliothek.extension.gui.dock.preference.preferences.ModifierMaskScreenOnlyPreference;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.control.ModifierMask;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A model that lists the keystrokes which are used in the framework.
 * @author Benjamin Sigg
 */
public class KeyStrokePreferenceModel extends DefaultPreferenceModel{
	private KeyStrokeInitSelectorPreference initSelector;
	private KeyStrokeMaximizePreference maximize;
	private KeyStrokeHidePreference hide;
	private ModifierMaskNoCombinationPreference maskNoCombination;
	private ModifierMaskScreenOnlyPreference maskScreenOnly;
	
    /**
     * Creates a new model.
     * @param properties the properties to read and write from
     */
    public KeyStrokePreferenceModel( DockProperties properties ){
    	super( properties.getController() );
    	
        add( initSelector = new KeyStrokeInitSelectorPreference( properties ));
        add( maximize = new KeyStrokeMaximizePreference( properties ));
        add( hide = new KeyStrokeHidePreference( properties ));
        add( maskNoCombination = new ModifierMaskNoCombinationPreference( properties ));
        add( maskScreenOnly = new ModifierMaskScreenOnlyPreference( properties ));
    }
    

    /**
     * Gets access to the preference that represents the {@link KeyStroke} which opens the 
     * selection popup.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeInitSelectorPreference getInitSelector(){
		return initSelector;
	}
    
    /**
     * Gets access to the preference that represents the {@link ModifierMask} that ensures
     * that {@link Dockable}s are not merged during drag and drop. 
     * @return the preference, not <code>null</code>
     */
    public ModifierMaskNoCombinationPreference getMaskNoCombination(){
		return maskNoCombination;
	}
    
    /**
     * Gets access to the preference that represents the {@link ModifierMask} which ensures
     * that {@link Dockable}s are dropped onto a {@link ScreenDockStation} during drag and drop.
     * @return the preference, not <code>null</code>
     */
    public ModifierMaskScreenOnlyPreference getMaskScreenOnly(){
		return maskScreenOnly;
	}
    
    /**
     * Gets access to the preference that represents the {@link KeyStroke} which lets 
     * maximize a {@link Dockable}.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeMaximizePreference getMaximize(){
		return maximize;
	}
    
    /**
     * Gets access to the preference that represents the {@link KeyStroke} which closes
     * a {@link Dockable}. 
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeHidePreference getHide(){
		return hide;
	}
}
