package tutorial.dockFrontend.basics;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.TextDockable;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockFrontend;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.event.DockFrontendAdapter;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

@Tutorial( title="Persistent Layout", id="DFPersistentLayout" )
public class PersistentLayoutExample {
	public static void main( String[] args ){
		/* You already have seen how a layout can be persistently stored using 
		 * the class DockSituation or PredefinedDockSituation. But maybe the
		 * user would like to store more than one layout, and maybe you don't
		 * like to setup a DockSituation all the times.
		 * 
		 * DockFrontend offers methods to store more than one layout persistently,
		 * the user just has to give the different layouts a name. The methods
		 * responsible for handling layouts are:
		 * 
		 *   - write/read: writes/reads all the stored layouts and the current
		 *       layout from/to a file.
		 *   - save/load/delete: saves, loads or deletes a layout, each layout
		 *       is identified by a unique String
		 *   - setEntryLayout: if set to true, then a Dockables location gets 
		 *       stored every time "save" is called. If set to false, then
		 *       the location gets only stored if "write" is called. 
		 *       
		 * This example allows you to experiment with the save/load/delete and
		 * shows what data is stored when you use the write/read methods. */
		
		/* Setting up a frame, a frontend, a DockStation and some Dockables */
		JTutorialFrame frame = new JTutorialFrame( PersistentLayoutExample.class );
		DockFrontend frontend = new DockFrontend( frame );
		frame.destroyOnClose( frontend );
		frontend.getController().setTheme( new NoStackTheme( new SmoothTheme() ) );
		
		SplitDockStation station = new SplitDockStation();
		frame.add( station );
		frontend.addRoot( "split", station );
		
		/* Prepare the Dockables we are going to put onto "station" */
		Dockable red = new ColorDockable( "Red", Color.RED, 2.5f );
		Dockable green =  new ColorDockable( "Green", Color.GREEN, 2.5f );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE, 2.5f );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW, 2.5f );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN, 2.5f );
		Dockable magenta = new ColorDockable( "Magenta", Color.MAGENTA, 2.5f );
		
		/* We will show the persistent data on this JTextArea */
		TextDockable layout = new TextDockable( "File" );
		
		/* Never forget to register all Dockables at the DockFrontend */
		frontend.addDockable( "red", red );
		frontend.addDockable( "green", green );
		frontend.addDockable( "blue", blue );
		frontend.addDockable( "yellow", yellow );
		frontend.addDockable( "cyan", cyan );
		frontend.addDockable( "magenta", magenta );
		frontend.addDockable( "file", layout );
		
		/* Adding the Dockables to "station" */
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 40, 100, red, green, blue );
		grid.setSelected( 0, 0, 40, 100, green );
		grid.addDockable( 40, 0, 60, 30, yellow );
		grid.addDockable( 40, 30, 20, 70, cyan );
		grid.addDockable( 60, 30, 40, 70, magenta );
		grid.addDockable( -40, 0, 40, 100, layout );
		
		station.dropTree( grid.toTree() );
	
		/* We build a menu that allows us to call the different methods easily */
		JMenu menu = new JMenu( "Layout" );
		
		/* The ReadAction calls DockFrontend.readXML */
		ReadAction read = new ReadAction( frontend, layout );
		menu.add( read );
		
		/* The WriteAction calls DockFrontend.writeXML */
		menu.add( new WriteAction( frontend, layout, read ) );
		menu.addSeparator();
		
		/* The LoadAction calls DockFrontend.load */
		menu.add( new LoadAction( frontend ) );
		
		/* The SaveAction calls DockFrontend.save() */
		menu.add( new SaveAction( frontend ) );
		
		/* The SaveAsAction calls DockFrontend.save(...) */
		menu.add( new SaveAsAction( frontend ) );
		
		/* Finally the DeleteAction calls DockFrontend.delete */
		menu.add( new DeleteAction( frontend ) );
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( menu );
		frame.setJMenuBar( menuBar );
		frame.setVisible( true );
	}
	
	/* This abstract action stores a DockFrontend to be used by its subclasses */
	public static abstract class FrontendAction extends AbstractAction{
		protected DockFrontend frontend;
		
		public FrontendAction( String text, DockFrontend frontend ){
			putValue( NAME, text );
			this.frontend = frontend;
		}
	}
	
	/* This action opens a dialog on which the user can choose one of the available
	 * layouts. The subclass decides what todo with the layout. */
	public static abstract class FrontendLayoutListAction extends FrontendAction{
		public FrontendLayoutListAction( String text, DockFrontend frontend ){
			super( text, frontend );
			
			/* A listener to "frontend" will tell us when there are layouts
			 * available and when not */
			frontend.addFrontendListener( new DockFrontendAdapter() {
				@Override
				public void saved( DockFrontend frontend, String name ){
					setEnabled( !frontend.getSettings().isEmpty() );
				}
				
				@Override
				public void deleted( DockFrontend frontend, String name ){
					setEnabled( !frontend.getSettings().isEmpty() );		
				}
				
				@Override
				public void read( DockFrontend frontend, String name ){
					setEnabled( !frontend.getSettings().isEmpty() );
				}
			});
			setEnabled( !frontend.getSettings().isEmpty() );
		}
		
		public void actionPerformed( ActionEvent e ){
			/* "getSettings" gets us an unmodifiable Set containing the names
			 * of all the available layouts */
			Set<String> settings = frontend.getSettings();
			String[] list = settings.toArray( new String[ settings.size() ] );
			Arrays.sort( list );
			
			String layout = (String)JOptionPane.showInputDialog( frontend.getController().findRootWindow(), 
					"Choose one", "Input", JOptionPane.INFORMATION_MESSAGE, null, list, frontend.getCurrentSetting() );
			if( layout != null ){
				action( layout );
			}
		}
		
		protected abstract void action( String settingName );
	}
	
	public static class SaveAction extends FrontendAction{
		public SaveAction( DockFrontend frontend ){
			super( "Save", frontend );
		}
		public void actionPerformed( ActionEvent e ){
			/* "save()" stores the current layout using the name of the last loaded layout. If there
			 * is no last loaded layout then we have choose a name */
			if( frontend.getCurrentSetting() == null ){
				String name = JOptionPane.showInputDialog( frontend.getController().findRootWindow(), "Please input name of layout" );
				if( name != null ){
					frontend.save( name );
				}
			}
			else{
				frontend.save();
			}
		}
	}
	
	public static class SaveAsAction extends FrontendAction{
		public SaveAsAction( DockFrontend frontend ){
			super( "Save As...", frontend );
		}
		
		public void actionPerformed( ActionEvent e ){
			String name = JOptionPane.showInputDialog( frontend.getController().findRootWindow(), "Please input name of layout" );
			if( name != null ){
				frontend.save( name );
			}
		}
	}
	
	public static class LoadAction extends FrontendLayoutListAction{
		public LoadAction( DockFrontend frontend ){
			super( "Load", frontend );
		}
		
		@Override
		protected void action( String settingName ){
			/* Cannot say much about that, just calling "load" with one of the
			 * available layouts */
			frontend.load( settingName );	
		}
	}
	
	public static class DeleteAction extends FrontendLayoutListAction{
		public DeleteAction( DockFrontend frontend ){
			super( "Delete", frontend );
		}
		
		@Override
		protected void action( String settingName ){
			frontend.delete( settingName );
		}
	}
	
	public static class WriteAction extends FrontendAction{
		private TextDockable target;
		private ReadAction opposite;
		
		public WriteAction( DockFrontend frontend, TextDockable target, ReadAction opposite ){
			super( "Write", frontend );
			this.target = target;
			this.opposite = opposite;
			opposite.setEnabled( false );
		}
		
		public void actionPerformed( ActionEvent e ){
			/* We convert all available layouts and the current layout to xml and
			 * show the text on our TextDockable */
			XElement xroot = new XElement( "layout" );
			frontend.writeXML( xroot );
			target.setText( xroot.toString() );
			opposite.setEnabled( true );
		}
	}
	
	public static class ReadAction extends FrontendAction{
		private TextDockable source;
		
		public ReadAction( DockFrontend frontend, TextDockable source ){
			super( "Read", frontend );
			this.source = source;
		}
		
		public void actionPerformed( ActionEvent e ){
			try{
				/* "read" does not delete already existing layouts, so we clean up
				 * before applying a new set of layouts. */
				Set<String> layouts = frontend.getSettings();
				String[] keys = layouts.toArray( new String[ layouts.size() ] );
				for( String key : keys ){
					frontend.delete( key );
				}
				
				/* And now we can safely apply the old layouts */
				XElement xroot = XIO.read( source.getText() );
				frontend.readXML( xroot );
			}
			catch( IOException ex ){
				ex.printStackTrace();
			}
		}
	}
}