import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.control.SingleParentRemover;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;

public class TestLayouting {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

		DockController controller = new DockController();
		controller.setSingleParentRemover( new SingleParentRemover(){
			@Override
			protected boolean test( DockStation station ){
				return false;
			}
		});
		
		controller.setRootWindow( frame );

		ScreenDockStation screen = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screen );
		ScreenDockProperty initial = new ScreenDockProperty( 20, 20, 200, 20 );

		ComponentDockable dockable = new ComponentDockable( new JButton( "hello" ) );

		ToolbarDockStation group = new ToolbarDockStation(){
			@Override
			public boolean accept( DockStation station ){
				return true;
			}
		};
		group.drop( dockable );
		
		ToolbarGroupDockStation toolbar = new ToolbarGroupDockStation();
		toolbar.drop( group );

		boolean dropped = screen.drop( toolbar, initial );
		if( !dropped ) {
			throw new IllegalStateException( "not dropped" );
		}

		screen.setShowing( true );
		frame.setBounds( 0, 0, 300, 300 );
		frame.setVisible( true );
		
		System.out.println(dockable.getComponent().getPreferredSize());
		System.out.println(group.getComponent().getPreferredSize());
		System.out.println(toolbar.getComponent().getPreferredSize());
		
	}
}
