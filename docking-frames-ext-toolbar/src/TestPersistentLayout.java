import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

import bibliothek.gui.DockController;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.ComponentDockable;
import bibliothek.gui.dock.ToolbarContainerDockStation;
import bibliothek.gui.dock.ToolbarDockStation;
import bibliothek.gui.dock.ToolbarGroupDockStation;
import bibliothek.gui.dock.event.DockRegisterListener;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;


public class TestPersistentLayout {
	public static void main( String[] args ){
		JFrame frame = new JFrame();
		final DockFrontend frontend = new DockFrontend( frame );
		
		ComponentDockable button1 = new ComponentDockable( new JButton( "One" ));
		ComponentDockable button2 = new ComponentDockable( new JButton( "Two" ));
		ComponentDockable button3 = new ComponentDockable( new JButton( "Three" ));
		ComponentDockable button4 = new ComponentDockable( new JButton( "Four" ));
		ComponentDockable button5 = new ComponentDockable( new JButton( "Five" ));
		ToolbarContainerDockStation root = new ToolbarContainerDockStation();
		
		frontend.addDockable( "one", button1 );
		frontend.addDockable( "two", button2 );
		frontend.addDockable( "three", button3 );
		frontend.addDockable( "four", button4 );
		frontend.addDockable( "five", button5 );
		frontend.addRoot( "root", root );
		
		frontend.getController().getRegister().addDockRegisterListener( new DockRegisterListener(){
			@Override
			public void dockableUnregistered( DockController controller, Dockable dockable ){
				System.out.println( " -> unregistered: " + dockable );	
			}
			
			@Override
			public void dockableRegistering( DockController controller, Dockable dockable ){
				System.out.println( " -> registering: " + dockable );
			}
			
			@Override
			public void dockableRegistered( DockController controller, Dockable dockable ){
				System.out.println( " -> registered: " + dockable );
			}
			
			@Override
			public void dockableCycledRegister( DockController controller, Dockable dockable ){
				System.out.println( " -> cycled: " + dockable );
			}
			
			@Override
			public void dockStationUnregistered( DockController controller, DockStation station ){
				System.out.println( " -> station unregistered: " + station );
			}
			
			@Override
			public void dockStationRegistering( DockController controller, DockStation station ){
				System.out.println( " -> station registering: " + station );
			}
			
			@Override
			public void dockStationRegistered( DockController controller, DockStation station ){
				System.out.println( " -> station registered: " + station );
			}
		});
		
		frame.add( root.getComponent() );
		
		final File layout = new File("layout.xml");
		boolean layouted = false;
		
		if( layout.exists() ){
			try{
				FileInputStream in = new FileInputStream( layout );
				XElement element = XIO.readUTF( in );
				in.close();
				frontend.readXML( element );
				layouted = true;
			}
			catch( IOException e ){
				e.printStackTrace();
			}
		}
		
		if( !layouted ){
			ToolbarGroupDockStation group = new ToolbarGroupDockStation();
			group.drop( button1 );
			group.drop( button2 );
			group.drop( button3 );
			group.drop( button4 );
			group.drop( button5 );
			
			ToolbarDockStation toolbar = new ToolbarGroupDockStation();
			toolbar.drop( group );
			
			root.drop( toolbar );
		}
		
		frame.setBounds( 20, 20, 400, 400 );
		frame.addWindowListener( new WindowAdapter(){
			@Override
			public void windowClosing( WindowEvent e ){
				try{
					XElement element = new XElement( "root" );
					frontend.writeXML( element );
					FileOutputStream out = new FileOutputStream( layout );
					XIO.writeUTF( element, out );
					out.close();
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
				System.exit( 0 );
			}
		});
		frame.setVisible( true );
	}
}
