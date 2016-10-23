package tutorial.core.guide;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.displayer.DisplayerRequest;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.DisplayerFactoryValue;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;
import bibliothek.gui.dock.util.Priority;
import bibliothek.gui.dock.util.UIBridge;
import bibliothek.gui.dock.util.UIValue;

@Tutorial(title="DockableDisplayer", id="Displayers")
public class DisplayerExample {
	public static void main( String[] args ){
		/* Each DockStation puts a DockableDisplayer between itself and a Dockable. This
		 * DockableDisplayer is responsible for painting decorations like a title or
		 * a border.
		 * 
		 * DockableDisplayers are created by DisplayerFactories, and clients can replace
		 * that factory. In this example we create a custom displayer with another
		 * border.
		 * */
		
		/* Set up a frame, a station and some Dockables */
		JTutorialFrame frame = new JTutorialFrame( DisplayerExample.class );

		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/*
		 * DisplayerFactories are derived from DockThemes. There are different ways how to change the properties 
		 * of a DockTheme. A straight forward solution would be to just create a theme and use some of its setter
		 * methods, like this:
				
				FlatTheme theme = new FlatTheme();
				theme.setDisplayerFactory( new CustomDisplayerFactory( Color.BLUE ) );
				theme.setSplitDisplayFactory( new CustomDisplayerFactory( Color.RED ) );
				controller.setTheme( new FlatTheme() );
				
		 * In the current case however we are going to use the ThemeManager to override a property independent from the
		 * current theme.
		 * DockStations install instances of "UIValue" at the ThemeManager to read properties. The ThemeManager on the
		 * other hand uses "UIBridge"s to forward properties from theme to station. We can create our own bridge
		 * and override the standard bridge.  
		 */
		
		/* we make use of the existing class StationThemeItemBridge which already implements some methods */
		UIBridge<DisplayerFactory, UIValue<DisplayerFactory>> bridge = new UIBridge<DisplayerFactory, UIValue<DisplayerFactory>>(){
			/* we are going to use a red and a blue border */
			private DisplayerFactory red = new CustomDisplayerFactory( Color.RED );
			private DisplayerFactory blue = new CustomDisplayerFactory( Color.BLUE );
			
			/* this method is called when a property has to be transferred. Normally a bridge calls "uiValue.set( value )", but
			 * we are free to forward our own objects */
			public void set( String id, DisplayerFactory value, UIValue<DisplayerFactory> uiValue ){
				/* the identifier of any property used by a SplitDockStation ends with ".split" */
				if( id.endsWith( ".split" )){
					uiValue.set( red );
				}
				else{
					uiValue.set( blue );
				}
			}
			
			public void add( String id, UIValue<DisplayerFactory> uiValue ){
				// ignore
			}
			
			public void remove( String id, UIValue<DisplayerFactory> uiValue ){
				// ignore	
			}
		};
		
		/* once the bridge is created we can install it with a high priority */
		controller.getThemeManager().publish( Priority.CLIENT, DisplayerFactoryValue.KIND_DISPLAYER_FACTORY, ThemeManager.DISPLAYER_FACTORY_TYPE, bridge );

		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station, BorderLayout.CENTER );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, new ColorDockable( "White", Color.WHITE ) );
		grid.addDockable( 1, 0, 1, 1, new ColorDockable( "Black", Color.BLACK ) );
		station.dropTree( grid.toTree() );
		
		
		frame.setVisible( true );
	}

	/* This factory creates new CustomDockableDisplayers. */
	private static class CustomDisplayerFactory implements DisplayerFactory{
		private Color color;
		
		public CustomDisplayerFactory( Color color ){
			this.color = color;
		}
		
		public void request( DisplayerRequest request ){
			Dockable dockable = request.getTarget();
	    	DockStation station = request.getParent();
	    	DockTitle title = request.getTitle();
	    	
			CustomDockableDisplayer displayer = new CustomDockableDisplayer( color, station, dockable, title );
			
			/* Tell the displayer how to handle the border-hint created by its Dockable.
			 * In this case the border is shown in almost any case. */
	        displayer.setDefaultBorderHint( true );
	        displayer.setRespectBorderHint( true );
	        displayer.setSingleTabShowInnerBorder( true );
	        displayer.setSingleTabShowOuterBorder( true );
	        
	        request.answer( displayer );
		}
	}
	
	/* This displayer shows a custom border. */
	private static class CustomDockableDisplayer extends BasicDockableDisplayer{
		private Color color;
		
		public CustomDockableDisplayer( Color color, DockStation station, Dockable dockable, DockTitle title ){
			super( station, dockable, title );
			this.color = color;
		}
		
		@Override
		protected Border getDefaultBorder(){
			/* And at this location we create our custom border */
			return BorderFactory.createLineBorder( color, 5 );
		}
	}
}
