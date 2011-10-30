import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.PositionedDockStation;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;

/**
 * I have two questions.
 * 
 * First, When I add StackDockStation to the ToolbarDockStation do I need to add
 * the StackDockStation to the controller at the same time?
 * 
 * Second, the FlapDockStation drop in the ToolbarContainterDockStation is 
 * visible. But if I drop a StackDockStation, the stack station is not
 * visible. Any idea why?
 *  
 * 
 * */
public class TestToolbarDockAndStack {

	/**
	 * @param args
	 */
	public static void main( String[] args ){

		DockController controller = new DockController();

		/**
		 * Create a ToolbarContainerDockStation
		 * */
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
		toolbarStation.drop( toolbar1, Position.WEST );

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
		toolbarStation.drop( toolbar2, Position.EAST );

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
		toolbarStation.drop( stackStation );
		System.out.println( "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" );

		/**
		 * Display frame
		 * */
		JFrame frame = new JFrame();
		frame.add( toolbarStation.getComponent() );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 400, 400 );
		frame.setVisible( true );

	}

}