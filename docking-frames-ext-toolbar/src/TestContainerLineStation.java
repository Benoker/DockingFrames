
import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.DockController;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ContainerLineStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;
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
public class TestContainerLineStation{

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		JPanel pane = new JPanel(new BorderLayout());
		frame.add( pane );

		DockController controller = new DockController();

		System.out.println("###############################################################");
		System.out.println("##################  NEW CONTAINER  ############################");
		System.out.println("###############################################################");
		ContainerLineStation toolbarStationWest = new ContainerLineStation();
		controller.add( toolbarStationWest );
		pane.add(toolbarStationWest.getComponent(), BorderLayout.WEST);
		ContainerLineStation toolbarStationEast = new ContainerLineStation();
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
		
		
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 500, 500 );
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