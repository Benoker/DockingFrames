
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
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestToolbarContainerDockStation{

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		JPanel pane = new JPanel(new BorderLayout());
		frame.add( pane );

		DockController controller = new DockController();
		ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		System.out.println("###############################################################");
		System.out.println("##################  NEW CONTAINER  ############################");
		System.out.println("###############################################################");
		ToolbarContainerDockStation toolbarStationWest = new ToolbarContainerDockStation(Orientation.HORIZONTAL);
		controller.add( toolbarStationWest );
		pane.add(toolbarStationWest.getComponent(), BorderLayout.NORTH );
		ToolbarContainerDockStation toolbarStationEast = new ToolbarContainerDockStation(Orientation.VERTICAL);
		controller.add( toolbarStationEast );
		pane.add(toolbarStationEast.getComponent(), BorderLayout.EAST);
		System.out.println("###############################################################");
		System.out.println("##################  NEW COMPONENT  ############################");
		System.out.println("###############################################################");
		ComponentDockable dockable1 = createDockable( "1", "One" );
		System.out.println("###############################################################");
		System.out.println("##################  COMPONENT DROP INTO GROUP  ################");
		System.out.println("###############################################################");
		
		toolbarStationWest.drop( dockable1 );
		dockable1.getDockParent().drop( createDockable( "2", "Two" ));
		dockable1.getDockParent().drop( createDockable( "3", "Three" ));
		dockable1.getDockParent().drop( createDockable( "4", "Four" ));
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 500, 500 );
		screen.setShowing( true );
		frame.setVisible( true );
		System.out.println("##############################################");
		System.out.println("##################  MAIN END  ################");
		System.out.println("##############################################");

	}

	private static ComponentDockable createDockable( String small, String large ){
		ComponentDockable dockable = new ComponentDockable();
		dockable.setComponent( new JLabel( small ), ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( large ), ExpandedState.STRETCHED );
		dockable.setComponent( new JScrollPane( new JTextArea( small + "\n\n" + large ) ), ExpandedState.EXPANDED );
		return dockable;
	}
}