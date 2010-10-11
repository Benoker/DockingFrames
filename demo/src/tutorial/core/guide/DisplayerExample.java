package tutorial.core.guide;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.border.Border;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockStation;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.DisplayerFactory;
import bibliothek.gui.dock.station.DockableDisplayer;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.basic.BasicDockableDisplayer;
import bibliothek.gui.dock.title.DockTitle;

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
		
		SplitDockStation station = new SplitDockStation();
		controller.add( station );
		frame.add( station, BorderLayout.CENTER );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 1, 1, new ColorDockable( "White", Color.WHITE ) );
		grid.addDockable( 1, 0, 1, 1, new ColorDockable( "Black", Color.BLACK ) );
		station.dropTree( grid.toTree() );
		
		/* In order to set the factory we create a new theme... */
		FlatTheme theme = new FlatTheme();
		/* ... and set two factories because the FlatTheme uses two factories for
		 * different purposes */
		theme.setDisplayerFactory( new CustomDisplayerFactory( Color.BLUE ) );
		theme.setSplitDisplayFactory( new CustomDisplayerFactory( Color.RED ) );
		controller.setTheme( theme );
		
		frame.setVisible( true );
	}

	/* This factory creates new CustomDockableDisplayers. */
	private static class CustomDisplayerFactory implements DisplayerFactory{
		private Color color;
		
		public CustomDisplayerFactory( Color color ){
			this.color = color;
		}
		
		public DockableDisplayer create( DockStation station, Dockable dockable, DockTitle title ){
			CustomDockableDisplayer displayer = new CustomDockableDisplayer( color, station, dockable, title );
			
			/* Tell the displayer how to handle the border-hint created by its Dockable.
			 * In this case the border is shown in almost any case. */
	        displayer.setDefaultBorderHint( true );
	        displayer.setRespectBorderHint( true );
	        displayer.setSingleTabShowInnerBorder( true );
	        displayer.setSingleTabShowOuterBorder( true );
	        
	        return displayer;
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
