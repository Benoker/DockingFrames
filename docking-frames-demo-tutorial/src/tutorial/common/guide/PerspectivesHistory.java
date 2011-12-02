package tutorial.common.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.mode.ExtendedMode;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CDockablePerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CMinimizePerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CWorkingPerspective;
import bibliothek.gui.dock.common.perspective.MultipleCDockablePerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.facile.mode.Location;
import bibliothek.gui.dock.layout.DockableProperty;
import bibliothek.gui.dock.station.flap.FlapDockProperty;
import bibliothek.util.Filter;
import bibliothek.util.xml.XElement;

@Tutorial( title="Perspectives (History)", id="PerspectivesHistory" )
public class PerspectivesHistory {
	/* CDockables not only have a current location, they also have a history of locations.
	 * For example if a CDockable was minimized on the east side of the application, then this
	 * location "minimized east" is stored in the history and used if the user minimizes the
	 * dockable the next time.
	 * 
	 * The perspective API allows create and modify the history of each dockable, this example
	 * sets up several dockables at different locations with a history.
	 * 
	 ****
	 * IMPORTANT: In this example we set the history of MultipleCDockables. We need to
	 * manually set the identifiers of the MultipleCDockablePerspectives to get this working.
	 ****
	 */
	
	
	/* We are going to work with MultipleCDockables so we need a factory and an identifier
	 * for this factory. */
	public static final String CUSTOM_MULTI_FACTORY_ID = "custom";
	
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( PerspectivesHistory.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );

		/* When working with perspectives it is always a good idea first to set up the 
		 * CControl, then create the perspectives. */
		ColorFactory colorFactory = new ColorFactory();
		control.addSingleDockableFactory( colorFactory, colorFactory );
		control.addMultipleDockableFactory( CUSTOM_MULTI_FACTORY_ID, new CustomMultiFactory() );
		
		/* By creating the root-stations now we automatically create counterparts in the 
		 * perspective. Otherwise we would need to do some setting up with the perspectives as 
		 * well. */
		frame.add( control.getContentArea(), BorderLayout.CENTER );
		control.createWorkingArea( "work" );
		
		/* Access to the perspective API */
		CControlPerspective perspectives = control.getPerspectives();
		
		/* Creating a new, empty perspective */
		CPerspective perspective = perspectives.createEmptyPerspective();
	
		/* For building up a history we need to move around CDockablePerspectives. It is
		 * not possible to use several dockables with the same identifier. To make things
		 * easier we just store the dockables in a map. In a real application you probably
		 * want to implement a more advanced mechanism to store and access your dockables. 
		 * 
		 * IMPORTANT: the unique identifier of MultipleCDockablePerspectives is set by this method. */
		Map<String, CDockablePerspective> dockables = collect();
		
		/* We start by assigned the minimized location of all dockables. */
		setUpMinimized( perspective, dockables );
		/* Now we assign the normalized location of all dockables. */
		setUpNormalized( perspective, dockables );
		/* It is also possible to access and modify the location history directly. */
		modifyHistoryDirectly( perspective, dockables );
		/* At the end we set up the layout that the user will see when the application starts. */
		setUpFinalLayout( perspective, dockables );
		
		/* By calling "shrink" we instruct the perspective to remove unnecessary 
		 * PerspectiveStations from the layout. This is what the SingleParentRemover will do
		 * with real DockStations. */
		perspective.shrink();
		
	    /* Finally we apply the perspective we just created */
		perspectives.setPerspective( perspective, true );
	    
