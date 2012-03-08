package bibliothek.gui.dock.toolbar;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.station.screen.ScreenDockProperty;
import bibliothek.gui.dock.wizard.WizardSplitDockStation;
import bibliothek.gui.dock.wizard.WizardSplitDockStation.Side;

public class WizardSplitDockStationTest {
	public static void main( String[] args ){
		JFrame frame = new JFrame( "Test" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		JPanel borderPanel= new JPanel(new BorderLayout());
		frame.add(borderPanel);

		DockController controller = new DockController();
		controller.setRootWindow( frame );

		WizardSplitDockStation station = new WizardSplitDockStation( Side.RIGHT );
		
		controller.add( station );
		borderPanel.add( station.getComponent(), BorderLayout.EAST);

		ScreenDockStation screen = new ScreenDockStation( frame );
		controller.add( screen );

		screen.drop( create( "A" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "B" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "C" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "D" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "E" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "F" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "G" ), new ScreenDockProperty( 420, 20, 400, 400 ) );
		screen.drop( create( "Very long long long long button" ), new ScreenDockProperty( 420, 20, 400, 400 ) );

		frame.setBounds( 20, 20, 400, 400 );
		screen.setShowing( true );
		frame.setVisible( true );
	}

	private static Dockable create( String title ){
		DefaultDockable dockable = new DefaultDockable( title );
		dockable.setLayout( new BorderLayout() );
		JButton button = new JButton( title );
		dockable.add( button );
		return dockable;
	}

}