import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.DockController;
import bibliothek.gui.Orientation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.station.toolbar.group.ToolbarGroupProperty;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarGroupDockStation {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		JPanel pane = new JPanel( new BorderLayout() );
		frame.add( pane );

		DockController controller = new DockController();
		ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		ToolbarContainerDockStation west = new ToolbarContainerDockStation( Orientation.VERTICAL );
		ToolbarContainerDockStation east = new ToolbarContainerDockStation( Orientation.VERTICAL );
		ToolbarContainerDockStation north = new ToolbarContainerDockStation( Orientation.HORIZONTAL );
		ToolbarContainerDockStation south = new ToolbarContainerDockStation( Orientation.HORIZONTAL );

		controller.add( west );
		controller.add( east );
		controller.add( north );
		controller.add( south );

		frame.add( west.getComponent(), BorderLayout.WEST );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );

		ToolbarGroupDockStation group = new ToolbarGroupDockStation();

		group.drop( createToolbar( "a", "b", "c" ), 0, 0 );
		group.drop( createToolbar( "d", "e", "f" ), 0, 1 );
		group.drop( createToolbar( "g", "h" ), 1, 0 );
		group.drop( createToolbar( "i", "j" ), 1, 1 );

		group.drop( createToolbar( "k", "l" ), new ToolbarGroupProperty( 1, 0, null ) );
		group.drop( createToolbar( "m", "n", "o" ), new ToolbarGroupProperty( 3, 2, null ) );
		group.drop( createToolbar( "q", "r", "s" ), new ToolbarGroupProperty( -1, 5, null ) );

		// group.move( group.getDockable( 0 ), new ToolbarGroupProperty( 2, 1, null ) );
		
		west.drop( group );

		frame.setBounds( 20, 20, 400, 400 );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
	}

	private static ToolbarDockStation createToolbar( String... buttons ){
		ToolbarDockStation toolbar = new ToolbarDockStation();
		for( String button : buttons ) {
			toolbar.drop( createDockable( button.toLowerCase(), button.toUpperCase() ) );
		}
		return toolbar;
	}

	private static ComponentDockable createDockable( String small, String large ){
		ComponentDockable dockable = new ComponentDockable();
		dockable.setComponent( new JLabel( small ), ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( large ), ExpandedState.STRETCHED );
		dockable.setComponent( new JScrollPane( new JTextArea( small + "\n\n" + large ) ), ExpandedState.EXPANDED );
		return dockable;
	}
}
