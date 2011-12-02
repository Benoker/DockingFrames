package tutorial.common.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import tutorial.support.ColorSingleCDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.SingleCDockableFactory;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.CStackPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.util.Filter;

@Tutorial(title="Perspectives (Introduction)", id="PerspectivesIntroduction")
public class PerspectivesIntroduction {
	public static void main( String[] args ){
		/* Perspectives allow clients to modify and create layouts, the location, size
		 * and relation of Dockables.
		 * In this example we set up the layout of an application using only perspectives.
		 * 
		 * Perspectives act as wrapper around a CControl, so in order to access them
		 * we first need to set up a CControl. */
		
		JTutorialFrame frame = new JTutorialFrame( PerspectivesIntroduction.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		
		/* Because perspectives copy some settings from the CControl, it is a good idea 
		 * to first register any factories and register any root-stations before accessing
		 * the perspectives.  */
		ColorFactory colorFactory = new ColorFactory();
		control.addSingleDockableFactory( colorFactory, colorFactory );
		frame.add( control.getContentArea(), BorderLayout.CENTER );
		
		/* A CControlPerspective is a view of a CControl through the eyes of the 
		 * perspective-API. It can only be accessed by calling "getPerspectives". */
		CControlPerspective perspectives = control.getPerspectives();
		
		/* Since we want to create a new layout, we need to create a new perspective. */
		CPerspective perspective = perspectives.createEmptyPerspective();
		
		/* The perspective-API offers different views for different elements of the Common-API.
		 * The CGridPerspective represents a CGridArea, in this case the one area that is in
		 * the middle of our JFrame. */
	    CGridPerspective center = perspective.getContentArea().getCenter();
	    /* The CGridPerspective works just like a CGrid. We add views of SingleCDockables.
	     * The only information that is stored of a SingleCDockable is its unique identifier,
	     * so there is no need to create any subclasses or set any properties other than 
	     * the id. */
	    center.gridAdd( 0, 0, 50, 50, new SingleCDockablePerspective( "Red" ) );
	    center.gridAdd( 50, 0, 50, 50, new SingleCDockablePerspective( "Green" ) );
	    center.gridAdd( 0, 50, 50, 50, new SingleCDockablePerspective( "Blue" ) );
	    
	    /* If we want to combine several dockable we can create a CStackPerspective. */
	    CStackPerspective stack = new CStackPerspective();
	    stack.add( new SingleCDockablePerspective( "White" ) );
	    stack.add( new SingleCDockablePerspective( "Black" ) );
	    stack.setSelection( stack.getDockable( 0 ) );
	    /* Like many classes in Common, CGridPerspective is a wrapper around a class of Core.
	     * In this case the underlying class can be accessed with "grid()" and can be
	     * modified directly. */
	    center.grid().addDockable( 50, 50, 50, 50, stack );
	    
	    /* And we can access other areas than the center area as well, for example the
	     * minimize-area at the west side. */
	    perspective.getContentArea().getWest().add( new SingleCDockablePerspective( "Yellow" ) );
	    
	    /* By storing the perspective with an identifier we can later access it again. */
	    perspectives.setPerspective( "example", perspective );
	    
	    /* After storing the perspective we can load it from the CControl */
		control.load( "example" );
	    
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
}
