package tutorial.common.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import tutorial.support.ColorIcon;
import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;
import bibliothek.gui.dock.common.MultipleCDockableFactory;
import bibliothek.gui.dock.common.MultipleCDockableLayout;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CWorkingPerspective;
import bibliothek.gui.dock.common.perspective.MultipleCDockablePerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.util.Filter;
import bibliothek.util.xml.XElement;

@Tutorial( title="Perspectives (Multiple Dockables)", id="PerspectivesMultiple")
public class PerspectivesMulti {
	/* Perspectives support SingleCDockables and MultipleCDockables. This example sets up
	 * a CWorkingArea with some dockables on it. */
	
	/* Since we are going to work with MultipleCDockables we will need some factory and 
	 * an unique identifier for this factory. That would be this constant. */
	public static final String CUSTOM_MULTI_FACTORY_ID = "custom";
	
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( PerspectivesMulti.class );
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
		
		/* Now we just drop some random SingleCDockables onto the center area */
	    CGridPerspective center = perspective.getContentArea().getCenter();
	    center.gridAdd( 0, 0, 50, 50, new SingleCDockablePerspective( "Red" ) );
	    center.gridAdd( 50, 0, 50, 50, new SingleCDockablePerspective( "Green" ) );
	    center.gridAdd( 50, 0, 50, 50, new SingleCDockablePerspective( "Blue" ) );
	    
	    /* because we called "control.createWorkingArea" we can now access the 
	     * CWorkingPerspective with the same unique identifier. We could also just
	     * create a new CWorkingPerspective and use "addRoot" to store it. */
	    CWorkingPerspective work = (CWorkingPerspective)perspective.getStation( "work" );
	    center.gridAdd( 0, 50, 100, 100, work );
	    
	    for( int i = 0; i < 5; i++ ){
	    	/* To add MultipleCDockales we only need the unique identifier of their factory,
	    	 * and their content. The content is an object of type MultipleCDockableLayout.
	    	 * 
	    	 * Btw. by using the same position and size for all dockables we can easily 
	    	 * stack them. */
	    	CustomMultiLayout layout = new CustomMultiLayout( new Color( 20*i, 50, 0 ));
	    	work.gridAdd( 0, 0, 100, 100, new MultipleCDockablePerspective( CUSTOM_MULTI_FACTORY_ID, layout ));
	    }
	    
	    /* Finally we apply the perspective we just created */
		perspectives.setPerspective( perspective, true );
	    
	    frame.setVisible( true );
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
			setTitleIcon( new ColorIcon( color ) );
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
