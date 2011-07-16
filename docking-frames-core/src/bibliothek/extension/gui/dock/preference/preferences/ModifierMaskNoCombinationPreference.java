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

import bibliothek.gui.Dockable;
import bibliothek.gui.dock.control.DockRelocatorMode;
import bibliothek.gui.dock.control.ModifierMask;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * Preference for the mask that prevents combinations when moving
 * a {@link Dockable}.
 * @author Benjamin Sigg
 * @see DockRelocatorMode#NO_COMBINATION_MASK
 */
public class ModifierMaskNoCombinationPreference extends DockPropertyPreference<ModifierMask>{
	/**
	 * Creates a new preference
	 * @param properties to read and write the value of this preference
	 */
	public ModifierMaskNoCombinationPreference( DockProperties properties ){
		super( properties, DockRelocatorMode.NO_COMBINATION_MASK, Path.TYPE_MODIFIER_MASK_PATH, new Path( "dock.DockRelocatorMode.NO_COMBINATION_MASK" ) );
		
		setLabelId( "preference.shortcuts.no_combination_mask.label" );
		setDescriptionId( "preference.shortcuts.no_combination_mask.description" );
		
		setDefaultValue( DockRelocatorMode.NO_COMBINATION_MASK.getDefault( null ) );
	}
}
