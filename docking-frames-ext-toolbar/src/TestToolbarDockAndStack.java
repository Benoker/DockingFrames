import java.awt.BorderLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.StackDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarDockAndStack{

	/**
	 * @param args
	 */
	public static void main( String[] args ){

		DockController controller = new DockController();

		JPanel pane = new JPanel(new BorderLayout());

		/**
		 * Create a ToolbarContainerDockStation
		 * */
		ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation(
				Orientation.VERTICAL);
		pane.add(toolbarStationWest.getComponent(), BorderLayout.WEST);
		ToolbarContainerDockStation toolbarStationNorth = new ToolbarContainerDockStation(
				Orientation.HORIZONTAL);
		pane.add(toolbarStationNorth.getComponent(), BorderLayout.NORTH);
		controller.add(toolbarStationWest);
		controller.add(toolbarStationNorth);

		// Disable the expand state action button
		controller.getProperties().set(
				ExpandableToolbarItemStrategy.STRATEGY,
				new DefaultExpandableToolbarItemStrategy(){
					@Override
					public boolean isEnabled( Dockable item, ExpandedState state ){
						return false;
					}
				});

		ToolbarGroupDockStation group1 = new ToolbarGroupDockStation();
		JButton button = new JButton("One");
		button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		ComponentDockable dockable1 = new ComponentDockable(button);
		group1.drop(dockable1);
		button = new JButton("Two");
		button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		ComponentDockable dockable2 = new ComponentDockable(button);
		group1.drop(dockable2);
		button = new JButton("Three");
		button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		ComponentDockable dockable3 = new ComponentDockable(button);
		group1.drop(dockable3);
		button = new JButton("Four");
		button.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		ComponentDockable dockable4 = new ComponentDockable(button);
		group1.drop(dockable4);

		ToolbarDockStation toolbar1 = new ToolbarDockStation();
		toolbar1.drop(group1);
		toolbarStationWest.drop(toolbar1);

		ToolbarGroupDockStation group2 = new ToolbarGroupDockStation();
		ComponentDockable dockable5 = new ComponentDockable(new JButton("Five"));
		group2.drop(dockable5);
		ComponentDockable dockable6 = new ComponentDockable(new JButton("Six"));
		group2.drop(dockable6);
		ToolbarGroupDockStation group3 = new ToolbarGroupDockStation();
		ComponentDockable dockable7 = new ComponentDockable(
				new JButton("Seven"));
		group3.drop(dockable7);
		ComponentDockable dockable8 = new ComponentDockable(
				new JButton("Eight"));
		group3.drop(dockable8);

		ToolbarDockStation toolbar2 = new ToolbarDockStation();
		toolbar2.drop(group2);
		toolbar2.drop(group3);
		toolbarStationNorth.drop(toolbar2);

		/**
		 * Create a stack and add it in the center area
		 * */
		StackDockStation stackStation = new StackDockStation();
		controller.add(stackStation);
		DefaultDockable dockable9 = new DefaultDockable("First");
		stackStation.drop(dockable9);
		DefaultDockable dockable10 = new DefaultDockable("Second");
		stackStation.drop(dockable10);
		// controller.add(stackStation);
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		pane.add(stackStation.getComponent(), BorderLayout.CENTER);
		// toolbarStation.drop( stackStation );
		System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");

		/**
		 * Display frame
		 * */
		JFrame frame = new JFrame();
		frame.getContentPane().add(pane);
		// frame.add( toolbarStation.getComponent() );
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(20, 20, 400, 400);
		frame.setVisible(true);

	}

}