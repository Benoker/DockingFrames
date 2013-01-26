/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2010 Benjamin Sigg
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
package bibliothek.gui.dock.station.screen;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDesktopPane;
import javax.swing.SwingUtilities;

import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.event.DockStationAdapter;
import bibliothek.gui.dock.event.DockStationListener;

/**
 * This {@link ScreenDockFullscreenStrategy} is based on a {@link JDesktopPane} and assumes that
 * a window is fullscreen if it covers the entire {@link JDesktopPane}. This strategy further assumes that
 * any window is child of that pane.
 * @author Benjamin Sigg
 */
public class InternalFullscreenStrategy implements ScreenDockFullscreenStrategy{
	/** the panel which must be covered */
	private JDesktopPane desktop;
	
	/** whether {@link #desktop} is just resizing */
	private boolean onResize = false;
	
	/** the size {@link #desktop} had when calling code the last time */
	private Dimension lastSize = null;
	
	/** a listener that is added to {@link #desktop} */
	private ComponentListener componentListener = new ComponentAdapter(){
		public void componentResized( ComponentEvent e ){
			if( desktop.isVisible() ){
				if( lastSize == null || lastSize.width == 0 || lastSize.height == 0 ){
					lastSize = desktop.getSize();
				}
				else{
					onResize = true;
					SwingUtilities.invokeLater( new Runnable(){
						public void run() {
							try{
								for( Map.Entry<ScreenDockStation, Dockable[]> entry : dockables.entrySet() ){
									ScreenDockStation station = entry.getKey();
									for( Dockable dockable : entry.getValue() ){
										ScreenDockWindow window = station.getWindow( dockable );
										window.setWindowBounds( new Rectangle( 0, 0, desktop.getWidth(), desktop.getHeight() ) );
									}
									entry.setValue( station.getFullscreenChildren() );
								}
							}
							finally{
								onResize = false;
							}		
						}
					});
				}
			}
		}
	};
	
	/** all the stations that are installed and the dockables they consider to be in fullscreen mode */
	private Map<ScreenDockStation, Dockable[]> dockables = new HashMap<ScreenDockStation, Dockable[]>();
	
	/** a listener added to any station */
	private DockStationListener stationListener = new DockStationAdapter(){
		public void dockableAdded( DockStation station, Dockable dockable ){
			if( !onResize ){
				ScreenDockStation screen = (ScreenDockStation)station;
				if( dockables.containsKey( screen )){
					dockables.put( screen, screen.getFullscreenChildren() );
				}
			}
		}
		
		public void dockableRemoved( DockStation station, Dockable dockable ){
			if( !onResize ){
				ScreenDockStation screen = (ScreenDockStation)station;
				if( dockables.containsKey( screen )){
					dockables.put( screen, screen.getFullscreenChildren() );
				}
			}
		}
	};
	
	/** a listener added to any station */
	private ScreenDockStationListener screenListener = new ScreenDockStationListener(){
		public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ){
			// ignore	
		}
		
		public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ){
			// ignore	
		}
		
		public void fullscreenChanged( ScreenDockStation station, Dockable dockable ){
			if( !onResize ){
				if( dockables.containsKey( station )){
					dockables.put( station, station.getFullscreenChildren() );
				}
			}
		}
	};
	
	/**
	 * Creates a new strategy.
	 * @param desktop the pane which must be covered, this strategy assumes that any window
	 * is a child of this pane.
	 */
	public InternalFullscreenStrategy( JDesktopPane desktop ){
		if( desktop == null ){
			throw new IllegalArgumentException( "desktop must not be null" );
		}
		this.desktop = desktop;
	}
	
	public void install( ScreenDockStation station ){
		boolean empty = dockables.isEmpty();
		
		station.addDockStationListener( stationListener );
		station.addScreenDockStationListener( screenListener );
		dockables.put( station, station.getFullscreenChildren() );
		
		if( empty ){
			desktop.addComponentListener( componentListener );
		}
	}
	
	public void uninstall( ScreenDockStation station ){
		station.removeDockStationListener( stationListener );
		station.removeScreenDockStationListener( screenListener );
		dockables.remove( station );
		
		if( dockables.isEmpty() ){
			desktop.removeComponentListener( componentListener );
		}
	}
	
	public boolean isFullscreen( ScreenDockWindow window ){
		Rectangle bounds = window.getWindowBounds();
		return bounds.x <= 0 && bounds.y <= 0 && bounds.width + bounds.x >= desktop.getWidth() && bounds.height + bounds.y >= desktop.getHeight();
	}

	public void setFullscreen( ScreenDockWindow window, boolean fullscreen ){
		if( fullscreen ){
			window.setNormalBounds( window.getWindowBounds() );
			window.setWindowBounds( new Rectangle( 0, 0, desktop.getWidth(), desktop.getHeight() ) );
		}
		else{
			Rectangle bounds = window.getNormalBounds();
			if( bounds != null ){
				window.setWindowBounds( bounds );
				window.setNormalBounds( null );
			}
		}
	}
}
