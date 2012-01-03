import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ExpandableToolbarItemStrategy;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SimpleToolbarDockStation;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestSimpleToolbarDockStation {

	public static void main( String[] args ){

		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel( new FlowLayout() );
		frame.add( pane );

		final DockController controller = new DockController();

		final ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		final SimpleToolbarDockStation toolbarStation = new SimpleToolbarDockStation();
		controller.add( toolbarStation );

		frame.add( toolbarStation.getComponent() );
		
		ComponentDockable dockable =  new ComponentDockable(new JButton("First"));
		ComponentDockable dockable2 =  new ComponentDockable(new JButton("Second"));
		ComponentDockable dockable3 =  new ComponentDockable(new JButton("Three"));
		ComponentDockable dockable4 =  new ComponentDockable(new JButton("Four"));
		ComponentDockable dockable5 =  new ComponentDockable(new JButton("Five"));
		ComponentDockable dockable6 =  new ComponentDockable(new JButton("Six"));
		toolbarStation.drop( dockable );
		toolbarStation.drop( dockable2 );
		toolbarStation.drop( dockable3 );
		toolbarStation.drop( dockable4 );
		toolbarStation.drop( dockable5 );
		toolbarStation.drop( dockable6 );

		// Disable the expand state action button
		controller.getProperties().set( ExpandableToolbarItemStrategy.STRATEGY, new DefaultExpandableToolbarItemStrategy(){
			@Override
			public boolean isEnabled( Dockable item, ExpandedState state ){
				return false;
			}
		} );
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		frame.setVisible( true );
		screen.setShowing( true );
	}

}
