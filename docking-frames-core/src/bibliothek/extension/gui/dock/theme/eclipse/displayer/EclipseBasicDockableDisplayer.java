/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2009 Benjamin Sigg
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

import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector;
import bibliothek.extension.gui.dock.theme.eclipse.EclipseThemeConnector.TitleBar;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.station.DockableDisplayerListener;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

/**
 * This {@link BasicDockableDisplayer} observes the {@link EclipseThemeConnector}
 * and may discard itself if no longer valid.
 * @author Benjamin Sigg
 */
public class EclipseBasicDockableDisplayer extends BasicDockableDisplayer{
	private TitleBarObserver observer;
	
	public EclipseBasicDockableDisplayer( DockStation station, Dockable dockable, DockTitle title, Location location, TitleBar bar ){
		super( station, dockable, title, location );
		
		observer = new TitleBarObserver( station, dockable, bar ){
			@Override
			protected void invalidated(){
				for( DockableDisplayerListener listener : listeners() ){
					listener.discard( EclipseBasicDockableDisplayer.this );
				}
			}
		};
	}
	
	@Override
	public void setDockable( Dockable dockable ){
		super.setDockable( dockable );
		if( observer != null ){
			observer.setDockable( dockable );
		}
	}
	
	@Override
	public void setController( DockController controller ){
		super.setController( controller );
		if( observer != null ){
			observer.setController( controller );
		}
	}
}
