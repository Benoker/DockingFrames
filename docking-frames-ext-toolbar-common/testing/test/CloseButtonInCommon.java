/*
 * Bibliothek - DockingFrames
 * Library built on Java/Swing, allows the user to "drag and drop"
 * panels containing any Swing-Component the developer likes to add.
 * 
 * Copyright (C) 2012 Herve Guillaume, Benjamin Sigg
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
 * Herve Guillaume
 * rvguillaume@hotmail.com
 * FR - France
 *
 * Benjamin Sigg
 * benjamin_sigg@gmx.ch
 * CH - Switzerland
 */

package test;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.action.ActionContentModifier;
import bibliothek.gui.dock.action.ActionGuard;
import bibliothek.gui.dock.action.DefaultDockActionSource;
import bibliothek.gui.dock.action.DockActionSource;
import bibliothek.gui.dock.action.LocationHint;
import bibliothek.gui.dock.action.actions.SimpleButtonAction;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.frontend.FrontendEntry;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;
import bibliothek.gui.dock.util.DockUtilities;
import bibliothek.gui.dock.util.DockUtilities.DockVisitor;

public class CloseButtonInCommon {
	public static void main( String[] args ){
		JFrame frame = new JFrame();

		final CControl control = new CControl( frame );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );

		control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy(){
			public boolean isEnabled( Dockable item, ExpandedState state ){
				return super.isEnabled( item, state ) && state != ExpandedState.EXPANDED;
			}
		} );
		control.getController().addActionGuard( new ToolbarGroupClosing( control ) );

		CToolbarContentArea area = new CToolbarContentArea( control, "root" );
		control.addStationContainer( area );
		frame.add( area );

		CToolbarAreaLocation location = new CToolbarAreaLocation( area.getEastToolbar() );

		add( control, "A", location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "B", location.group( 0 ).toolbar( 0, 0 ).item( 1 ) );
		add( control, "C", location.group( 0 ).toolbar( 0, 0 ).item( 2 ) );
		add( control, "D", location.group( 0 ).toolbar( 0, 0 ).item( 3 ) );

		JMenuItem item = new JMenuItem( "Open all" );
		item.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed( ActionEvent e ){
				for( SingleCDockable dockable : control.getRegister().getSingleDockables() ) {
					dockable.setVisible( true );
				}
			}
		} );

		JMenu menu = new JMenu( "Docking" );
		menu.add( item );
		JMenuBar menubar = new JMenuBar();
		menubar.add( menu );
		frame.setJMenuBar( menubar );

		frame.setVisible( true );
	}

	private static void add( CControl control, String id, CLocation location ){
		CToolbarItem item = new CToolbarItem( id );
		item.intern().setComponent( new JButton( id ), ExpandedState.SHRUNK );
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}

	public static class CloseIcon implements Icon{
		private Color color;
		
		public CloseIcon( Color color ){
			this.color = color;
		}
		
		public int getIconWidth(){
			return 8;
		}
		
		public int getIconHeight(){
			return 8;
		}
		
		@Override
		public void paintIcon( Component c, Graphics g, int x, int y ){
			g.setColor( color );
			g.drawLine( x+2, y+2, x+7, y+7 );
			g.drawLine( x+3, y+2, x+7, y+6 );
			g.drawLine( x+2, y+3, x+6, y+7 );
			
			g.drawLine( x+2, y+7, x+7, y+2 );
			g.drawLine( x+3, y+7, x+6, y+2 );
			g.drawLine( x+2, y+6, x+7, y+3 );
		}
	}
	
	public static class ToolbarGroupClosing extends SimpleButtonAction implements ActionGuard {
		private CControl control;

		public ToolbarGroupClosing( CControl control ){
			this.control = control;
			
			setText( "Close" );
			setTooltip( "Close this toolbar" );
			setIcon( ActionContentModifier.NONE_HOVER, new CloseIcon( Color.RED ) );
			setIcon( new CloseIcon( Color.WHITE ) );
		}

		@Override
		public void action( Dockable dockable ){
			control.getLocationManager().store( dockable );

			DockUtilities.visit( dockable, new DockVisitor(){
				@Override
				public void handleDockable( Dockable dockable ){
					DockFrontend frontend = control.intern();
					FrontendEntry entry = frontend.getFrontendEntry( dockable );
					if( entry != null ) {
						entry.updateLocation();
					}
				}
			} );
			DockStation parent = dockable.getDockParent();
			if( parent != null ) {
				parent.drag( dockable );
			}
		}

		@Override
		public boolean react( Dockable dockable ){
			return dockable instanceof ToolbarGroupDockStation;
		}

		@Override
		public DockActionSource getSource( Dockable dockable ){
			return new DefaultDockActionSource( new LocationHint( LocationHint.ACTION_GUARD, LocationHint.RIGHT_OF_ALL ), this );
		}
	}
}
