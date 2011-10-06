import java.awt.Window;

import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.station.toolbar.Position;
import bibliothek.gui.dock.util.DirectWindowProvider;

/**
 * You will see that program test work. But, some exceptions has launched as:
 * "java.lang.IllegalStateException: the parent of 'ComponentDockable@148cc8c'
 * is not 'ToolbarContainerDockStation@1815859' but
 * 'ToolbarGroupDockStation@fe748f'" You can generate this exception in a simple
 * way: launch the program and drag and drop the button "Five" in the north area
 * It seems that the problem comes from line as: dockable.setDockParent(this); e.g.
 * in method add(...) from ToolbarContainerDockStation. I have to address this issue.
 * 
 * */
public class TestToolbarDockStation {

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		JFrame frame = new JFrame();

		DockController controller = new DockController();

		DirectWindowProvider windowProvider = new DirectWindowProvider();
		windowProvider.setWindow( frame );
		ScreenDockStation screenStation = new ScreenDockStation( windowProvider );
		screenStation.setShowing( true );
		controller.add( screenStation );

		ToolbarContainerDockStation toolbarStation = new ToolbarContainerDockStation();
		controller.add( toolbarStation );

		ToolbarGroupDockStation group1 = new ToolbarGroupDockStation();
		ComponentDockable dockable1 = new ComponentDockable( new JButton( "One" ) );
		group1.drop( dockable1 );
		ComponentDockable dockable2 = new ComponentDockable( new JButton( "Two" ) );
		group1.drop( dockable2 );
		ComponentDockable dockable3 = new ComponentDockable( new JButton( "Three" ) );
		group1.drop( dockable3 );
		ComponentDockable dockable4 = new ComponentDockable( new JButton( "Four" ) );
		group1.drop( dockable4 );

		ToolbarDockStation toolbar1 = new ToolbarDockStation();
		toolbar1.drop( group1 );
		toolbarStation.drop( toolbar1, Position.NORTH );

		ToolbarGroupDockStation group2 = new ToolbarGroupDockStation();
		ComponentDockable dockable5 = new ComponentDockable( new JButton( "Five" ) );
		group2.drop( dockable5 );

		ToolbarDockStation toolbar2 = new ToolbarDockStation();
		toolbar2.drop( group2 );
		toolbarStation.drop( toolbar2, Position.WEST );

		frame.add( toolbarStation.getComponent() );

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );
		//	screenStation.setShowing(true);

	}

}