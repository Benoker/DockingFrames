package tutorial.core.guide;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.TextDockable;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.DockFactory;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.layout.LocationEstimationMap;
import bibliothek.gui.dock.layout.PredefinedDockSituation;
import bibliothek.gui.dock.perspective.PerspectiveDockable;
import bibliothek.gui.dock.perspective.PerspectiveElement;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.station.support.PlaceholderStrategy;
import bibliothek.gui.dock.themes.NoStackTheme;
import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;

@Tutorial(title="Persistent Layout: Global", id="PersistentLayout")
public class PersistentLayoutExample {
	/* Assume you have written a big application with many Dockables. But the user complains because the 
	 * layout is reset every time he restarts the application. Of course there is an answer: store
	 * the layout persistently in a file.
	 * 
	 * In this example we convert the layout into xml and write the text on the screen. No need for
	 * a file, this is just a simple example.
	 * 
	 * If you think this is rather complex: there is a class DockFrontend which does most of the work
	 * described in this example automatically. We will visit DockFrontend in a later example.
	 *  */
	
	public static void main( String[] args ){
		/* Setting up a frame, a controller, a station and some Dockables, as usual */
		JTutorialFrame frame = new JTutorialFrame( PersistentLayoutExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		controller.setTheme( new NoStackTheme( new SmoothTheme() ));
		
		final SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* A TextDockable is not much more than a JTextArea */
		final TextDockable textDockable = new TextDockable( "Layout" );
		
		/* We use a customized ColorDockable for which we can write a factory */
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 50, 100, textDockable );
		grid.addDockable( 50, 0, 50, 50, new CustomColorDockable( "Red", Color.RED), new CustomColorDockable( "Green", Color.GREEN ));
		grid.addDockable( 50, 50, 25, 50, new CustomColorDockable( "Blue", Color.BLUE ));
		grid.addDockable( 75, 50, 25, 50, new CustomColorDockable( "Yellow", Color.YELLOW ));
		station.dropTree( grid.toTree() );
		
		
		/* The algorithms to store and load a layout are defined in the class DockSituation.
		 * DockSituation contains a map of factories which tell the algorithms how to write 
		 * and read the contents of a DockStation or Dockable. 
		 * DockSituation takes a set of DockStations and writes them into a file. When 
		 * reading the file a new set of new DockStations is created. This is not always
		 * desirable as we often would like to reuse existing DockStations and/or Dockables.
		 * The PredefinedDockSituation is an extension of DockSituation and allows to
		 * reuse existing Objects.  
		 * 
		 * It does not matter when we create "situation" or if we create multiple instances of
		 * PredefinedDockSituation. We do it now because because its convenient.
		 *  */
		final PredefinedDockSituation situation = new PredefinedDockSituation( controller );
		/* We are going to reuse "station" and "textDockable". "situation" will not store 
		 * those two elements, rather the identifiers "root" and "layout" are stored. */
		situation.put( "root", station );
		situation.put( "layout", textDockable );
		/* Since we are using instances of CustomColorDockable we need to provide a factory 
		 * for them. Have a look at CustomColorDockableFactory to understand what the
		 * a factory needs to do.  */
		situation.add( new CustomColorDockableFactory() );
		
		
		/* Finally we need some buttons to push in order to save and load the layout */
		final JMenuItem saveButton = new JMenuItem( "Save" );
		final JMenuItem loadButton = new JMenuItem( "Load" );
		loadButton.setEnabled( false );
		
		JMenu menu = new JMenu( "Layout" );
		menu.add( loadButton );
		menu.add( saveButton );
		JMenuBar menuBar = new JMenuBar();
		menuBar.add( menu );
		frame.setJMenuBar( menuBar );
		
		saveButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				/* To store a layout we need a map containing all the root-DockStations */
				Map<String, DockStation> stations = new HashMap<String, DockStation>();
				stations.put( "grid-station", station );
				XElement xroot = new XElement( "root" );
				situation.writeXML( stations, xroot );
				textDockable.setText( xroot.toString() );
				loadButton.setEnabled( true );
			}
		});
		
		loadButton.addActionListener( new ActionListener(){
			public void actionPerformed( ActionEvent e ){
				try{
					/* Here we read the layout. There is no need to check the result of
					 * "readXML": because we have predefined the root-DockStation, the layout
					 * already got applied. */
					XElement xroot = XIO.read( textDockable.getText() );
					situation.readXML( xroot );
				}
				catch( IOException ex ){
					ex.printStackTrace();
				}
			}
		});
		
		frame.setVisible( true );
	}
	
	/* Each factory needs a unique identifier, this is the one used by CustomColorDockableFactory */
	public static final String CUSTOM_COLOR_DOCKABLE_FACTORY_ID = "color";
	
	/* Our new customized Dockable. */
	private static class CustomColorDockable extends ColorDockable{
		public CustomColorDockable( String title, Color color ){
			super(title, color);
		}
		
		/* By implementing "getFactoryID" we tell the DockSituation which factory belongs to
		 * this Dockable. */
		@Override
		public String getFactoryID(){
			return CUSTOM_COLOR_DOCKABLE_FACTORY_ID;
		}
	}
	
	/* DockSituation does not write a file directly. It first converts the layout and contents of
	 * all DockStations and Dockables into an intermediate format. Using an intermediate format
	 * has several advantages:
	 *  - The layout can be stored in memory without needing much space
	 *  - Different formats for files can be used
	 *  - The algorithm can extract information from the intermediate format: e.g guess the location 
	 *    of a Dockable even if there is no real DockStation or Dockable around
	 * 
	 * Any object can be part of the intermediate format, in our case CustomColorLayout is the
	 * intermediate representation of CustomColorDockable. */
	private static class CustomColorLayout{
		private String title;
		private Color color;
		
		public CustomColorLayout( String title, Color color ){
			this.title = title;
			this.color = color;
		}
		
		public String getTitle(){
			return title;
		}
		
		public Color getColor(){
			return color;
		}
	}
	
	/* And we need the factory itself. This factory offers verious methods to convert data 
	 * from one format into another from. In a picture:
	 * 
	 *                                                    ------> XML
	 * CustomColorDockable <-----> CustomColorLayout <----|
	 *                                                    ------> byte[]
	 *
	 * DockFactories are used to handle Dockables and DockStations. We do not need to implement
	 * the methods handling children because our CustomColorDockable is no DockStation and hence
	 * will never have any children.
	 *  */
	private static class CustomColorDockableFactory implements DockFactory<CustomColorDockable, PerspectiveElement, CustomColorLayout>{
		public String getID(){
			return CUSTOM_COLOR_DOCKABLE_FACTORY_ID;
		}
		
		/* CustomColorDockable ---> CustomColorLayout */
		public CustomColorLayout getLayout( CustomColorDockable element, Map<Dockable, Integer> children ){
			return new CustomColorLayout( element.getTitleText(), element.getColor() );
		}
		
		/* CustomColorDockable <--- CustomColorLayout */
		public CustomColorDockable layout( CustomColorLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
			return layout( layout, placeholders );
		}

		/* CustomColorDockable <--- CustomColorLayout */
		public CustomColorDockable layout( CustomColorLayout layout, PlaceholderStrategy placeholders ){
			return new CustomColorDockable( layout.getTitle(), layout.getColor() );
		}
		
		/* CustomColorDockable <--- CustomColorLayout, using an existing CustomColorDockable (never happens
		 *                                             in our case) */
		public void setLayout( CustomColorDockable element, CustomColorLayout layout, Map<Integer, Dockable> children, PlaceholderStrategy placeholders ){
			setLayout( element, layout, placeholders );
		}

		/* CustomColorDockable <--- CustomColorLayout, using an existing CustomColorDockable (never happens
		 *                                             in our case) */
		public void setLayout( CustomColorDockable element, CustomColorLayout layout, PlaceholderStrategy placeholders ){
			element.setTitleText( layout.getTitle() );
			element.setColor( layout.getColor() );
		}
		
		/* ignore in this example */
		public CustomColorLayout getPerspectiveLayout( PerspectiveElement element, Map<PerspectiveDockable, Integer> children ){
			return null;
		}
		
		/* ignored in this example */
		public void layoutPerspective( PerspectiveElement perspective, CustomColorLayout layout, Map<Integer, PerspectiveDockable> children ){
			// ignore
		}
		
		/* ignored in this example */
		public PerspectiveElement layoutPerspective( CustomColorLayout layout, Map<Integer, PerspectiveDockable> children ){
			return null;
		}
		
		/* CustomColorLayout ----> byte[] */
		public void write( CustomColorLayout layout, DataOutputStream out ) throws IOException{
			out.writeUTF( layout.getTitle() );
			out.writeInt( layout.getColor().getRGB() );
		}
		
		/* CustomColorLayout <---- byte[] */
		public CustomColorLayout read( DataInputStream in, PlaceholderStrategy placeholders ) throws IOException{
			String title = in.readUTF();
			Color color = new Color( in.readInt() );
			return new CustomColorLayout( title, color );
		}

		/* CustomColorLayout ----> XML */
		public void write( CustomColorLayout layout, XElement element ){
			element.addElement( "title" ).setString( layout.getTitle() );
			element.addElement( "color" ).setInt( layout.getColor().getRGB() );
		}
		
		/* CustomColorLayout <---- XML */
		public CustomColorLayout read( XElement element, PlaceholderStrategy placeholders ){
			String title = element.getElement( "title" ).getString();
			Color color = new Color( element.getElement( "color" ).getInt() );
			return new CustomColorLayout( title, color );
		}
		
		public void estimateLocations( CustomColorLayout layout, LocationEstimationMap children ){
			// ignore
		}
	}
}
