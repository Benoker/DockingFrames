
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bibliothek.gui.DockController;
import bibliothek.gui.Position;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarContainerDockStation;
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
public class TestToolbarDockStation {

	/**
	 * @param args
	 */
	public static void main( String[] args ){
		JFrame frame = new JFrame();

		DockController controller = new DockController();
		// controller.getRelocator().setDragOnlyTitel(true);

		DirectWindowProvider windowProvider = new DirectWindowProvider();
		windowProvider.setWindow( frame );
		ScreenDockStation screenStation = new ScreenDockStation( windowProvider );
		screenStation.setShowing( true );
		controller.add( screenStation );
		System.out.println("###############################################################");
		System.out.println("##################  NEW CONTAINER  ############################");
		System.out.println("###############################################################");
		ToolbarContainerDockStation toolbarStation = new ToolbarContainerDockStation();
		controller.add( toolbarStation );
		System.out.println("###############################################################");
		System.out.println("##################  NEW COMPONENT  ############################");
		System.out.println("###############################################################");
		ComponentDockable dockable1 = createDockable( "1", "One" );
		ComponentDockable dockable2 = createDockable( "2", "Two" );
		ComponentDockable dockable3 = createDockable( "3", "Three" );
		System.out.println("###############################################################");
		System.out.println("##################  NEW GROUP  ################################");
		System.out.println("###############################################################");
		ToolbarGroupDockStation group1 = new ToolbarGroupDockStation();
		System.out.println("###############################################################");
		System.out.println("##################  NEW TOOLBAR  ##############################");
		System.out.println("###############################################################");
		ToolbarDockStation toolbar1 = new ToolbarDockStation();
		ToolbarDockStation toolbar2 = new ToolbarDockStation();
		ToolbarDockStation toolbar3 = new ToolbarDockStation();
		System.out.println("###############################################################");
		System.out.println("##################  COMPONENT DROP INTO GROUP  ################");
		System.out.println("###############################################################");
		group1.drop( dockable1 );
		System.out.println("###############################################################");
		System.out.println("##################  GROUP DROP INTO TOOLBAR  ##################");
		System.out.println("###############################################################");
		toolbar1.drop( group1 );
		toolbar2.drop(dockable2);
		toolbar3.drop(dockable3);
		System.out.println("###############################################################");
		System.out.println("##################  TOOLBAR DROP INTO CONTAINER  ##############");
		System.out.println("###############################################################");
		toolbarStation.drop( toolbar1, Position.WEST );
		toolbarStation.drop( toolbar2, Position.WEST );
		toolbarStation.drop( toolbar3, Position.NORTH );
		
//		ToolbarDockStation toolbar3 = new ToolbarDockStation();
//		ComponentDockable dockable6 = new ComponentDockable( new JButton( "Six" ) );
//		toolbar3.drop( dockable6 );
//		toolbarStation.drop( toolbar3, Position.SOUTH );
//
//		ToolbarGroupDockStation group2 = new ToolbarGroupDockStation();
//		ComponentDockable dockable5 = new ComponentDockable( new JButton( "Five" ) );
//		group2.drop( dockable5 );
//
//		ToolbarDockStation toolbar2 = new ToolbarDockStation();
//		toolbar2.drop( group2 );
//		toolbarStation.drop( toolbar2, Position.WEST );

//		Dockable other = new DefaultDockable( "Hallo" );
//		screenStation.drop( other );
		
		frame.add( toolbarStation.getComponent() );

		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setBounds( 20, 20, 500, 500 );
		frame.setVisible( true );
		//	screenStation.setShowing(true);

	}

	private static ComponentDockable createDockable( String small, String large ){
		ComponentDockable dockable = new ComponentDockable();
		dockable.setComponent( new JLabel( small ), ExpandedState.SHRUNK );
		dockable.setComponent( new JButton( large ), ExpandedState.STRETCHED );
		dockable.setComponent( new JScrollPane( new JTextArea( small + "\n\n" + large ) ), ExpandedState.EXPANDED );
		return dockable;
	}
}