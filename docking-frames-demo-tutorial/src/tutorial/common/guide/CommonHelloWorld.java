package tutorial.common.guide;

import java.awt.Color;

import javax.swing.JPanel;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;

/*
 * Hello World - with the common library
 */
@Tutorial( id="CommonHelloWorld", title="Hello World" )
public class CommonHelloWorld {
	public static void main( String[] args ){
		/* One always needs some frame, dialog or applet to show things.
		 * JTutorialFrame is a normal JFrame which sets up title, size and
		 * closing-listeners */
		JTutorialFrame frame = new JTutorialFrame( CommonHelloWorld.class );
		
		/* The CControl manages all the different aspects of the framework. Often 
		 * applications need only one of these objects and for most applications
		 * creating the CControl early on is the easiest solution. */
		CControl control = new CControl( frame );
		
		/* Since many tutorials may run in the same JVM we need todo some 
		 * cleaning up when closing a tutorial. Calling CControl.destroy()
		 * will free some resources, and the JTutorialFrame does call this
		 * method automatically. */
		frame.destroyOnClose( control );
		
		
		/* Finally we start with the real work: setting up the stations. The stations
		 * are the anchor points for the Dockables. To make things easier we use the
		 * default "CContentArea" which is offered by the CControl. It does not require
		 * any additional setup other than putting it on the main-frame. */
		frame.add( control.getContentArea() );
		
		/* Now we create our first Dockable... */
		SingleCDockable yellow = create( "yellow", "Yellow", Color.YELLOW );
		/* .., and register it at the CControl. All Dockables must be known to the 
		 * CControl. */
		control.addDockable( yellow );
		
		/* We set the initial location of our Dockable. The class "CLocation" and its
		 * subclasses are used to describe a location. In this case we set the location
		 * to point to the minimize area at the left side of the frame. */
		yellow.setLocation( CLocation.base().minimalWest() );
		
		/* And then we make the Dockable visible */
		yellow.setVisible( true );
		
		/* When setting up the center area of the CContentArea you can make use of
		 * the class "CGrid". CGrid only exists to layout some Dockables and put them
		 * onto the center area. Adding Dockables to the CGrid after it has been deployed
		 * will not have any effects, neither will be CGrid be updated by the framework
		 * once it is deployed.
		 * By using giving "control" to the CGrid we do not need to call "control.addDockable",
		 * the CGrid will do that for us. */
		CGrid grid = new CGrid( control );
		/* Best imaging the CGrid as a sheet of paper. You put your panels onto the paper
		 * and measure the position and size of the panels afterwards. You then forward
		 * these numbers to the CGrid. */
		grid.add( 0, 0, 1, 1, create( "red", "Red", Color.RED ) );
		grid.add( 0, 1, 1, 1, create( "green", "Green", Color.GREEN ) );
		grid.add( 1, 0, 1, 2, create( "blue", "Blue", Color.BLUE ) );
		/* Once filled up, the grid can be deployed. All its content is copied to the
		 * center area. */
		control.getContentArea().deploy( grid );
		
		/* And we finish startup by making our main-frame visible */
		frame.setVisible( true );
	}
	
	/* This method creates a new Dockable with title "title" and a single JPanel with
	 * its background color set to "color". */
	private static SingleCDockable create( String id, String title, Color color ){
		DefaultSingleCDockable dockable = new DefaultSingleCDockable( id, title );
		dockable.setTitleText( title );
		dockable.setCloseable( false );
		
		JPanel panel = new JPanel();
		panel.setOpaque( true );
		panel.setBackground( color );
		dockable.add( panel );
		
		return dockable;
	}
}
