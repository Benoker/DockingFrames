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
import bibliothek.extension.gui.dock.preference.preferences.EclipseIconPreference;
import bibliothek.extension.gui.dock.preference.preferences.EclipseTabPreference;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.dock.util.DockProperties;

/**
 * Various preferences used by the {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class EclipseThemePreferenceModel extends DefaultPreferenceModel{
	private EclipseTabPreference tab;
	private EclipseIconPreference icon;
	
	/**
	 * Creates a new model
	 * @param properties the properties to read or write
	 */
	public EclipseThemePreferenceModel( DockProperties properties ){
		super( properties.getController() );
		add( tab = new EclipseTabPreference( properties ));
		add( icon = new EclipseIconPreference( properties ));
	}
	
	/**
	 * Grants access to the preference that represents the look of tabs.
	 * @return the preference, not <code>null</code>
	 */
	public EclipseTabPreference getTab(){
		return tab;
	}
	
	/**
	 * Grants access to the preference that tells whether an icon is painted
	 * on unselected tabs or not. 
	 * @return the icon preference, not <code>null</code>
	 */
	public EclipseIconPreference getIcon(){
		return icon;
	}
}
