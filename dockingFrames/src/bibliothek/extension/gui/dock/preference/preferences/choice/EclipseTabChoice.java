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
package bibliothek.extension.gui.dock.preference.preferences.choice;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.ArchGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.DockTitleTab;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.RectGradientPainter;
import bibliothek.extension.gui.dock.theme.eclipse.stack.tab.TabPainter;
import bibliothek.gui.dock.util.DockProperties;

/**
 * The way tabs are painted in the {@link EclipseTheme}
 * @author Benjamin Sigg
 */
public class EclipseTabChoice extends DefaultChoice<TabPainter>{
	/**
	 * Creates a new choice.
	 * @param properties default settings
	 */
	public EclipseTabChoice( DockProperties properties ){
		super( properties.getController() );
		
		setDefaultChoice( "round" );
		
		addLinked( "title", "preference.theme.eclipse.tab.choice.title", DockTitleTab.FACTORY );
		addLinked( "rect", "preference.theme.eclipse.tab.choice.rect", RectGradientPainter.FACTORY );
		addLinked( "round", "preference.theme.eclipse.tab.choice.round", ArchGradientPainter.FACTORY );
	}
}
