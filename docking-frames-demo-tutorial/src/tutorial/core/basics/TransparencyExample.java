package tutorial.core.basics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import tutorial.support.JTutorialFrame;
import tutorial.support.Tutorial;
import bibliothek.extension.gui.dock.theme.BubbleTheme;
import bibliothek.gui.DockController;
import bibliothek.gui.DockTheme;
import bibliothek.gui.dock.DefaultDockable;
import bibliothek.gui.dock.FlapDockStation;
import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.SplitDockStation;
import bibliothek.gui.dock.station.split.SplitDockGrid;
import bibliothek.gui.dock.themes.ThemeManager;
import bibliothek.gui.dock.util.BackgroundAlgorithm;
import bibliothek.gui.dock.util.BackgroundComponent;
import bibliothek.gui.dock.util.BackgroundPaint;
import bibliothek.gui.dock.util.BackgroundPanel;
import bibliothek.gui.dock.util.ConfiguredBackgroundPanel;
import bibliothek.gui.dock.util.PaintableComponent;
import bibliothek.gui.dock.util.Transparency;

@Tutorial( id="Transparency", title="Transparency" )
public class TransparencyExample {
	/* (Almost) all components of the framework use a special strategy to paint their content. Clients
	 * can replace that algorithm and paint the components in any way the like. They can also configure
	 * most of the components to be transparent. This might not make sense for all components, but this example
	 * shows how to configure all components anyway */
	public static void main( String[] args ) throws IOException{
		/* setting up frame and controller as usual */
		JTutorialFrame frame = new JTutorialFrame( TransparencyExample.class );
		
		DockController controller = new DockController();
		controller.setRootWindow( frame );
		frame.destroyOnClose( controller );
		
		/* The background image we are going to use, it will shine through the application */
		BufferedImage image = ImageIO.read( BackgroundPanel.class.getResource( "/data/tutorial/shadowsAndLight_GregMartin.jpg" ) );

		ImagePanel contentPane = new ImagePanel( image );
		frame.add( contentPane );
		
		/* There are several possibilities how to apply our custom background. The easiest one is to use the properties
		 * to replace the default strategy. */ 
		controller.getProperties().set( DockTheme.BACKGROUND_PAINT, new CustomPaint() );
		
		/* By accessing the ThemeManager we can set a strategy that is used by only one type of component. In this case
		 * we set a custom identifier which will later be used by our "BackgroundDockable" which is shown later in this
		 * example. */
		controller.getThemeManager().setBackgroundPaint( ThemeManager.BACKGROUND_PAINT + ".custom", new CustomPaint() );
		
		/* We can apply different themes. Depending on the theme we might need to make some additional addjustements
		 * to create a good looking application. */
	//	controller.setTheme( new EclipseTheme() );
		controller.setTheme( new BubbleTheme() );
	//	controller.setTheme( new BasicTheme() );
	//	controller.setTheme( new FlatTheme() );
		
		/* And now we set up different DockStations and Dockables */
		SplitDockStation splitDockStation = new SplitDockStation();
		controller.add( splitDockStation );
		contentPane.add( splitDockStation, BorderLayout.CENTER );
		
		SplitDockGrid grid = new SplitDockGrid();
		
		grid.addDockable(  0,  0, 100, 20, new BackgroundDockable( "Red", Color.RED ));
		grid.addDockable(  0, 20,  30, 50, new BackgroundDockable( "Blue", Color.BLUE ));
		grid.addDockable(  0, 70,  30, 30, new BackgroundDockable( "Yellow", Color.YELLOW ));
		grid.addDockable( 30, 20,  80, 80, new BackgroundDockable( "White", Color.WHITE ));
		grid.addDockable( 30, 20,  80, 80, new BackgroundDockable( "Black", Color.BLACK ));
		
		splitDockStation.dropTree( grid.toTree() );
		
		FlapDockStation flapDockStation = new FlapDockStation();
		controller.add( flapDockStation );
		flapDockStation.add( new BackgroundDockable( "Green", Color.GREEN ));
		contentPane.add( flapDockStation.getComponent(), BorderLayout.NORTH );
		
		ScreenDockStation screenDockStation = new ScreenDockStation( controller.getRootWindowProvider() );
		controller.add( screenDockStation );
		
		/* Now we make all frames and windows visible. */
		frame.setVisible( true );
		screenDockStation.setShowing( true );
	}
	
	/* We use this JPanel to paint an interesting background */
	private static class ImagePanel extends JPanel{
		private BufferedImage image;
		
		public ImagePanel( BufferedImage image ){
			super( new BorderLayout() );
			this.image = image;
		}
		
		@Override
		protected void paintComponent( Graphics g ){
			super.paintComponent( g );
			
			int width = getWidth();
			int height = getHeight();
			
			int imageWidth = image.getWidth();
			int imageHeight = image.getHeight();
			
			int x = (imageWidth - width) / 2;
			int y = (imageHeight - height) / 2;
			
			g.drawImage( image, 0, 0, width, height, x, y, x+width, y+height, this );
		}
	}
	
	/* This is our custom painting algorithm. We do not actually paint something new, we just use the interface
	 * to configure the existing components. */
	private static class CustomPaint implements BackgroundPaint{
		public void install( BackgroundComponent component ){
			/* Any component that is using this algorithm is configured to be transparent */
			component.setTransparency( Transparency.TRANSPARENT );	
		}
		
		public void uninstall( BackgroundComponent component ){
			/* We should undo a configuration once we no longer manage a component */
			component.setTransparency( Transparency.DEFAULT );
		}

		public void paint( BackgroundComponent background, PaintableComponent paintable, Graphics g ){
			// we do not need to do anything here
		}
	}
	
	/* This is a specialized Dockable, it contains a single panel. If that panel would not be transparent, then
	 * it would show a color. */
	private static class BackgroundDockable extends DefaultDockable{
		private BackgroundPanel panel;
		private BackgroundAlgorithm background;
		
		public BackgroundDockable( String title, Color color ){
			super( title );
		
			/* We use a ConfiguredBackgroundPanel: it already offers methods to use a replaceable strategy
			 * for painting and supports transparency. */
			panel = new ConfiguredBackgroundPanel( Transparency.SOLID );
			panel.setBackground( color );
			add( panel );
			
			/* And we need a connection between our panel and the framework. The connection has a 
			 * type (called "kind") and a unique identifier. The identifier matches the one identifier we
			 * used to register our speical background algorithm. */
			background = new BackgroundAlgorithm( BackgroundComponent.KIND.append( "custom" ), ThemeManager.BACKGROUND_PAINT + ".custom" ){
				public Component getComponent(){
					return panel;
				}
			};
			panel.setBackground( background );
		}
		
		@Override
		public void setController( DockController controller ){
			super.setController( controller );
			/* The connection between panel and framework needs to know the current DockController in
			 * order to work. */
			background.setController( controller );
		}
	}
}
