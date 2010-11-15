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
		/* Perspectives  */
		
		JTutorialFrame frame = new JTutorialFrame( PerspectivesIntroduction.class );
		CControl control = new CControl( frame );
		frame.destroyOnClose( control );
		frame.add( control.getContentArea(), BorderLayout.CENTER );
	
		ColorFactory colorFactory = new ColorFactory();
		control.addSingleDockableFactory( colorFactory, colorFactory );
		
		CControlPerspective perspectives = control.getPerspectives();
		CPerspective perspective = perspectives.createEmptyPerspective();
		
	    CGridPerspective center = perspective.getContentArea().getCenter();
	    center.gridAdd( 0, 0, 50, 50, new SingleCDockablePerspective( "Red" ) );
	    center.gridAdd( 50, 0, 50, 50, new SingleCDockablePerspective( "Green" ) );
	    center.gridAdd( 0, 50, 50, 50, new SingleCDockablePerspective( "Blue" ) );
	    
	    CStackPerspective stack = new CStackPerspective();
	    stack.add( new SingleCDockablePerspective( "White" ) );
	    stack.add( new SingleCDockablePerspective( "Black" ) );
	    stack.setSelection( stack.getDockable( 0 ) );
	    center.grid().addDockable( 50, 50, 50, 50, stack );
	    
	    perspective.getContentArea().getWest().add( new SingleCDockablePerspective( "Yellow" ) );
	    
	    perspectives.setPerspective( "example", perspective );
	    
		control.load( "example" );
	    
	    frame.setVisible( true );
	}
	
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
