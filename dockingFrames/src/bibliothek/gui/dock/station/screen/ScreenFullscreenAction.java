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

import javax.swing.Icon;
import javax.swing.KeyStroke;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.DockUI;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.event.IconManagerListener;
import bibliothek.gui.dock.util.IconManager;
import bibliothek.gui.dock.util.PropertyValue;


/**
 * This {@link DockAction} is mainly used by the {@link ScreenDockStation}
 * to allow it's children to go into fullscreen-mode.
 * @author Benjamin Sigg
 */
public class ScreenFullscreenAction extends GroupedButtonDockAction<Boolean> implements ListeningDockAction {
	private ScreenDockStation screen;
	private DockController controller;
	private Listener listener = new Listener();

	private PropertyValue<KeyStroke> accelerator = new PropertyValue<KeyStroke>( SplitDockStation.MAXIMIZE_ACCELERATOR ){
		@Override
		protected void valueChanged( KeyStroke oldValue, KeyStroke newValue ){
			setAccelerator( Boolean.TRUE, newValue );
			setAccelerator( Boolean.FALSE, newValue );
		}
	};

	/**
	 * Constructs the action and sets the <code>station</code> on
	 * which the {@link Dockable Dockables} will be made fullscreen.
	 * @param station the station
	 */
	public ScreenFullscreenAction( ScreenDockStation station ){
		super( null );

		screen = station;
		setRemoveEmptyGroups( false );

		station.addScreenDockStationListener( new ScreenDockStationListener() {
			public void windowRegistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
				// ignore
			}
			
			public void windowDeregistering( ScreenDockStation station, Dockable dockable, ScreenDockWindow window ) {
				// ignore
			}
			
			public void fullscreenChanged( ScreenDockStation station, Dockable dockable ) {
				change( dockable, station.isFullscreen( dockable ) );
			}
		});
		
		setText( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "screen.normalize" ) );
		setText( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "screen.maximize" ) );

		setTooltip( Boolean.TRUE, DockUI.getDefaultDockUI().getString( "screen.normalize.tooltip" ));
		setTooltip( Boolean.FALSE, DockUI.getDefaultDockUI().getString( "screen.maximize.tooltip" ));
	}

	public void setController( DockController controller ) {
		if( this.controller != controller ){
			if( this.controller != null ){
				this.controller.getIcons().remove( "screen.normalize", listener );
				this.controller.getIcons().remove( "screen.maximize", listener );
			}

			this.controller = controller;
			accelerator.setProperties( controller );

			if( controller != null ){
				IconManager icons = controller.getIcons();
				icons.add( "screen.normalize", listener );
				icons.add( "screen.maximize", listener );
				setIcon( true, icons.getIcon( "screen.normalize" ));
				setIcon( false, icons.getIcon( "screen.maximize" ));
			}
		}
	}

	public void action( Dockable dockable ) {
		while( dockable.getDockParent() != screen ){
			DockStation station = dockable.getDockParent();
			if( station == null )
				return;

			dockable = station.asDockable();
			if( dockable == null )
				return;
		}

		boolean state = screen.isFullscreen( dockable );
		screen.setFullscreen( dockable, !state );
	}

	private void change( Dockable dockable, Boolean value ){
		if( isKnown( dockable ))
			setGroup( value, dockable );

		DockStation station = dockable.asDockStation();
		if( station != null ){
			for( int i = 0, n = station.getDockableCount(); i<n; i++ )
				change( station.getDockable(i), value );
		}
	}

	@Override
	protected Boolean createGroupKey( Dockable dockable ){
		while( dockable.getDockParent() != screen ){
			DockStation station = dockable.getDockParent();
			if( station == null )
				return Boolean.FALSE;

			dockable = station.asDockable();
			if( dockable == null )
				return Boolean.FALSE;
		}

		return screen.isFullscreen( dockable );
	}

	/**
	 * A listener to the set of icons
	 * @author Benjamin Sigg
	 */
	 private class Listener implements IconManagerListener{
		public void iconChanged( String key, Icon icon ) {
			if( key.equals( "screen.normalize" ))
				setIcon( true, icon );
			else
				setIcon( false, icon );
		}
	 }
}
