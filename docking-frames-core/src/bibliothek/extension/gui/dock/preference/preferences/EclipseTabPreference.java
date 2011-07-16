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

import bibliothek.extension.gui.dock.preference.preferences.choice.EclipseTabChoice;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.gui.dock.util.DockProperties;
import bibliothek.util.Path;

/**
 * Determines the way tabs are painted in the {@link EclipseTheme}.
 * @author Benjamin Sigg
 */
public class EclipseTabPreference extends ChoiceDockPropertyPreference<TabPainter>{
	/**
	 * Creates a new preference
	 * @param properties the properties to read from or to write to
	 */
	public EclipseTabPreference( DockProperties properties ){
		super( properties, EclipseTheme.TAB_PAINTER, new Path( "dock.theme.eclipse.tab" ), new EclipseTabChoice( properties ) );
		
		setLabelId( "preference.theme.eclipse.tab.label" );
		setDescriptionId( "preference.theme.eclipse.tab.description" );
	}
}
