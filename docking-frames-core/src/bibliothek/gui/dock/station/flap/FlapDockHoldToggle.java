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

package bibliothek.gui.dock.station.flap;

import javax.swing.Icon;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.action.DockAction;
import bibliothek.gui.dock.action.DockActionIcon;
import bibliothek.gui.dock.action.DockActionText;
import bibliothek.gui.dock.action.ListeningDockAction;
import bibliothek.gui.dock.action.actions.GroupedSelectableDockAction;
import bibliothek.gui.dock.event.FlapDockListener;
import bibliothek.gui.dock.station.flap.button.ButtonContentAction;

/**
 * This {@link DockAction} is shown together with the children of a 
 * {@link FlapDockStation}, and allows to "stick" a {@link Dockable}.
 * When a {@link Dockable} is sticked, it will not be disposed by the
 * {@link FlapDockStation}, when it looses the focus. In fact, this 
 * action just uses the {@link FlapDockStation#setHold(Dockable, boolean) setHold}-method
 * of the station.
 * @author Benjamin Sigg
 */
@ButtonContentAction
public class FlapDockHoldToggle extends GroupedSelectableDockAction.Check<Boolean> implements ListeningDockAction {
	private FlapDockStation flap;
	private DockController controller;

	private Listener[] icons;
	private DockActionText textStick;
	private DockActionText textStickTooltip;
	private DockActionText textFree;
	private DockActionText textFreeTooltip;

	/**
	 * Constructor, sets the icons and makes the action ready to be shown.
	 * @param station The station on which the {@link Dockable Dockables}
	 * are registered.
	 */
	public FlapDockHoldToggle( FlapDockStation station ){
		super( null );
		setRemoveEmptyGroups( false );
		flap = station;

		icons = new Listener[]{ new Listener( "flap.hold" ), new Listener( "flap.free" ) };

		station.addFlapDockStationListener( new FlapDockListener(){
			public void holdChanged( FlapDockStation station, Dockable dockable, boolean hold ){
				if( isBound( dockable ) )
					setGroup( hold, dockable );
			}
		} );

		setSelected( Boolean.FALSE, false );
		setSelected( Boolean.TRUE, true );

		textStick = new DockActionText( "flap.stick.true", this ){
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.TRUE, newValue );
			}
		};
		textFree = new DockActionText( "flap.stick.false", this ){
			protected void changed( String oldValue, String newValue ){
				setText( Boolean.FALSE, newValue );
			}
		};

		textStickTooltip = new DockActionText( "flap.stick.true.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.TRUE, newValue );
			}
		};
		textFreeTooltip = new DockActionText( "flap.stick.false.tooltip", this ){
			protected void changed( String oldValue, String newValue ){
				setTooltip( Boolean.FALSE, newValue );
			}
		};
	}

	@Override
	public boolean trigger( Dockable dockable ){
		setSelected( dockable, !isSelected( dockable ) );
		return true;
	}

	@Override
	public void setSelected( Dockable dockable, boolean selected ){
		flap.setHold( dockable, selected );
		setGroup( selected, dockable );
	}

	@Override
	protected Boolean createGroupKey( Dockable dockable ){
		return flap.isHold( dockable );
	}

	public void setController( DockController controller ){
		if( this.controller != controller ) {
			this.controller = controller;

			for( Listener icon : icons ) {
				icon.setController( controller );
			}
			textStick.setController( controller );
			textFree.setController( controller );
			textStickTooltip.setController( controller );
			textFreeTooltip.setController( controller );
		}
	}

	/**
	 * A listener changing the icon of this action when necessary
	 * @author Benjamin Sigg
	 *
	 */
	private class Listener extends DockActionIcon {
		public Listener( String id ){
			super( id, FlapDockHoldToggle.this );
		}

		@Override
		protected void changed( Icon oldValue, Icon icon ){
			String key = getId();

			if( key.equals( "flap.free" ) )
				setIcon( false, icon );
			else
				setSelectedIcon( true, icon );
		}
	}
}
