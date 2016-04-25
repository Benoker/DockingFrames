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
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.GroupedButtonDockAction;
import bibliothek.gui.dock.util.PropertyValue;


/**
 * This {@link DockAction} is mainly used by the {@link ScreenDockStation}
 * to allow it's children to go into fullscreen-mode.
 * @author Benjamin Sigg
 */
public class ScreenFullscreenAction extends GroupedButtonDockAction<Boolean> implements ListeningDockAction {
	private ScreenDockStation screen;
	private DockController controller;

	private DockActionIcon iconNormalize;
	private DockActionIcon iconMaximize;
	
	private DockActionText textNormalize;
	private DockActionText textMaximize;
	private DockActionText textNormalizeTooltip;
	private DockActionText textMaximizeTooltip;
	
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

		textNormalize = new DockActionText( "screen.normalize", this ){
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.TRUE, newValue );	
			}
		};
		textMaximize = new DockActionText( "screen.maximize", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.FALSE, newValue );	
			}
		};
		
		textNormalizeTooltip = new DockActionText( "screen.normalize.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.TRUE, newValue );	
			}
		};
		textMaximizeTooltip = new DockActionText( "screen.maximize.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.FALSE, newValue );	
			}
		};
		
		iconNormalize = new DockActionIcon( "screen.normalize", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( true, newValue );
			}
		};
		iconMaximize = new DockActionIcon( "screen.maximize", this ){
			protected void changed( Icon oldValue, Icon newValue ){
				setIcon( false, newValue );	
			}
		};
	}

	public void setController( DockController controller ) {
		if( this.controller != controller ){
			this.controller = controller;
			accelerator.setProperties( controller );
			
			if( controller == null ){
				iconMaximize.setManager( null );
				iconNormalize.setManager( null );
			}
			else{
				iconMaximize.setManager( controller.getIcons() );
				iconNormalize.setManager( controller.getIcons() );
			}
			textNormalize.setController( controller );
			textNormalizeTooltip.setController( controller );
			textMaximize.setController( controller );
			textMaximizeTooltip.setController( controller );
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
}