	    frame.setVisible( true );
	}
	
	/* This method creates several dockable-perspectives and stores them in a map. */
	private static Map<String, CDockablePerspective> collect(){
		Map<String, CDockablePerspective> result = new HashMap<String, CDockablePerspective>();
		
		result.put( "Red", new SingleCDockablePerspective( "Red" ) );
		result.put( "Green", new SingleCDockablePerspective( "Green" ) );
		result.put( "Blue", new SingleCDockablePerspective( "Blue" ) );
		result.put( "Yellow", new SingleCDockablePerspective( "Yellow" ) );
		result.put( "White", new SingleCDockablePerspective( "White" ) );
		result.put( "Black", new SingleCDockablePerspective( "Black" ) );
		
		for( int i = 0; i < 5; i++ ){
			CustomMultiLayout layout = new CustomMultiLayout( new Color( 0, i*20, 50 ));
			String id = "m" + i;
			
			/* When creating the new MultipleCDockablePerspective we directly assign a unique identifier. 
			 * If we do not assign the identifier the framework will assign a random identifier in the moment
			 * when the layout is converted to the intermediate format. As this happens at the very end, the
			 * dockable will run around without identifier for a long time. But without identifier history
			 * information cannot be created properly. The reason is that the framework uses placeholders to indicate
			 * where a dockable was shown, and these placeholders depend on the unique identifiers of the dockables.  */
			result.put( id, new MultipleCDockablePerspective( CUSTOM_MULTI_FACTORY_ID, id, layout ));
		}
		
		return result;
	}
	
	/* This method assigns the minimized location to the dockables */
	private static void setUpMinimized( CPerspective perspective, Map<String, CDockablePerspective> dockables ){
		/* In the beginning we access different minimize-perspectives and just drop our dockables
		 * onto them. */
		CMinimizePerspective west = perspective.getContentArea().getWest();
		west.add( dockables.get( "Red" ) );
		west.add( dockables.get( "Green" ) );
		west.add( dockables.get( "Blue" ) );
		
		CMinimizePerspective east = perspective.getContentArea().getEast();
		east.add( dockables.get( "Yellow" ) );
		east.add( dockables.get( "White" ) );
		east.add( dockables.get( "Black" ) );
		
		CMinimizePerspective south = perspective.getContentArea().getSouth();
		for( int i = 0; i < 5; i++ ){
			south.add( dockables.get( "m" + i ) );
		}
		
		/* And then we instruct the framework that the current location of the dockables should
		 * be stored as history information.
		 * The dockables remain at their current location, but we can just re-arrange them later. */
		perspective.storeLocations();
	}
	
	/* This method assigns the normalized location to the dockables. It works very much the same
	 * as the method "setUpMinimized" just above. */
	private static void setUpNormalized( CPerspective perspective, Map<String, CDockablePerspective> dockables ){
		CGridPerspective center = perspective.getContentArea().getCenter();
		CWorkingPerspective work = (CWorkingPerspective)perspective.getStation( "work" );
		
		center.gridAdd(  0,  0,  50, 25, dockables.get( "Red" ));
		center.gridAdd( 50,  0,  50, 25, dockables.get( "Green" ));
		center.gridAdd(  0, 25,  50, 25, dockables.get( "Blue" ));
		center.gridAdd( 50, 25,  50, 25, dockables.get( "Yellow" ));
		center.gridAdd(  0, 50, 100, 50, work );
		
		work.gridAdd( 0, 0, 50, 100, dockables.get( "White" ), dockables.get( "Black" ));
		for( int i = 0; i < 5; i++ ){
			work.gridAdd( 50, 0, 50, 100, dockables.get( "m" + i ) );
		}
		
		perspective.storeLocations();
	}
	
	/* It is possible to access and modify history information directly. In this case
	 * modify the history of the "White" dockable such that it thinks it was minimized
	 * on the "north" minimize-area. */
	private static void modifyHistoryDirectly( CPerspective perspective, Map<String, CDockablePerspective> dockables ){
		CMinimizePerspective north = perspective.getContentArea().getNorth();
		CDockablePerspective white = dockables.get( "White" );
		
		/* We first inform the north minimize-area that "white" was a child by 
		 * inserting a placeholder for "white" */
		north.addPlaceholder( white );
		
		/* Now we build up location information first by specifying the exact location of "white" */
		DockableProperty property = new FlapDockProperty( 0, false, 100, white.intern().asDockable().getPlaceholder() );
		/* We pack additional information like the mode and the root-station that was the parent of
		 * "white" together. */
		Location location = new Location( ExtendedMode.MINIMIZED.getModeIdentifier(), north.getUniqueId(), property );
		/* And finally we add the new location information to the history. */
		white.getLocationHistory().add( ExtendedMode.MINIMIZED, location );
	}
	
	/* After building up the history we set up the layout that we show the user when the
	 * applications starts. */
	private static void setUpFinalLayout( CPerspective perspective, Map<String, CDockablePerspective> dockables ){
		/* We minimize the "red" and the "black" dockable */
		CMinimizePerspective west = perspective.getContentArea().getWest();
		west.add( dockables.get( "Red" ) );
		
		CMinimizePerspective east = perspective.getContentArea().getEast();
		east.add( dockables.get( "Black" ) );
	}

	/* This factory and filter creates new SingleCDockables with some panel that has some
	 * special color set as background. */
	private static class ColorFactory implements SingleCDockableFactory, Filter<String>{
		private Map<String, Color> colors = new HashMap<String, Color>();
		
		public ColorFactory(){
			colors.put( "Red", Color.RED );
			colors.put( "Green", Color.GREEN );
			colors.put( "Blue", Color.BLUE );
			colors.put( "Yellow", Color.YELLOW );
			colors.put( "White", Color.WHITE );
			colors.put( "Black", Color.BLACK );
		}
		
		public boolean includes( String item ){
			return colors.containsKey( item );
		}
		
		public SingleCDockable createBackup( String id ){
			return new ColorSingleCDockable( id, colors.get( id ) );
		}
	}
	
	/* This is the kind of MultipleCDockable we will use on our application. */
	private static class CustomMultiDockable extends DefaultMultipleCDockable{
		private Color color;
		
		public CustomMultiDockable( CustomMultiFactory factory, String title, Color color ){
			super( factory, title );
			this.color = color;
			JPanel panel = new JPanel();
			panel.setBackground( color );
			panel.setOpaque( true );
			add( panel, BorderLayout.CENTER );
		}
		
		public Color getColor(){
			return color;
		}
	}
	
	/* This kind of MultipleCDockableLayout describes the content of a CustomMultiDockable. */
	private static class CustomMultiLayout implements MultipleCDockableLayout{
		private Color color;
		
		public CustomMultiLayout( Color color ){
			this.color = color;
		}
		
		public Color getColor(){
			return color;
		}
		
		public void readStream( DataInputStream in ) throws IOException{
			color = new Color( in.readInt() );
		}

		public void readXML( XElement element ){
			color = new Color( element.getInt() );
		}

		public void writeStream( DataOutputStream out ) throws IOException{
			out.writeInt( color.getRGB() );
		}

		public void writeXML( XElement element ){
			element.setInt( color.getRGB() );
		}
	}
	
	/* And this factory creates new CustomMultiDockables and new CustomMultiLayouts when 
	 * the framework needs them. */
	private static class CustomMultiFactory implements MultipleCDockableFactory<CustomMultiDockable, CustomMultiLayout>{
		public CustomMultiLayout create(){
			return new CustomMultiLayout( null );
		}

		public boolean match( CustomMultiDockable dockable, CustomMultiLayout layout ){
			return dockable.getColor().equals( layout.getColor() );
		}

		public CustomMultiDockable read( CustomMultiLayout layout ){
			Color color = layout.getColor();
			String title = "R=" + color.getRed() + ", G=" + color.getGreen() + ", B=" + color.getBlue();
			return new CustomMultiDockable( this, title, color );
		}

		public CustomMultiLayout write( CustomMultiDockable dockable ){
			return new CustomMultiLayout( dockable.getColor() );
		}
	}	
}
