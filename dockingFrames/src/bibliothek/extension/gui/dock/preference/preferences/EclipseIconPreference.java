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

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * Allows to set whether icons are shown on deselected tabs in the {@link EclipseTheme}.
 * @author Benjamin Sigg
 * @see EclipseTheme#PAINT_ICONS_WHEN_DESELECTED
 */
public class EclipseIconPreference extends DockPropertyPreference<Boolean>{
	/**
	 * Creates a new preference.
	 * @param properties the properties to access
	 */
	public EclipseIconPreference( DockProperties properties ){
		super( properties, EclipseTheme.PAINT_ICONS_WHEN_DESELECTED, Path.TYPE_BOOLEAN_PATH, new Path( "dock.theme.eclipse.icons" ));
		
		setLabelId( "preference.theme.eclipse.icon.label" );
		setDescriptionId( "preference.theme.eclipse.icon.description" );
		
		setDefaultValue( Boolean.FALSE );
	}
}
