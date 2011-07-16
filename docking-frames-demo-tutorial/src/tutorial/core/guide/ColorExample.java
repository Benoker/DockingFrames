package tutorial.core.guide;

import java.awt.Color;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.color.TitleColor;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.color.ColorBridge;
import bibliothek.gui.dock.util.color.ColorManager;
import bibliothek.gui.dock.util.color.DockColor;

@Tutorial( id="UIColor", title="UI Properties: Colors" )
public class ColorExample {
	/* The ColorManager allows clients to easily replace any color that is used by the framework.
	 * 
	 * Some keys for the colors can be found in the different implementations of ColorScheme. */
	
	public static void main( String[] args ){
		/* setting up a frame, station and controller */
		JTutorialFrame frame = new JTutorialFrame( ColorExample.class );
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station );
		
		/* get the ColorManager... */
		ColorManager colors = controller.getColors();
		/* ... and tell the framework to use red for painting drag and drop gestures. */
		colors.put( Priority.CLIENT, "paint.divider", Color.RED );
		colors.put( Priority.CLIENT, "paint.line", Color.RED );
		colors.put( Priority.CLIENT, "paint.insertion", Color.RED );
		
		/* And this is how a more complex rule is installed */
		colors.publish( Priority.CLIENT, TitleColor.KIND_TITLE_COLOR, new CustomColorBridge() );
		
		/* setting up some dockables to demonstrate the effects */
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, new ColorDockable( "Red", Color.RED.brighter() ) );
		grid.addDockable( 0, 1, 1, 1, new ColorDockable( "Green", Color.GREEN.brighter() ) );
		grid.addDockable( 1, 0, 1, 1, new ColorDockable( "Blue", Color.BLUE.brighter() ) );
		grid.addDockable( 1, 1, 1, 1, new ColorDockable( "Yellow", Color.YELLOW.brighter() ) );
		
		station.dropTree( grid.toTree() );
		
		frame.setVisible( true );
	}
	
	/* This is a filter letting the DockTitles use colors that depend on the Dockable 
	 * which they represent. */
	private static class CustomColorBridge implements ColorBridge{
		public void add( String id, DockColor uiValue ){
			// ignore
		}

		public void remove( String id, DockColor uiValue ){
			// ignore
		}

		public void set( String id, Color value, DockColor uiValue ){
			/* Because we published this filter with the path "TitleColor.KIND_TITLE_COLOR", this
			 * cast is safe. */
			TitleColor title = (TitleColor)uiValue;
			Dockable dockable = title.getTitle().getDockable();
			if( dockable instanceof ColorDockable ){
				Color base = ((ColorDockable)dockable).getColor();
				
				if( "title.active.left".equals( id )){
					uiValue.set( base );
				}
				else if( "title.active.right".equals( id )){
					uiValue.set( Color.WHITE );
				}
				else if( "title.inactive.left".equals( id )){
					uiValue.set( base.darker() );
				}
				else if( "title.inactive.right".equals( id )){
					uiValue.set( Color.LIGHT_GRAY );
				}
				else{
					uiValue.set( value );
				}
			}
			else{
				uiValue.set( value );
			}
		}
	}
}
