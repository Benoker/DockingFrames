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
import javax.swing.border.LineBorder;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.themes.basic.BasicDisplayerFactory;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;

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
		TitleBar bar = theme.getThemeConnector( station.getController() ).getTitleBarKind( dockable );
		DockableDisplayer displayer;
		
		switch( bar ){
		    case NONE:
		        return new NoTitleDisplayer( station, dockable, false, false );
		    case NONE_BORDERED:
		        return new NoTitleDisplayer( station, dockable, true, false );
		    case NONE_HINTED:
		        return new NoTitleDisplayer( station, dockable, false, true );
		    case NONE_HINTED_BORDERED:
		        return new NoTitleDisplayer( station, dockable, true, true );
		    case ECLIPSE:
		        return new EclipseDockableDisplayer(theme, station, dockable);
		    case BASIC_BORDERED:
		        displayer = super.create(station, dockable, title);
                if( displayer.getComponent() instanceof JComponent )
                    ((JComponent)displayer.getComponent()).setBorder( new LineBorder( RexSystemColor.getBorderColor() ) );
                return displayer;
		    case BASIC:
		    default:
		        displayer = super.create(station, dockable, title);
		        if( displayer.getComponent() instanceof JComponent )
                    ((JComponent)displayer.getComponent()).setBorder(null);
		        return displayer;
		}
	}
	
	@Override
	protected BasicDockableDisplayer create( Dockable dockable,
	        DockTitle title, Location location ) {
	    
	    return new BasicDockableDisplayer( dockable, title, location ){
	        @Override
	        public void updateUI() {
	            super.updateUI();
	            setBorder( new LineBorder( RexSystemColor.getBorderColor()) );
	        }
	    };
	}
}
