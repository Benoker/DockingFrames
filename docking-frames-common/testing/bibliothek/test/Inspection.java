package bibliothek.test;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import bibliothek.gui.dock.common.CControl;

public class Inspection {	
	public static void open( CControl control ){
		open( control, new PlaceholderInspection() );
	}
	
	private static void open( Object root, InspectionGraph graph ){
		JFrame frame = new JFrame( "Inspection" );
		frame.add( new InspectionPanel( root, graph ), BorderLayout.CENTER );
		frame.setBounds( 400, 400, 600, 400 );
		frame.setVisible( true );
	}
}
