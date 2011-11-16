import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;


public class TestToolbarDockAndStack {

	/**
	 * @param args
	 */
	public static void main( String[] args ){

		DockController controller = new DockController();
		
		JPanel pane = new JPanel(new BorderLayout());

		/**
		 * Create a ToolbarContainerDockStation
		 * */
		ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation(Orientation.VERTICAL);
		pane.add(toolbarStationWest.getComponent(), BorderLayout.WEST);
		ToolbarContainerDockStation toolbarStationNorth = new ToolbarContainerDockStation(Orientation.HORIZONTAL);
		pane.add(toolbarStationNorth.getComponent(), BorderLayout.NORTH);
		controller.add( toolbarStationWest );
		controller.add( toolbarStationNorth );

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
		toolbarStationWest.drop( toolbar1 );

		ToolbarGroupDockStation group2 = new ToolbarGroupDockStation();
		ComponentDockable dockable5 = new ComponentDockable( new JButton( "Five" ) );
		group2.drop( dockable5 );
		ComponentDockable dockable6 = new ComponentDockable( new JButton( "Six" ) );
		group2.drop( dockable6 );
		ToolbarGroupDockStation group3 = new ToolbarGroupDockStation();
		ComponentDockable dockable7 = new ComponentDockable( new JButton( "Seven" ) );
		group3.drop( dockable7 );
		ComponentDockable dockable8 = new ComponentDockable( new JButton( "Eight" ) );
		group3.drop( dockable8 );

		ToolbarDockStation toolbar2 = new ToolbarDockStation();
		toolbar2.drop( group2 );
		toolbar2.drop( group3 );
		toolbarStationNorth.drop( toolbar2 );

		/**
		 * Create a stack and add it in the center area
		 * */
		StackDockStation stackStation = new StackDockStation();
		controller.add( stackStation );
		DefaultDockable dockable9 = new DefaultDockable( "First" );
		stackStation.drop( dockable9 );
		DefaultDockable dockable10 = new DefaultDockable( "Second" );
		stackStation.drop( dockable10 );
		// controller.add(stackStation);
		System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" );
		pane.add(stackStation.getComponent(), BorderLayout.CENTER);
		//toolbarStation.drop( stackStation );
		System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" );

		/**
		 * Display frame
		 * */
		JFrame frame = new JFrame();
		frame.getContentPane().add(pane);
		//frame.add( toolbarStation.getComponent() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );

	}

}