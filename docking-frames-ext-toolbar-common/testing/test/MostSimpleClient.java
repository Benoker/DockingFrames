package test;

import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;

public class MostSimpleClient {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		CControl control = new CControl( frame );

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
		item.intern().setComponent( new JButton( id ), ExpandedState.SHRUNK );
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}
	
	private static CLocation get( CControl control, String id ){
		SingleCDockable dockable = control.getSingleDockable( id );
		return dockable.getBaseLocation();
	}
}
