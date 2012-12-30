package tutorial.dockFrontend.basics;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.FlapDockStation.Direction;
import bibliothek.gui.dock.themes.NoStackTheme;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;

@Tutorial( title="Putting a Dockable aside another", id = "PuttingAside" )
public class AsideExample {
	public static void main( String[] args ){
		/* Sometimes you might want to show a Dockable right next to another Dockable. The
		 * "aside" feature allows you to get a location close to one Dockable, and use that
		 * location for another Dockable.
		 * 
		 * This example shows one Dockable, if you click on the "copy me" button a new Dockable opens
		 * right next to the first one. And every time you open a new Dockable it appears close to
		 * its source.  */
		
		/* We need a frame and several stations for testing */
		JTutorialFrame frame = new JTutorialFrame( AsideExample.class );
		DockFrontend frontend = new DockFrontend( frame );
		frame.destroyOnClose( frontend );
		frontend.getController().setTheme( new NoStackTheme( new SmoothTheme() ) );
		
		SplitDockStation center = new SplitDockStation();
		FlapDockStation north = new FlapDockStation();
		FlapDockStation south = new FlapDockStation();
		FlapDockStation east = new FlapDockStation();
		FlapDockStation west = new FlapDockStation();
		ScreenDockStation screen = new ScreenDockStation( frontend.getOwner() );
		
		north.setAutoDirection( false );
		south.setAutoDirection( false );
		east.setAutoDirection( false );
		west.setAutoDirection( false );
		
		north.setDirection( Direction.SOUTH );
		south.setDirection( Direction.NORTH );
		east.setDirection( Direction.WEST );
		west.setDirection( Direction.EAST );
		
		frame.add( center, BorderLayout.CENTER );
		frame.add( north.getComponent(), BorderLayout.NORTH );
		frame.add( south.getComponent(), BorderLayout.SOUTH );
		frame.add( east.getComponent(), BorderLayout.EAST );
		frame.add( west.getComponent(), BorderLayout.WEST );
		
		frontend.addRoot( "split", center );
		frontend.addRoot( "south", south );
		frontend.addRoot( "north", north );
		frontend.addRoot( "east", east );
		frontend.addRoot( "west", west );
		frontend.addRoot( "screen", screen );
		frontend.setDefaultStation( center );
		
		/* And we create an initial dockable, which registers itself at "frontend" */
		CopyableDockable dockable = new CopyableDockable( frontend, "id" );
		frontend.show( dockable );
		
		frame.setVisible( true );
		screen.setShowing( true );
	}
	
	private static class CopyableDockable extends DefaultDockable implements ActionListener{
		private DockFrontend frontend;
		private String id;
		private int copies = 0;
		
		public CopyableDockable( DockFrontend frontend, String id ){
			/* Nothing special happens in this constructor: a few properties are saved 
			 * and a button is created. */
			this.frontend = frontend;
			this.id = id;
			
			setTitleText( id );
			frontend.addDockable( id, this );
			JButton button = new JButton( "Copy me" );
			add( button );
			button.addActionListener( this );
		}
		
		public void actionPerformed( ActionEvent e ){
			String nextId = id + "-" + (copies++);
			
			/* This is the really interesting piece of code.
			 *  1. A dockable is created and added to the frontend
			 *  2. The location of the new Dockable is set "aside" this Dockable
			 *  3. The new Dockable is made visible */
			
			CopyableDockable next = new CopyableDockable( frontend, nextId );
			frontend.setLocationAside( next, this );
			frontend.show( next );
		}
	}
}
