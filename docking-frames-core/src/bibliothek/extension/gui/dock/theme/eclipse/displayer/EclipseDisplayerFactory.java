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
package bibliothek.extension.gui.dock.theme.eclipse.displayer;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;

import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.extension.gui.dock.theme.eclipse.rex.RexSystemColor;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.DockableDisplayer.Location;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This factory makes use of a {@link EclipseThemeConnector} to decide which
 * kind of {@link DockableDisplayer} to create. To be more exact: the displayer
 * depends on the {@link TitleBar}-value returned by {@link EclipseThemeConnector#getTitleBarKind(DockStation, Dockable)}.
 * @author Janni Kovacs
 */
public class EclipseDisplayerFactory implements DisplayerFactory {
	private EclipseTheme theme;

	/**
	 * Creates a new displayer factory.
	 * @param theme the owner of this factory, not <code>null</code>
	 */
	public EclipseDisplayerFactory( EclipseTheme theme ) {
		this.theme = theme;
	}

	public void request( DisplayerRequest request ){
		Dockable dockable = request.getTarget();
    	DockStation station = request.getParent();
    	DockTitle title = request.getTitle();
    	
    	DockableDisplayer displayer = create( station, dockable, title );
    	
    	if( displayer != null ){
    		request.answer( displayer );
    	}
	}
	
	/**
	 * Creates a new {@link DockableDisplayer} for <code>dockable</code>.
	 * @param station the station which will show the displayer
	 * @param dockable the element which will be shown in the displayer
	 * @param title the title of <code>dockable</code>
	 * @return the new displayer or <code>null</code>
	 */
	protected DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ) {
		TitleBar bar = theme.getThemeConnector( station.getController() ).getTitleBarKind( station, dockable );
		
		switch( bar ){
		    case NONE:
		    case NONE_BORDERED:
		    case NONE_HINTED:
		    case NONE_HINTED_BORDERED:
		        return new NoTitleBarDisplayer( station, dockable, title, bar );
		    case ECLIPSE:
		        return new EclipseDockableDisplayer( theme, station, dockable, title );
		    case BASIC_BORDERED:
		        return create( station, dockable, title, true, bar );
		    case BASIC:
		    default:
		    	return create( station, dockable, title, false, bar );
		}
	}
	
	/**
	 * Creates a new displayer.
	 * @param station the parent of the displayer
	 * @param dockable the content, may be <code>null</code>
	 * @param title the title to show, may be <code>null</code>
	 * @param border whether to show a border
	 * @param bar what kind of titlebar the displayer should use 
	 * @return the new displayer
	 */
	protected BasicDockableDisplayer create( DockStation station, Dockable dockable, DockTitle title, boolean border, TitleBar bar ) {
		Location location = Location.TOP;
		if( dockable.asDockStation() != null ){
			location = Location.LEFT;
		}
		
	    EclipseBasicDockableDisplayer displayer;
		if( border ){
		    displayer = new EclipseBasicDockableDisplayer( station, dockable, title, location, bar ){
		        @Override
		        protected Border getDefaultBorder(){
			        return new LineBorder( RexSystemColor.getBorderColor());
		        }
		    };
		}
		else{
			displayer = new EclipseBasicDockableDisplayer( station, dockable, title, location, bar ){
				@Override
				protected Border getDefaultBorder(){
					return null;
				}
			};
		}
		
		return displayer;
	}
}
