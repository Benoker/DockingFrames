package tutorial.toolbar.common;

import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JButton;

import tutorial.support.ColorIcon;
import tutorial.support.JTutorialFrame;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.action.CButton;
import bibliothek.gui.dock.toolbar.CToolbarContentArea;
import bibliothek.gui.dock.toolbar.CToolbarItem;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
import bibliothek.gui.dock.toolbar.location.CToolbarAreaLocation;

public class CommonHelloWorld {
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( CommonHelloWorld.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		control.putProperty( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy() );

		CToolbarContentArea area = new CToolbarContentArea( control, "base" );
		control.addStationContainer( area );

		frame.add( area );

		CToolbarAreaLocation location = new CToolbarAreaLocation( area.getEastToolbar() );

		Icon red = new ColorIcon( Color.RED );
		Icon green = new ColorIcon( Color.GREEN );
		Icon blue = new ColorIcon( Color.BLUE );
		
		add( control, "A", red,   location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "B", green, location.group( 0 ).toolbar( 0, 0 ).item( 1 ) );
		add( control, "C", blue,  location.group( 0 ).toolbar( 0, 0 ).item( 2 ) );
		
		add( control, "D", red,   location.group( 0 ).toolbar( 0, 1 ).item( 0 ) );
		add( control, "E", green, location.group( 0 ).toolbar( 0, 1 ).item( 1 ) );
		add( control, "F", blue,  location.group( 0 ).toolbar( 0, 1 ).item( 2 ) );
		
		add( control, "G", red,   location.group( 0 ).toolbar( 0, -1 ).item( 0 ) );
		
		add( control, "H", red,   location.group( 0 ).toolbar( -1, 0 ).item( 0 ) );
		add( control, "I", green, location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		add( control, "J", blue,  location.group( 0 ).toolbar( 0, 0 ).item( 0 ) );
		
		add( control, "K", red,   location.group( 0 ).toolbar( 15, 16 ).item( 18 ) );
		
		add( control, "L", green, location.group( -1 ).toolbar( 0, 0 ).item( 0 ) );

		add( control, "M", blue,  get( control, "L" ).aside() );
		add( control, "N", red,   get( control, "M" ).aside() );
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );
	}

	private static void add( CControl control, String id, Icon icon, CLocation location ){
		String text = id + id.toLowerCase() + id.toLowerCase() + id.toLowerCase(); 
		
		CToolbarItem item = new CToolbarItem( id );
		
		item.setItem( new CButton( null, icon ) );
		item.setItem( new JButton( text, icon ), ExpandedState.STRETCHED );
		
		item.setLocation( location );
		control.addDockable( item );
		item.setVisible( true );
	}
	
	private static CLocation get( CControl control, String id ){
		SingleCDockable dockable = control.getSingleDockable( id );
		return dockable.getBaseLocation();
	}
}
