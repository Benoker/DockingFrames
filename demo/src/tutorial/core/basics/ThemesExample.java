package tutorial.core.basics;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tutorial.support.ColorDockable;
import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.extension.gui.dock.theme.EclipseTheme;
import bibliothek.extension.gui.dock.theme.FlatTheme;
import bibliothek.extension.gui.dock.theme.SmoothTheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme;
import bibliothek.extension.gui.dock.theme.bubble.BubbleColorScheme.Distribution;
import bibliothek.gui.DockController;
import bibliothek.gui.Dockable;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.ColorScheme;
import bibliothek.gui.dock.themes.NoStackTheme;

@Tutorial(title="Themes", id="Themes")
public class ThemesExample {
	/* By now you probably like the basic design of this framework, but the user interface
	 * looks ugly to you. Well, you are not alone, and that is the reason why there are themes.
	 * A theme tells the framework how to paint and behave, it can be changed at any time. */
	
	public static void main( String[] args ){
		JTutorialFrame frame = new JTutorialFrame( ThemesExample.class );
		
		/* In this example we create four different themes and show them at the
		 * same time in one frame. You already know how the BasicTheme looks like,
		 * so we won't show it again. */
		
		JPanel examples = new JPanel( new GridLayout( 2, 2 ));
		examples.add( createThemePanel( "SmoothTheme", setupSmoothTheme( frame )));
		examples.add( createThemePanel( "FlatTheme", setupFlatTheme( frame )));
		examples.add( createThemePanel( "BubbleTheme", setupBubbleTheme( frame )));
		examples.add( createThemePanel( "EclipseTheme", setupEclipseTheme( frame )));
		frame.add( examples );
		
		frame.setVisible( true );
	}
	
	private static Component setupSmoothTheme( JTutorialFrame frame ){
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* Setting a theme is easy: calling "setTheme" will set the theme 
		 * immediately on any known component. 
		 * The SmoothTheme is basically the same as the BasicTheme, except
		 * that the titles change their color smoothly. We pack the
		 * SmootTheme into a NoStackTheme, the NoStackTheme will ensure that
		 * the user cannot drop a StackDockStation into another StackDockStation,
		 * it also removes the titles of dockable DockStations. */
		controller.setTheme( new NoStackTheme( new SmoothTheme() ));
		
		SplitDockStation station = createExampleStation();
		controller.add( station );
		return station;
	}
	
	private static Component setupFlatTheme( JTutorialFrame frame ){
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* The FlatTheme uses as few borders as possible, otherwise it is
		 * identical to the BasicTheme. */
		controller.setTheme( new NoStackTheme( new FlatTheme() ));
		
		SplitDockStation station = createExampleStation();
		controller.add( station );
		return station;
	}
	
	private static Component setupBubbleTheme( JTutorialFrame frame ){
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* The BubbleTheme uses a lot of animations to visualize the different states
		 * a Dockable may have (mainly focused and selected). */
		controller.setTheme( new NoStackTheme( new BubbleTheme() ));
		
		/* Themes can be customized, all of them offer some "set..." methods. Some options
		 * are not stored directly in the theme-object but in the property-map.
		 * 
		 * There are many more things we can change: how buttons are painted, what titles are
		 * used, how tabs are painted and where they are... some of these things will be 
		 * shown by later examples
		 * 
		 * In this case we change the colors used by the BubbleTheme. For simplicity we
		 * use a predefined set of colors (normally titles would appear in red, now they
		 * appear in blue). */
		ColorScheme colors = new BubbleColorScheme( Distribution.BRG );
		
		/* Once we decided what colors to use, we put them into the property-map */
		controller.getProperties().set( BubbleTheme.BUBBLE_COLOR_SCHEME, colors );
		
		SplitDockStation station = createExampleStation();
		controller.add( station );
		return station;	
	}
	
	private static Component setupEclipseTheme( JTutorialFrame frame ){
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* The EclipseTheme imitates the look and feel of the famous Eclipse IDE. */
		controller.setTheme( new EclipseTheme() );
		
		SplitDockStation station = createExampleStation();
		controller.add( station );
		return station;
	}
	
	/* This method creates a JLabel and puts it together with "example" on a JPanel. */
	private static JPanel createThemePanel( String title, Component example ){
		JPanel panel = new JPanel( new GridBagLayout() );
		Insets insets = new Insets( 5, 5, 5, 5 );
		panel.add( new JLabel( title ), new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, insets, 0, 0 ));
		panel.add( example, new GridBagConstraints( 0, 1, 1, 1, 1.0, 1000.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, insets, 0, 0 ));
		panel.setBorder( BorderFactory.createLineBorder( Color.BLACK, 2 ));
		return panel;
	}
	
	/* Just creating a SplitDockStation with some Dockables to demonstrate the look 
	 * of a theme  */
	private static SplitDockStation createExampleStation(){
		SplitDockStation station = new SplitDockStation();
		
		Dockable red = new ColorDockable( "Red", Color.RED, 2.5f );
		Dockable green =  new ColorDockable( "Green", Color.GREEN, 2.5f );
		Dockable blue = new ColorDockable( "Blue", Color.BLUE, 2.5f );		
		Dockable yellow = new ColorDockable( "Yellow", Color.YELLOW, 2.5f );
		Dockable cyan = new ColorDockable( "Cyan", Color.CYAN, 2.5f );
		
		SplitDockGrid grid = new SplitDockGrid();
		grid.addDockable( 0, 0, 60, 100, red, green, blue );
		grid.setSelected( 0, 0, 60, 100, green );
		grid.addDockable( 60, 0, 40, 30, yellow );
		grid.addDockable( 60, 30, 40, 70, cyan );
		
		station.dropTree( grid.toTree() );
		
		return station;
	}
}
