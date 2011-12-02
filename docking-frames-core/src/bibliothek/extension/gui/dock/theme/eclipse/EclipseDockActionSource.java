/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2007 Benjamin Sigg
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
package bibliothek.extension.gui.dock.theme.eclipse;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.FilteredDockActionSource;

/**
 * A list of {@link DockAction DockActions} filtered by the 
 * {@link EclipseThemeConnector}, using {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)}.
 * @author Benjamin Sigg
 *
 */
public class EclipseDockActionSource extends FilteredDockActionSource {
	/** the theme for which this source is used */
	private EclipseTheme theme;
	/** the Dockable for which actions are filtered */
	private Dockable dockable;
	/** the expected result of {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)} */
	private boolean tab;
	
	/**
	 * Creates a new source
	 * @param theme the theme for which this source is used
	 * @param source the source which is filtered
	 * @param dockable the Dockable for which the actions are shown
	 * @param tab the expected result of {@link EclipseThemeConnector#isTabAction(Dockable, DockAction)}
	 */
	public EclipseDockActionSource( EclipseTheme theme, DockActionSource source, Dockable dockable, boolean tab ){
		super( source );
		this.theme = theme;
		this.dockable = dockable;
		this.tab = tab;
	}
	
	@Override
	protected boolean include( DockAction action ){
		return theme.getThemeConnector( dockable.getController() ).isTabAction( dockable, action ) == tab;
	}
}
