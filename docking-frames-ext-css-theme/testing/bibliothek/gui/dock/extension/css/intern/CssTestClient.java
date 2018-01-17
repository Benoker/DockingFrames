package bibliothek.gui.dock.extension.css.intern;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.extension.css.CssTheme;
import bibliothek.gui.dock.station.split.SplitDockGrid;

public class CssTestClient {
	public static void main( String[] args ) throws IOException{
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		
		CssTheme theme = new CssTheme();
		theme.read( new File( "test/default.css" ) );
		controller.setTheme( theme );
		
		SplitDockStation center = new SplitDockStation();
		controller.add( center );
		frame.add( center, BorderLayout.CENTER );
		
		DefaultDockable dockableA = new DefaultDockable( "Aaaa" );
		DefaultDockable dockableB = new DefaultDockable( "Bbbb" );
		DefaultDockable dockableC = new DefaultDockable( "Cccc" );
		DefaultDockable dockableD = new DefaultDockable( "Dddd" );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, dockableA, dockableB );
		grid.addDockable( 1, 0, 1, 0.5, dockableC );
		grid.addDockable( 1, 0.5, 1, 0.5, dockableD );
		center.dropTree( grid.toTree() );
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );
	}
}
