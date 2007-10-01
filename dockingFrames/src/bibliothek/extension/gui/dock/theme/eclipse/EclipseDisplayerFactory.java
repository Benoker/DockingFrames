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

import javax.swing.JComponent;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockableDisplayer;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.extension.gui.dock.theme.EclipseTheme;

/**
 * @author Janni Kovacs
 */
public class EclipseDisplayerFactory extends BasicDisplayerFactory {
	private EclipseTheme theme;

	public EclipseDisplayerFactory(EclipseTheme theme) {
		this.theme = theme;
	}

	@Override
	public DockableDisplayer create(DockStation station, Dockable dockable, DockTitle title) {
		DockableDisplayer displayer;
		if (dockable.asDockStation() == null) {
			if (theme.getThemeConnector( station.getController() ).isTitleBarShown(dockable)) {
				displayer = new EclipseDockableDisplayer(theme, station, dockable);
			} else {
				displayer = new NoTitleDisplayer(station, dockable);
			}
		} else {
			displayer = super.create(station, dockable, title);
			
			if( displayer.getComponent() instanceof JComponent )
	            ((JComponent)displayer.getComponent()).setBorder(null);
		}
		
		return displayer;
	}
}
