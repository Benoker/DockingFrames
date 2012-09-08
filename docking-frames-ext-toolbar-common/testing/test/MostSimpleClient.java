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

import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;

public class MostSimpleClient {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		CControl control = new CControl( frame );
		control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy());
		// control.putProperty( AbstractToolbarDockStation.ON_CONFLICT_ENABLE, false );
		
		CToolbarContentArea area = new CToolbarContentArea( control, "base" );
		control.addStationContainer( area );

		frame.add( area );

		CToolbarAreaLocation location = new CToolbarAreaLocation( area.getEastToolbar() );

		add( control, "A", location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "B", location.group( 0 ).toolbar( 0, 0 ).item( 1 ) );
		add( control, "C", location.group( 0 ).toolbar( 0, 0 ).item( 2 ) );
		
		add( control, "D", location.group( 0 ).toolbar( 0, 1 ).item( 0 ) );
		add( control, "E", location.group( 0 ).toolbar( 0, 1 ).item( 1 ) );
		add( control, "F", location.group( 0 ).toolbar( 0, 1 ).item( 2 ) );
		
		add( control, "G", location.group( 0 ).toolbar( 0, -1 ).item( 0 ) );
		
		add( control, "H", location.group( 0 ).toolbar( -1, 0 ).item( 0 ) );
		add( control, "I", location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "J", location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		
		add( control, "K", location.group( 0 ).toolbar( 15, 16 ).item( 18 ) );
		
		add( control, "L", location.group( -1 ).toolbar( 0, 0 ).item( 0 ) );

		add( control, "M", get( control, "L" ).aside() );
		add( control, "N", get( control, "M" ).aside() );
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );
	}

	private static void add( CControl control, String id, CLocation location ){
		CToolbarItem item = new CToolbarItem( id );
		item.setItem( new JButton( id ), ExpandedState.SHRUNK );
		if( !("A".equals( id ) || "B".equals( id ) || "C".equals( id ))){
			item.setItem( new JButton( id + id + id ), ExpandedState.STRETCHED );	
		}		
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}
	
	private static CLocation get( CControl control, String id ){
		SingleCDockable dockable = control.getSingleDockable( id );
		return dockable.getBaseLocation();
	}
}
