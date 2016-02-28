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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.control.ModifierMask;
import bibliothek.gui.dock.util.DockProperties;

/**
 * A set or {@link Preference}s for the {@link KeyStroke}s that are used
 * in common.
 * @author Benjamin Sigg
 */
public class CKeyStrokePreferenceModel extends DefaultPreferenceModel{
	private KeyStrokeInitSelectorPreference initSelector;
	private ModifierMaskNoCombinationPreference maskNoCombination;
	private ModifierMaskScreenOnlyPreference maskScreenOnly;
    
	private KeyStrokeMaximizeChangePreference maximizeChange;
	private KeyStrokeMaximizePreference maximize;
	private KeyStrokeNormalizePreference normalize;
	private KeyStrokeMinimizePreference minimize;
	private KeyStrokeExternalizePreference externalize;
	private KeyStrokeCancelOperation cancel;
	
    /**
     * Creates a new model.
     * @param properties the set of properties to read and write
     */
    public CKeyStrokePreferenceModel( DockProperties properties ){
    	super( properties.getController() );
    	
        add( initSelector = new KeyStrokeInitSelectorPreference( properties ));
        add( maskNoCombination = new ModifierMaskNoCombinationPreference( properties ));
        add( maskScreenOnly = new ModifierMaskScreenOnlyPreference( properties ));
        
        add( maximizeChange = new KeyStrokeMaximizeChangePreference( properties ) );
        add( maximize = new KeyStrokeMaximizePreference( properties ));
        add( normalize = new KeyStrokeNormalizePreference( properties ));
        add( minimize = new KeyStrokeMinimizePreference( properties ));
        add( externalize = new KeyStrokeExternalizePreference( properties ));
        add( cancel = new KeyStrokeCancelOperation( properties ));
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
     * Gets access to the preference that represents the {@link KeyStroke} which switches between
     * maximized and normalized {@link Dockable}.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeMaximizeChangePreference getMaximizeChange(){
		return maximizeChange;
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
     * Gets access to the preference that represents the {@link KeyStroke} which lets 
     * normalize a {@link Dockable}.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeNormalizePreference getNormalize(){
		return normalize;
	}
    
    /**
     * Gets access to the preference that represents the {@link KeyStroke} which lets 
     * minimize a {@link Dockable}.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeMinimizePreference getMinimize(){
		return minimize;
	}
    
    /**
     * Gets access to the preference that represents the {@link KeyStroke} which lets 
     * externalize a {@link Dockable}.
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeExternalizePreference getExternalize(){
		return externalize;
	}
    
    /**
     * Gets access to the preference that represents the {@link KeyStroke} which cancels
     * the current drag and drop operation
     * @return the preference, not <code>null</code>
     */
    public KeyStrokeCancelOperation getCancel() {
		return cancel;
	}
}
