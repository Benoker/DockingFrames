import java.awt.BorderLayout;
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
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.toolbar.expand.DefaultExpandableToolbarItemStrategy;
import bibliothek.gui.dock.toolbar.expand.ExpandedState;

public class TestSimpleToolbarDockStation{

	public static void main( String[] args ){

		final JFrame frame = new JFrame();
		final JPanel pane = new JPanel(new BorderLayout());
		frame.add(pane);

		final DockController controller = new DockController();

		final ScreenDockStation screen = new ScreenDockStation(frame);
		controller.add(screen);

		final SimpleToolbarDockStation outerToolbarStation = new SimpleToolbarDockStation();
		controller.add(outerToolbarStation);
		
		final SimpleToolbarDockStation innerToolbarStation = new SimpleToolbarDockStation();
		controller.add(innerToolbarStation);
		ComponentDockable dockable = new ComponentDockable(new JButton("First"));
		ComponentDockable dockable2 = new ComponentDockable(new JButton(
				"Second"));
		ComponentDockable dockable3 = new ComponentDockable(
				new JButton("Three"));
		ComponentDockable dockable4 = new ComponentDockable(new JButton("Four"));
		ComponentDockable dockable5 = new ComponentDockable(new JButton("Five"));
		ComponentDockable dockable6 = new ComponentDockable(new JButton("Six"));
		innerToolbarStation.drop(dockable);
		innerToolbarStation.drop(dockable2);
		innerToolbarStation.drop(dockable3);
		innerToolbarStation.drop(dockable4);
		innerToolbarStation.drop(dockable5);
		innerToolbarStation.drop(dockable6);
		
		
		outerToolbarStation.drop(innerToolbarStation);

		
		
		
		
//		final DockTitleManager titles = controller.getDockTitleManager();
//		titles.registerClient(SimpleToolbarDockStation.TITLE_ID,
//				ToolbarGroupTitle.FACTORY);

//		pane.add(outerToolbarStation.getComponent());
		pane.add(outerToolbarStation.getComponent());

		
		
		

		// Disable the expand state action button
		controller.getProperties().set(ExpandableToolbarItemStrategy.STRATEGY,
				new DefaultExpandableToolbarItemStrategy(){
					@Override
					public boolean isEnabled( Dockable item, ExpandedState state ){
						return false;
					}
				});

		frame.setBounds(20, 20, 400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		screen.setShowing(true);
	}

}
